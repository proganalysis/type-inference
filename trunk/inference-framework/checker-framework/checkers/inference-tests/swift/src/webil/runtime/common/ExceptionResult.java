package webil.runtime.common;

/*
 * Denotes exceptional termination of a method
 */
public class ExceptionResult extends ClosureResult {
    public final Throwable exceptionThrown;
    public ExceptionResult(Throwable exceptionThrown) {
        this.exceptionThrown = exceptionThrown;
    }
}
