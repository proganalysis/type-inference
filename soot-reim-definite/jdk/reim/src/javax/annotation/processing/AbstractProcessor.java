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
    @PolyreadThis public @Polyread Set<String> getSupportedOptions()  { throw new RuntimeException("skeleton method"); }
    @PolyreadThis public @Polyread Set<String> getSupportedAnnotationTypes()  { throw new RuntimeException("skeleton method"); }
    @PolyreadThis public @Polyread SourceVersion getSupportedSourceVersion()  { throw new RuntimeException("skeleton method"); }
    public synchronized void init(ProcessingEnvironment processingEnv) { throw new RuntimeException("skeleton method"); }
    public abstract boolean process(Set<? extends TypeElement> annotations,
                    RoundEnvironment roundEnv);
    @PolyreadThis public @Polyread Iterable<? extends Completion> getCompletions( Element element,
                             AnnotationMirror annotation,
                             ExecutableElement member,
                             String userText) { throw new RuntimeException("skeleton method"); }
    @ReadonlyThis protected synchronized boolean isInitialized()  { throw new RuntimeException("skeleton method"); }
}
