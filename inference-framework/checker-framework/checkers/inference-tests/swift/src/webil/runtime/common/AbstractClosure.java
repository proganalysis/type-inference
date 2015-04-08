package webil.runtime.common;

import com.google.gwt.user.client.rpc.IsSerializable;


public abstract class AbstractClosure extends ClosureResult implements IsSerializable { 
    // travels over the wire
    
    public final static int RETURN = 0;
    public final static int METHCALL = 1;
    public final static int HANDLER = 2;
    public final static int NORMAL = 3;
    public final static int EXTERNAL = 4;
    

    /**
     * Default constructor needed for GWT serializability. Don't use otherwise.
     * @deprecated
     */
    public AbstractClosure() {}
    
    public AbstractClosure(int continuationID, StackFrameID stackFrameID) {
        this.continuationID = continuationID;
        this.stackFrameID = stackFrameID;
//        isHighIntegrity = false;
    }
    
    
    public int continuationID; // the particular ID will tell us which kind of
//  closure this is
    public StackFrameID stackFrameID;
//    public boolean isHighIntegrity;
//    public int type = Closure.NORMAL; // the closure type is in the closure, but this might need to be revisited.
    
    public String toString() {
        return "{Execution Block: "  + continuationID + ", Activation Record: " + stackFrameID + "}";  
    }
    
    public boolean equals(Object o) {
        if (o instanceof AbstractClosure) {
            AbstractClosure other = (AbstractClosure) o;
            return continuationID == other.continuationID && 
                stackFrameID.equals(other.stackFrameID);
        } else {
            return false;
        }
    }
    
    public int hashCode() {
        return continuationID ^ stackFrameID.hashCode();
    }
    
}
