package webil.runtime.common;

import com.google.gwt.user.client.rpc.IsSerializable;

public class WilString implements IsSerializable {
    private String string;
    
    public WilString(String str) {
        this.string = str;
    }
    
    public WilString() {
        string = null;
    }
    
    public String toString() {
        return this.string;
    }
}
