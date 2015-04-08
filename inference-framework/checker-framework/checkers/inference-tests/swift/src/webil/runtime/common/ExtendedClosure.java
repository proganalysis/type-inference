package webil.runtime.common;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * Just a triple of a closure, stack update and a heap update
 */
public class ExtendedClosure implements IsSerializable {
    public WireClosure incoming;
    StackUpdate stackUpdate;
    HeapUpdate heapUpdate;
    int globalSFID;
    int globalOID;
    public boolean withinStoSC;
    public boolean prevTrusted;

    // used to distinguish different windows in the same session
    public int subSession;

    public ExtendedClosure(WireClosure incoming, StackUpdate stackUpdate,
        HeapUpdate heapUpdate, int globalSFID, int globalOID, int subSession) {
        this.incoming = incoming;
        this.stackUpdate = stackUpdate;
        this.heapUpdate = heapUpdate;
        this.globalOID = globalOID;
        this.globalSFID = globalSFID;
        this.subSession = subSession;
    }

    /**
     * Default constructor needed for GWT serializability. Don't use otherwise.
     * @deprecated
     */
    public ExtendedClosure() {}

    public String toString() {
        return incoming.toString() + ", " + stackUpdate.toString() + ", "
            + heapUpdate.toString();
    }
}
