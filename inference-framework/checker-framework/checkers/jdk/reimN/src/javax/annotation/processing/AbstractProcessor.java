package javax.annotation.processing;

import java.util.Set;
import java.util.HashSet;
import java.util.Collections;
import javax.lang.model.element.*;
import javax.lang.model.SourceVersion;
import javax.tools.Diagnostic;

import checkers.inference2.reimN.quals.*;

public abstract class AbstractProcessor implements Processor {
    protected ProcessingEnvironment processingEnv;
    protected AbstractProcessor() { throw new RuntimeException("skeleton method"); }
    public @PolyPoly Set<String> getSupportedOptions(@PolyPoly AbstractProcessor this)  { throw new RuntimeException("skeleton method"); }
    public @PolyPoly Set<String> getSupportedAnnotationTypes(@PolyPoly AbstractProcessor this)  { throw new RuntimeException("skeleton method"); }
    public @PolyPoly SourceVersion getSupportedSourceVersion(@PolyPoly AbstractProcessor this)  { throw new RuntimeException("skeleton method"); }
    public synchronized void init(ProcessingEnvironment processingEnv) { throw new RuntimeException("skeleton method"); }
    public abstract boolean process(Set<? extends TypeElement> annotations,
                    RoundEnvironment roundEnv);
    public @PolyPoly Iterable<? extends Completion> getCompletions(@PolyPoly AbstractProcessor this, Element element,
                             AnnotationMirror annotation,
                             ExecutableElement member,
                             String userText) { throw new RuntimeException("skeleton method"); }
    protected synchronized boolean isInitialized(@ReadRead AbstractProcessor this)  { throw new RuntimeException("skeleton method"); }
}
