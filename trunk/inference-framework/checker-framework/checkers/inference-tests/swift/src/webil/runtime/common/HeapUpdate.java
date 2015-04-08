package webil.runtime.common;

import java.util.HashMap;
import java.util.Map;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * An update of the runtime type information in the heap Used in conjunction
 * with the Object Store
 */
public class HeapUpdate implements IsSerializable {

    /**
     * Map of object IDs to runtime type information, represented as the
     * continuation ID of the constructor to call
     * 
     */
    Map<webil.runtime.common.ObjectID, java.lang.Integer> objectTypes;

    public HeapUpdate() {
        objectTypes = new HashMap();
    }
    
    public String toString() {
        return objectTypes.toString();
    }

}
