package webil.runtime.common;

import com.google.gwt.user.client.rpc.IsSerializable;

public class WireCFIClosure extends WireClosure {
    /**
     * Default constructor needed for GWT serializability. Don't use otherwise.
     * @deprecated
     */
    public WireCFIClosure() {}
    
    public WireCFIClosure(int continuationID, StackFrameID stackFrameID, int exceptionClassID) {
        super(continuationID, stackFrameID, null);
        this.exceptionClassID = exceptionClassID;
    }
    
    public int exceptionClassID;

}
