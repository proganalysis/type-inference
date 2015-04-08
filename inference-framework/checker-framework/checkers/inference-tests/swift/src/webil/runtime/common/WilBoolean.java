package webil.runtime.common;

import com.google.gwt.user.client.rpc.IsSerializable;

public class WilBoolean implements IsSerializable {
    private Boolean bool;
    
    public WilBoolean(Boolean bool) {
        this.bool = bool;
    }
    
    public WilBoolean(boolean bool) {
        if (bool) this.bool = Boolean.TRUE;
        else this.bool = Boolean.FALSE;
    }
    
    public WilBoolean() {
        this.bool = null;
    }
    
    public Boolean toBoolean() {
        return bool;
    }
}
