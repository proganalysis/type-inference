package webil.runtime.common;

import com.google.gwt.user.client.rpc.IsSerializable;

public class ObjectID implements IsSerializable {
    /**
     * Tunable parameter of the length of an object ID
     * Should be long enough that a cryptographically generated
     * value is truly unpredictable even with many objects
     */
    //final static int OBJECTID_LENGTH = 4;
        
    byte[] id;

    /**
     * Default constructor needed for GWT serializability. Don't use otherwise.
     * @deprecated
     */
    public ObjectID() {}
    
    public ObjectID(byte[] id) {
        this.id = id;
    }
    
    public String toString() {
        String byteString = "";
        for (int i = id.length-1; i >= 0 ; i--) {
            byteString += id[i];
            if (i > 0) byteString += '.';
        }
        return "<objectID " + byteString + ">";
    }

    public boolean equals(Object another) {
        if(!(another instanceof ObjectID)) {
            return false;
        }
        ObjectID other = (ObjectID)another;
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

}
