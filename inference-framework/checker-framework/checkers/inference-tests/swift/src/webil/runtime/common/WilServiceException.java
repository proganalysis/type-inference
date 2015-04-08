package webil.runtime.common;

import com.google.gwt.user.client.rpc.SerializableException;

/**
 * When an exception happens in the runtime and is sent over the wire (to
 * notify the other host), it is sent as a WilServiceException.
 */
public class WilServiceException extends SerializableException {

    public WilServiceException(String message) {
        super(message);
    }
    
    public WilServiceException() {}
    
}
