package com.hunterwb.servicescanner;

import javax.annotation.processing.Completion;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;
import javax.tools.StandardLocation;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Collections;
import java.util.Set;

/**
 * A <i>universal processor</i> as described in {@link Processor} which supports processing all types.
 */
abstract class UniversalProcessor implements Processor {

    private ProcessingEnvironment processingEnv;

    @Override
    public final void init(ProcessingEnvironment processingEnv) {
        this.processingEnv = processingEnv;
        init();
    }

    void init() {}

    @Override
    public final Set<String> getSupportedAnnotationTypes() {
        return Collections.singleton("*");
    }

    @Override
    public final SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public final Iterable<? extends Completion> getCompletions(Element element, AnnotationMirror annotation, ExecutableElement member, String userText) {
        return Collections.emptyList();
    }

    @Override
    public final boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        if (roundEnv.processingOver()) {
            end();
        } else {
            process(roundEnv);
        }
        return false;
    }

    void process(RoundEnvironment roundEnv) {}

    void end() {}

    final Elements elements() {
        return processingEnv.getElementUtils();
    }

    final Types types() {
        return processingEnv.getTypeUtils();
    }

    final String option(String option) {
        return processingEnv.getOptions().get(option);
    }

    final void log(Diagnostic.Kind kind, String message) {
        processingEnv.getMessager().printMessage(kind, message);
    }

    final boolean fileExists(CharSequence name) {
        try {
            processingEnv.getFiler().getResource(StandardLocation.CLASS_OUTPUT, "", name).openInputStream().close();
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    final OutputStream openFileOutput(CharSequence name) throws IOException {
        return processingEnv.getFiler().createResource(StandardLocation.CLASS_OUTPUT, "", name).openOutputStream();
    }
}
