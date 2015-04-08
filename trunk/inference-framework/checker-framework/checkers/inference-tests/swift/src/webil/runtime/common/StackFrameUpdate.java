package webil.runtime.common;


import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * 
 * @author kvikram
 * Objects of this class are used to send updates of local variables from client
 * to server and vice versa. Assume only int,float and Object types for now.
 *
 */
public class StackFrameUpdate implements IsSerializable { // travels over the wire from client to server
    public StackFrameUpdate(StackFrameID stackFrameID) {
        this.stackFrameID = stackFrameID;
    }

    /**
     * Default constructor needed for GWT serializability. Don't use otherwise.
     * @deprecated
     */
    public StackFrameUpdate() {}

    public ObjectID self;
    public StackFrameID stackFrameID;
//    public FrameIndices indices; // contains meta information about the argument arrays

    public boolean[] booleans;
    public int[] integers;
    public float[] floats;
//    public ObjectID[] objectIDs; // could be immutable, can know only at runtime
    public IsSerializable[] objectIDs; // could be immutable, can know only at runtime
    public IsSerializable[] immutables; // statically known to be immutable
    
    public boolean[] dirtyBooleans;
    public boolean[] dirtyIntegers;
    public boolean[] dirtyFloats;
    public boolean[] dirtyObjectIDs;
    public boolean[] dirtyImmutables;

    
}
