package webil.runtime.common;

import com.google.gwt.user.client.rpc.IsSerializable;

public class ExternalJavaObject implements IsSerializable {
    
    protected ObjectID objectID;

    public ExternalJavaObject(ObjectID objectID) {
        this.objectID = objectID;
    }

    /** Default constructor needed for GWT serializability. Don't use otherwise.
      * @deprecated */
    public ExternalJavaObject() {}

}
