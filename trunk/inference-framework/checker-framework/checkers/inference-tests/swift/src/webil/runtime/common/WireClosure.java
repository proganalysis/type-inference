package webil.runtime.common;

import com.google.gwt.user.client.rpc.IsSerializable;

public class WireClosure extends AbstractClosure implements IsSerializable {
    
    public IsSerializable argument;
    public boolean isCFI;

    /**
     * Default constructor needed for GWT serializability. Don't use otherwise.
     * @deprecated
     */
    public WireClosure() {}
    
    public WireClosure(int continuationID, StackFrameID stackFrameID, IsSerializable argument) {
        super(continuationID, stackFrameID);
        this.argument = argument;
        this.isCFI = false;
    }
    
}
