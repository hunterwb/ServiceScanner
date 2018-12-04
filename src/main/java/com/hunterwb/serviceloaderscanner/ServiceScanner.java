package com.hunterwb.serviceloaderscanner;

import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic;
import java.io.*;
import java.nio.charset.Charset;
import java.util.*;

public final class ServiceScanner extends Scanner {

    private final Map<String, Set<String>> serviceProviders = new TreeMap<String, Set<String>>();

    @Override
    public Set<String> getSupportedOptions() {
        return Collections.singleton("services");
    }

    @Override
    public void init() {
        String services = options().get("services");
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
                e.getSimpleName().length() != 0;
    }

    private boolean isSubType(TypeMirror sub, String parentName) {
        if (elements().getBinaryName((TypeElement) types().asElement(sub)).contentEquals(parentName)) return true;
        for (TypeMirror ds : types().directSupertypes(sub)) {
            if (isSubType(ds, parentName)) return true;
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
                    for (String newService : providers) {
                        writer.write(newService);
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
