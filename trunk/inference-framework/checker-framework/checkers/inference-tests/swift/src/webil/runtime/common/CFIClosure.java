package webil.runtime.common;

import java.util.List;

/**
 * This closure will be triggered either by a return from a method, or by an exit from a try block
 *
 */
public class CFIClosure extends Closure {

//    final static int CFIContinuationID = -100; // XXX Think of a better constant here
    public List allowedEntries;
    public List disallowedEntries;
    public ObjectID exceptionThrown; // will be set when the exception is actually thrown
    // a null value means that normal exit happened
    
    /**
     * 
     * @param stackFrameID
     * The stack frame ID of the stack frame of the caller, i.e. the method body in which
     * this CFIClosure is inserted
     * 
     * @param possibleEntries
     * A List of String objects, each representing the runtime type of an Exception that could
     * be thrown from the scope associated with this CFIClosure (aka MTC). The case of a normal
     * exit from this scope is represented as a special string "normal"
     */
    public CFIClosure(int continuationID, StackFrameID stackFrameID, List allowedEntries, List disallowedEntries) {
        super(continuationID, stackFrameID);
        this.allowedEntries = allowedEntries;
        this.disallowedEntries = disallowedEntries;
    }

}
