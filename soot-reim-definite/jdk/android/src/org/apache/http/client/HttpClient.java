package org.apache.http.client;

import checkers.inference.reim.quals.*;
import checkers.inference.sflow.quals.*;


public abstract interface HttpClient {
    public abstract <T> T execute(org.apache.http.HttpHost arg0, @Safe org.apache.http.HttpRequest arg1, org.apache.http.client.ResponseHandler<? extends T> arg2) throws java.io.IOException,org.apache.http.client.ClientProtocolException;
    public abstract <T> T execute(org.apache.http.HttpHost arg0, @Safe org.apache.http.HttpRequest arg1, org.apache.http.client.ResponseHandler<? extends T> arg2, org.apache.http.protocol.HttpContext arg3) throws java.io.IOException,org.apache.http.client.ClientProtocolException;
    public abstract <T> T execute(@Safe org.apache.http.client.methods.HttpUriRequest arg0, org.apache.http.client.ResponseHandler<? extends T> arg1) throws java.io.IOException,org.apache.http.client.ClientProtocolException;
    public abstract <T> T execute(@Safe org.apache.http.client.methods.HttpUriRequest arg0, org.apache.http.client.ResponseHandler<? extends T> arg1, org.apache.http.protocol.HttpContext arg2) throws java.io.IOException,org.apache.http.client.ClientProtocolException;
    public abstract org.apache.http.HttpResponse execute(org.apache.http.HttpHost arg0, @Safe org.apache.http.HttpRequest arg1) throws java.io.IOException,org.apache.http.client.ClientProtocolException;
    public abstract org.apache.http.HttpResponse execute(org.apache.http.HttpHost arg0, @Safe org.apache.http.HttpRequest arg1, org.apache.http.protocol.HttpContext arg2) throws java.io.IOException,org.apache.http.client.ClientProtocolException;
    public abstract org.apache.http.HttpResponse execute(@Safe org.apache.http.client.methods.HttpUriRequest arg0) throws java.io.IOException,org.apache.http.client.ClientProtocolException;
    public abstract org.apache.http.HttpResponse execute(@Safe org.apache.http.client.methods.HttpUriRequest arg0, org.apache.http.protocol.HttpContext arg1) throws java.io.IOException,org.apache.http.client.ClientProtocolException;
    public abstract org.apache.http.conn.ClientConnectionManager getConnectionManager();
    public abstract org.apache.http.params.HttpParams getParams();
}
