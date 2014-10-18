package javax.annotation.processing;

import java.util.Set;
import java.util.HashSet;
import java.util.Collections;
import javax.lang.model.element.*;
import javax.lang.model.SourceVersion;
import javax.tools.Diagnostic;

import checkers.inference.reim.quals.*;

public abstract class AbstractProcessor implements Processor {
    protected ProcessingEnvironment processingEnv;
    protected AbstractProcessor() { throw new RuntimeException("skeleton method"); }
    public @Polyread Set<String> getSupportedOptions(@Polyread AbstractProcessor this)  { throw new RuntimeException("skeleton method"); }
    public @Polyread Set<String> getSupportedAnnotationTypes(@Polyread AbstractProcessor this)  { throw new RuntimeException("skeleton method"); }
    public @Polyread SourceVersion getSupportedSourceVersion(@Polyread AbstractProcessor this)  { throw new RuntimeException("skeleton method"); }
    public synchronized void init(ProcessingEnvironment processingEnv) { throw new RuntimeException("skeleton method"); }
    public abstract boolean process(Set<? extends TypeElement> annotations,
                    RoundEnvironment roundEnv);
    public @Polyread Iterable<? extends Completion> getCompletions(@Polyread AbstractProcessor this, Element element,
                             AnnotationMirror annotation,
                             ExecutableElement member,
                             String userText) { throw new RuntimeException("skeleton method"); }
    protected synchronized boolean isInitialized(@Readonly AbstractProcessor this)  { throw new RuntimeException("skeleton method"); }
}
