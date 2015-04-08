package webil.runtime.common;

import com.google.gwt.user.client.rpc.IsSerializable;

public class StackFrameID implements IsSerializable {
    /**
     * Similarly, the length of a stack frame ID
     */
    //final static int FRAMEID_LENGTH = 4;

    byte[] id;

    /**
     * Default constructor needed for GWT serializability. Don't use otherwise.
     * @deprecated
     */
    public StackFrameID() {}
    
    public StackFrameID(byte[] id) {
        this.id = id;
    }

    public boolean equals(Object another) {
        if(!(another instanceof StackFrameID)) {
            return false;
        }
        StackFrameID other = (StackFrameID)another;
        if(id.length != other.id.length) {
            return false;
        }
        for(int i = 0; i < id.length; i++) {
            if(id[i] != other.id[i]) {
                return false;
            }
            
        }
        return true;
    }

    public int hashCode() {
        int hash = 5381;
        
        for (int i = 0; i < id.length; i++) {
            hash = (hash << 5) + hash + id[i];
        }
        
        return hash;
    }
    
    public String toString() {
        String byteString = "";
        for (int i = id.length-1; i >= 0 ; i--) {
            byteString += id[i];
            if (i > 0) byteString += '.';
        }
        return "<stackID " + byteString + ">";
    }

}
