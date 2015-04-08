package webil.runtime.common;

public class ExceptionClosure extends Closure {
    public String exceptionType;

    public ExceptionClosure(int continuationID, StackFrameID stackFrameID, String exceptionType) {
        super(continuationID, stackFrameID);
        this.exceptionType = exceptionType;
    }

}
