package webil.runtime.common;


public class MethodCallResult extends ClosureResult {
    public MethodCallResult(Closure returnClosure, StackFrame arguments, WilObject receiver, int continuationID) {
        this.returnClosure = returnClosure;
        this.arguments = arguments;
//        this.receiver = receiver;
        this.arguments.self = receiver;
        this.continuationID = continuationID;
    }
	Closure returnClosure; // maybe this field can be avoided
	StackFrame arguments;
//	byte[] receiverID; // maybe this could be part of arguments
//        WilObject receiver;
        int continuationID;
}
