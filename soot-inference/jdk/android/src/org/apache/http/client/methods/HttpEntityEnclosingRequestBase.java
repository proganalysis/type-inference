package org.apache.http.client.methods;
import checkers.inference.reim.quals.*;
import checkers.inference.sflow.quals.*;


public abstract class HttpEntityEnclosingRequestBase extends org.apache.http.client.methods.HttpRequestBase implements org.apache.http.HttpEntityEnclosingRequest {
    private org.apache.http.HttpEntity entity;
    public HttpEntityEnclosingRequestBase() { throw new RuntimeException("skeleton method"); }
    public java.lang.Object clone() throws java.lang.CloneNotSupportedException { throw new RuntimeException("skeleton method"); }
    public boolean expectContinue() { throw new RuntimeException("skeleton method"); }
    public org.apache.http.HttpEntity getEntity() { throw new RuntimeException("skeleton method"); }
    public void setEntity(@Safe org.apache.http.HttpEntity arg0) { throw new RuntimeException("skeleton method"); }
}
