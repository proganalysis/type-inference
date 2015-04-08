package webil.runtime.client;

import webil.runtime.common.ExtendedClosure;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * GWT required Async mirror of WilService.
 */
public interface WilServiceAsync {
    public void getClientPrincipal(int subSession, AsyncCallback<Object> callback);
    public void getClosure(ExtendedClosure closure, AsyncCallback<Object> callback);    
}