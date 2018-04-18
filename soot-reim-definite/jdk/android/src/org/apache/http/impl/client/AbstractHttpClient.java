package org.apache.http.impl.client;

import checkers.inference.reim.quals.*;
import checkers.inference.sflow.quals.*;


public abstract class AbstractHttpClient implements org.apache.http.client.HttpClient {
    private org.apache.http.conn.ClientConnectionManager connManager;
    private org.apache.http.client.CookieStore cookieStore;
    private org.apache.http.client.CredentialsProvider credsProvider;
    private org.apache.http.params.HttpParams defaultParams;
    private org.apache.http.protocol.BasicHttpProcessor httpProcessor;
    private org.apache.http.conn.ConnectionKeepAliveStrategy keepAliveStrategy;
    private final org.apache.commons.logging.Log log = null;
    private org.apache.http.client.AuthenticationHandler proxyAuthHandler;
    private org.apache.http.client.RedirectHandler redirectHandler;
    private org.apache.http.protocol.HttpRequestExecutor requestExec;
    private org.apache.http.client.HttpRequestRetryHandler retryHandler;
    private org.apache.http.ConnectionReuseStrategy reuseStrategy;
    private org.apache.http.conn.routing.HttpRoutePlanner routePlanner;
    private org.apache.http.auth.AuthSchemeRegistry supportedAuthSchemes;
    private org.apache.http.cookie.CookieSpecRegistry supportedCookieSpecs;
    private org.apache.http.client.AuthenticationHandler targetAuthHandler;
    private org.apache.http.client.UserTokenHandler userTokenHandler;
    protected AbstractHttpClient(org.apache.http.conn.ClientConnectionManager arg0, org.apache.http.params.HttpParams arg1) { throw new RuntimeException("skeleton method"); }
    private org.apache.http.HttpHost determineTarget(org.apache.http.client.methods.HttpUriRequest arg0) { throw new RuntimeException("skeleton method"); }
    public void addRequestInterceptor(org.apache.http.HttpRequestInterceptor arg0) { throw new RuntimeException("skeleton method"); }
    public void addRequestInterceptor(org.apache.http.HttpRequestInterceptor arg0, int arg1) { throw new RuntimeException("skeleton method"); }
    public void addResponseInterceptor(org.apache.http.HttpResponseInterceptor arg0) { throw new RuntimeException("skeleton method"); }
    public void addResponseInterceptor(org.apache.http.HttpResponseInterceptor arg0, int arg1) { throw new RuntimeException("skeleton method"); }
    public void clearRequestInterceptors() { throw new RuntimeException("skeleton method"); }
    public void clearResponseInterceptors() { throw new RuntimeException("skeleton method"); }
    protected abstract org.apache.http.auth.AuthSchemeRegistry createAuthSchemeRegistry();
    protected abstract org.apache.http.conn.ClientConnectionManager createClientConnectionManager();
    protected org.apache.http.client.RequestDirector createClientRequestDirector(org.apache.http.protocol.HttpRequestExecutor arg0, org.apache.http.conn.ClientConnectionManager arg1, org.apache.http.ConnectionReuseStrategy arg2, org.apache.http.conn.ConnectionKeepAliveStrategy arg3, org.apache.http.conn.routing.HttpRoutePlanner arg4, org.apache.http.protocol.HttpProcessor arg5, org.apache.http.client.HttpRequestRetryHandler arg6, org.apache.http.client.RedirectHandler arg7, org.apache.http.client.AuthenticationHandler arg8, org.apache.http.client.AuthenticationHandler arg9, org.apache.http.client.UserTokenHandler arg10, org.apache.http.params.HttpParams arg11) { throw new RuntimeException("skeleton method"); }
    protected abstract org.apache.http.conn.ConnectionKeepAliveStrategy createConnectionKeepAliveStrategy();
    protected abstract org.apache.http.ConnectionReuseStrategy createConnectionReuseStrategy();
    protected abstract org.apache.http.cookie.CookieSpecRegistry createCookieSpecRegistry();
    protected abstract org.apache.http.client.CookieStore createCookieStore();
    protected abstract org.apache.http.client.CredentialsProvider createCredentialsProvider();
    protected abstract org.apache.http.protocol.HttpContext createHttpContext();
    protected abstract org.apache.http.params.HttpParams createHttpParams();
    protected abstract org.apache.http.protocol.BasicHttpProcessor createHttpProcessor();
    protected abstract org.apache.http.client.HttpRequestRetryHandler createHttpRequestRetryHandler();
    protected abstract org.apache.http.conn.routing.HttpRoutePlanner createHttpRoutePlanner();
    protected abstract org.apache.http.client.AuthenticationHandler createProxyAuthenticationHandler();
    protected abstract org.apache.http.client.RedirectHandler createRedirectHandler();
    protected abstract org.apache.http.protocol.HttpRequestExecutor createRequestExecutor();
    protected abstract org.apache.http.client.AuthenticationHandler createTargetAuthenticationHandler();
    protected abstract org.apache.http.client.UserTokenHandler createUserTokenHandler();
    protected org.apache.http.params.HttpParams determineParams(org.apache.http.HttpRequest arg0) { throw new RuntimeException("skeleton method"); }
    public <T> T execute(org.apache.http.HttpHost arg0, @Safe org.apache.http.HttpRequest arg1, org.apache.http.client.ResponseHandler<? extends T> arg2) throws java.io.IOException,org.apache.http.client.ClientProtocolException { throw new RuntimeException("skeleton method"); }
    public <T> T execute(org.apache.http.HttpHost arg0, @Safe org.apache.http.HttpRequest arg1, org.apache.http.client.ResponseHandler<? extends T> arg2, org.apache.http.protocol.HttpContext arg3) throws java.io.IOException,org.apache.http.client.ClientProtocolException { throw new RuntimeException("skeleton method"); }
    public <T> T execute(@Safe org.apache.http.client.methods.HttpUriRequest arg0, org.apache.http.client.ResponseHandler<? extends T> arg1) throws java.io.IOException,org.apache.http.client.ClientProtocolException { throw new RuntimeException("skeleton method"); }
    public <T> T execute(@Safe org.apache.http.client.methods.HttpUriRequest arg0, org.apache.http.client.ResponseHandler<? extends T> arg1, org.apache.http.protocol.HttpContext arg2) throws java.io.IOException,org.apache.http.client.ClientProtocolException { throw new RuntimeException("skeleton method"); }
    public final org.apache.http.HttpResponse execute(org.apache.http.HttpHost arg0, @Safe org.apache.http.HttpRequest arg1) throws java.io.IOException,org.apache.http.client.ClientProtocolException { throw new RuntimeException("skeleton method"); }
    public final org.apache.http.HttpResponse execute(org.apache.http.HttpHost arg0, @Safe org.apache.http.HttpRequest arg1, org.apache.http.protocol.HttpContext arg2) throws java.io.IOException,org.apache.http.client.ClientProtocolException { throw new RuntimeException("skeleton method"); }
    public final org.apache.http.HttpResponse execute(@Safe org.apache.http.client.methods.HttpUriRequest arg0) throws java.io.IOException,org.apache.http.client.ClientProtocolException { throw new RuntimeException("skeleton method"); }
    public final org.apache.http.HttpResponse execute(@Safe org.apache.http.client.methods.HttpUriRequest arg0, org.apache.http.protocol.HttpContext arg1) throws java.io.IOException,org.apache.http.client.ClientProtocolException { throw new RuntimeException("skeleton method"); }
    public final org.apache.http.auth.AuthSchemeRegistry getAuthSchemes() { throw new RuntimeException("skeleton method"); }
    public final org.apache.http.conn.ConnectionKeepAliveStrategy getConnectionKeepAliveStrategy() { throw new RuntimeException("skeleton method"); }
    public final org.apache.http.conn.ClientConnectionManager getConnectionManager() { throw new RuntimeException("skeleton method"); }
    public final org.apache.http.ConnectionReuseStrategy getConnectionReuseStrategy() { throw new RuntimeException("skeleton method"); }
    public final org.apache.http.cookie.CookieSpecRegistry getCookieSpecs() { throw new RuntimeException("skeleton method"); }
    public final org.apache.http.client.CookieStore getCookieStore() { throw new RuntimeException("skeleton method"); }
    public final org.apache.http.client.CredentialsProvider getCredentialsProvider() { throw new RuntimeException("skeleton method"); }
    protected final org.apache.http.protocol.BasicHttpProcessor getHttpProcessor() { throw new RuntimeException("skeleton method"); }
    public final org.apache.http.client.HttpRequestRetryHandler getHttpRequestRetryHandler() { throw new RuntimeException("skeleton method"); }
    public final org.apache.http.params.HttpParams getParams() { throw new RuntimeException("skeleton method"); }
    public final org.apache.http.client.AuthenticationHandler getProxyAuthenticationHandler() { throw new RuntimeException("skeleton method"); }
    public final org.apache.http.client.RedirectHandler getRedirectHandler() { throw new RuntimeException("skeleton method"); }
    public final org.apache.http.protocol.HttpRequestExecutor getRequestExecutor() { throw new RuntimeException("skeleton method"); }
    public org.apache.http.HttpRequestInterceptor getRequestInterceptor(int arg0) { throw new RuntimeException("skeleton method"); }
    public int getRequestInterceptorCount() { throw new RuntimeException("skeleton method"); }
    public org.apache.http.HttpResponseInterceptor getResponseInterceptor(int arg0) { throw new RuntimeException("skeleton method"); }
    public int getResponseInterceptorCount() { throw new RuntimeException("skeleton method"); }
    public final org.apache.http.conn.routing.HttpRoutePlanner getRoutePlanner() { throw new RuntimeException("skeleton method"); }
    public final org.apache.http.client.AuthenticationHandler getTargetAuthenticationHandler() { throw new RuntimeException("skeleton method"); }
    public final org.apache.http.client.UserTokenHandler getUserTokenHandler() { throw new RuntimeException("skeleton method"); }
    public void removeRequestInterceptorByClass(java.lang.Class<? extends org.apache.http.HttpRequestInterceptor> arg0) { throw new RuntimeException("skeleton method"); }
    public void removeResponseInterceptorByClass(java.lang.Class<? extends org.apache.http.HttpResponseInterceptor> arg0) { throw new RuntimeException("skeleton method"); }
    public void setAuthSchemes(org.apache.http.auth.AuthSchemeRegistry arg0) { throw new RuntimeException("skeleton method"); }
    public void setCookieSpecs(org.apache.http.cookie.CookieSpecRegistry arg0) { throw new RuntimeException("skeleton method"); }
    public void setCookieStore(org.apache.http.client.CookieStore arg0) { throw new RuntimeException("skeleton method"); }
    public void setCredentialsProvider(org.apache.http.client.CredentialsProvider arg0) { throw new RuntimeException("skeleton method"); }
    public void setHttpRequestRetryHandler(org.apache.http.client.HttpRequestRetryHandler arg0) { throw new RuntimeException("skeleton method"); }
    public void setKeepAliveStrategy(org.apache.http.conn.ConnectionKeepAliveStrategy arg0) { throw new RuntimeException("skeleton method"); }
    public void setParams(org.apache.http.params.HttpParams arg0) { throw new RuntimeException("skeleton method"); }
    public void setProxyAuthenticationHandler(org.apache.http.client.AuthenticationHandler arg0) { throw new RuntimeException("skeleton method"); }
    public void setRedirectHandler(org.apache.http.client.RedirectHandler arg0) { throw new RuntimeException("skeleton method"); }
    public void setReuseStrategy(org.apache.http.ConnectionReuseStrategy arg0) { throw new RuntimeException("skeleton method"); }
    public void setRoutePlanner(org.apache.http.conn.routing.HttpRoutePlanner arg0) { throw new RuntimeException("skeleton method"); }
    public void setTargetAuthenticationHandler(org.apache.http.client.AuthenticationHandler arg0) { throw new RuntimeException("skeleton method"); }
    public void setUserTokenHandler(org.apache.http.client.UserTokenHandler arg0) { throw new RuntimeException("skeleton method"); }
}
