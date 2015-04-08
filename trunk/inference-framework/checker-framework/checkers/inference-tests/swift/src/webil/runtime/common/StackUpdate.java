package webil.runtime.common;

import java.util.HashMap;
import java.util.Map;

import com.google.gwt.user.client.rpc.IsSerializable;

public class StackUpdate implements IsSerializable { // travels over the wire

    StackUpdate(int sizeOfStack) {
        stackDelta = new WireClosure[sizeOfStack][];
        framesDelta = new HashMap();
    }

    /**
     * Default constructor needed for GWT serializability. Don't use otherwise.
     * @deprecated
     */
    public StackUpdate() {}

    WireClosure[][] stackDelta; // stack of closures

    Map<webil.runtime.common.StackFrameID, webil.runtime.common.StackFrameUpdate> framesDelta; // updated stack frames (only the shared variables)
    int start;
    
    public String toString() {
        return start + ", " + framesDelta;
    }

}
