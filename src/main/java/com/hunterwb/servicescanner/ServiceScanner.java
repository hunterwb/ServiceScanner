package com.hunterwb.servicescanner;

import javax.annotation.processing.Completion;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;
import javax.tools.StandardLocation;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public final class ServiceScanner implements Processor {

    private final Map<TypeElement, Set<TypeElement>> serviceProviders = new LinkedHashMap<TypeElement, Set<TypeElement>>();

    private ProcessingEnvironment env;

    @Override public Set<String> getSupportedOptions() {
        return Collections.singleton("services");
    }

    @Override public Set<String> getSupportedAnnotationTypes() {
        return Collections.singleton("*");
    }

    @Override public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override public Iterable<? extends Completion> getCompletions(Element element, AnnotationMirror annotation, ExecutableElement member, String userText) {
        return Collections.emptyList();
    }

    @Override public void init(ProcessingEnvironment processingEnv) {
        if (processingEnv == null) throw new NullPointerException();
        if (env != null) throw new IllegalStateException();
        env = processingEnv;
        try {
            init();
        } catch (Exception e) {
            error(getStackTraceAsString(e));
        }
    }

    private void init() {
        String servicesOption = env.getOptions().get("services");
        if (servicesOption == null) {
            warning("No services added. Add services by passing their canonical names to javac in the following format: -Aservices=com.example.Service1,com.example.Service2");
        } else {
            for (String serviceString : servicesOption.split(",")) {
                TypeElement service = env.getElementUtils().getTypeElement(serviceString);
                if (service == null) {
                    error("Cannot find class with canonical name \"" + serviceString + '"');
                } else {
                    serviceProviders.put(service, new HashSet<TypeElement>());
                }
            }
        }
    }

    @Override public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        if (!serviceProviders.isEmpty()) {
            try {
                if (roundEnv.processingOver()) {
                    writeServices();
                } else {
                    processElements(roundEnv.getRootElements());
                }
            } catch (Exception e) {
                error(getStackTraceAsString(e));
            }
        }
        return false;
    }

    private void processElements(Iterable<? extends Element> elements) {
        for (Element e : elements) {
            if (isDeclaredType(e.getKind())) {
                processTypeElement((TypeElement) e);
                processElements(e.getEnclosedElements());
            }
        }
    }

    private void processTypeElement(TypeElement e) {
        if (hasDefaultConstructor(e)) {
            for (TypeElement service : serviceProviders.keySet()) {
                if (isAssignable(e, service)) {
                    serviceProviders.get(service).add(e);
                }
            }
        }
    }

    private void writeServices() throws IOException {
        for (Map.Entry<TypeElement, Set<TypeElement>> e : serviceProviders.entrySet()) {
            writeService(e.getKey(), e.getValue());
        }
    }

    private void writeService(TypeElement service, Set<TypeElement> providers) throws IOException {
        String s = binaryName(service);
        Set<String> ps = new TreeSet<String>();
        for (TypeElement provider : providers) {
            ps.add(binaryName(provider));
        }

        note("Found providers " + ps + " for service " + s);
        if (ps.isEmpty()) return;

        String fileName = "META-INF/services/" + s;
        OutputStream outputStream = env.getFiler().createResource(StandardLocation.CLASS_OUTPUT, "", fileName).openOutputStream();
        IOException e = null;
        try {
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
            for (String p : ps) {
                bw.write(p);
                bw.newLine();
            }
            bw.flush();
        } catch (IOException writeException) {
            e = writeException;
        }
        try {
            outputStream.close();
        } catch (IOException closeException) {
            if (e == null) e = closeException;
        }
        if (e != null) throw e;
    }

    private void note(String msg) {
        env.getMessager().printMessage(Diagnostic.Kind.NOTE, msg);
    }

    private void warning(String msg) {
        env.getMessager().printMessage(Diagnostic.Kind.WARNING, msg);
    }

    private void error(String msg) {
        env.getMessager().printMessage(Diagnostic.Kind.ERROR, msg);
    }

    private String binaryName(TypeElement type) {
        return env.getElementUtils().getBinaryName(type).toString();
    }

    private boolean isAssignable(TypeElement from, TypeElement to) {
        Types types = env.getTypeUtils();
        return types.isSubtype(from.asType(), types.erasure(to.asType()));
    }

    private static boolean isDeclaredType(ElementKind elementKind) {
        return elementKind.isClass() || elementKind.isInterface();
    }

    private static boolean hasDefaultConstructor(TypeElement t) {
        if (!t.getKind().isClass()) return false;
        Set<Modifier> modifiers = t.getModifiers();
        if (!modifiers.contains(Modifier.PUBLIC)) return false;
        if (modifiers.contains(Modifier.ABSTRACT)) return false;
        if (t.getNestingKind().isNested() && !modifiers.contains(Modifier.STATIC)) return false;
        for (Element e : t.getEnclosedElements()) {
            if (e.getKind() == ElementKind.CONSTRUCTOR) {
                ExecutableElement c = (ExecutableElement) e;
                if (c.getModifiers().contains(Modifier.PUBLIC) && c.getParameters().isEmpty()) {
                    return true;
                }
            }
        }
        return false;
    }

    private static String getStackTraceAsString(Throwable throwable) {
        StringWriter stringWriter = new StringWriter();
        throwable.printStackTrace(new PrintWriter(stringWriter));
        return stringWriter.toString();
    }
}
