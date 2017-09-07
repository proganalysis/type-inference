package org.apache.http;

import checkers.inference.reim.quals.*;
import checkers.inference.sflow.quals.*;


public abstract interface HttpEntityEnclosingRequest extends org.apache.http.HttpRequest {
    public abstract boolean expectContinue();
    public abstract org.apache.http.HttpEntity getEntity();
    public abstract void setEntity(@Safe org.apache.http.HttpEntity arg0);
}
