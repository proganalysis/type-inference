package webil.runtime.client;

import webil.runtime.common.ExtendedClosure;
import webil.runtime.common.ObjectID;
import webil.runtime.common.WilServiceException;

import com.google.gwt.user.client.rpc.RemoteService;

/**
 * GWT remote service RPC interface for WebIL's runtime.
 */
public interface WilService extends RemoteService {
    public ExtendedClosure getClosure(ExtendedClosure closure) throws WilServiceException;
    public ObjectID getClientPrincipal(int subSession) throws WilServiceException;    
}
