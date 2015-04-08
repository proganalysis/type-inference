package webil.runtime.common;

public class ReturnResult extends ClosureResult {
	final Object returnValue;
        final boolean isPrimitive;
        public ReturnResult(Object returnValue, boolean isPrimitive) { // the combination null,false indicates a "return null" statement
            this.returnValue = returnValue;
            this.isPrimitive = isPrimitive;
        }
        
        public ReturnResult() { // this is a marker for a return from a void method
            returnValue = null;
            isPrimitive = true;
        }
	// the runtime will just pop the top closure on the stack and execute it
}
