package com.hunterwb.serviceloaderscanner;

import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * Processes all types for {@link ServiceLoader} providers and generates their configuration files.
 * <p>
 * Processor Options:<ul>
 *     <li>services - comma delimited list of the fully qualified binary names of the services to look for</li>
 * </ul>
 */
public final class ServiceScanner extends UniversalProcessor {

    private final Map<String, Set<String>> serviceProviders = new TreeMap<String, Set<String>>();

    @Override
    public Set<String> getSupportedOptions() {
        return Collections.singleton("services");
    }

    @Override
    public void init() {
        String services = option("services");
        if (services == null || services.isEmpty()) {
            log(Diagnostic.Kind.WARNING, "No services configured. Add services by passing their fully qualified binary names to javac in the following format:");
            log(Diagnostic.Kind.WARNING, "-Aservices=com.example.Service1,com.example.Service2");
            return;
        }
        for (String service : services.split(",")) {
            serviceProviders.put(service, new TreeSet<String>());
        }
    }

    @Override
    public void process(RoundEnvironment roundEnv) {
        if (serviceProviders.isEmpty()) return;
        for (Element e : roundEnv.getRootElements()) {
            process(e);
        }
    }

    private void process(Element e) {
        if (isServiceProviderCandidate(e)) {
            for (Map.Entry<String, Set<String>> entry : serviceProviders.entrySet()) {
                String service = entry.getKey();
                Set<String> providers = entry.getValue();

                if (isSubType(e.asType(), service)) {
                    providers.add(elements().getBinaryName((TypeElement) e).toString());
                }
            }
        }
        for (Element enc : e.getEnclosedElements()) {
            process(enc);
        }
    }

    private boolean isServiceProviderCandidate(Element e) {
        return e.getKind() == ElementKind.CLASS &&
                !e.getModifiers().contains(Modifier.ABSTRACT) &&
                e.getSimpleName().length() != 0 &&
                hasDefaultConstructor((TypeElement) e);
    }

    private boolean isSubType(TypeMirror sub, String parentName) {
        if (elements().getBinaryName((TypeElement) types().asElement(sub)).contentEquals(parentName)) return true;
        for (TypeMirror ds : types().directSupertypes(sub)) {
            if (isSubType(ds, parentName)) return true;
        }
        return false;
    }

    private boolean hasDefaultConstructor(TypeElement t) {
        for (Element enclosed : t.getEnclosedElements()) {
            if (enclosed.getKind() != ElementKind.CONSTRUCTOR) continue;
            ExecutableElement constructor = (ExecutableElement) enclosed;
            if (!constructor.getModifiers().contains(Modifier.PUBLIC)) continue;
            if (!constructor.getParameters().isEmpty()) continue;
            return true;
        }
        return false;
    }

    public void end() {
        Charset utf8 = Charset.forName("UTF-8");
        for (Map.Entry<String, Set<String>> entry : serviceProviders.entrySet()) {
            String service = entry.getKey();
            Set<String> providers = entry.getValue();
            log(Diagnostic.Kind.NOTE, "Found service providers " + providers + " for service " + service);
            String serviceFileName = "META-INF/services/" + service;

            if (fileExists(serviceFileName)) {
                log(Diagnostic.Kind.WARNING, "Overwriting file " + serviceFileName);
            }

            try {
                OutputStream out = openFileOutput(serviceFileName);
                try {
                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out, utf8));
                    for (String provider : providers) {
                        writer.write(provider);
                        writer.newLine();
                    }
                    writer.flush();
                } finally {
                    out.close();
                }
            } catch (IOException e) {
                log(Diagnostic.Kind.ERROR, e.toString());
                return;
            }
        }
    }
}
