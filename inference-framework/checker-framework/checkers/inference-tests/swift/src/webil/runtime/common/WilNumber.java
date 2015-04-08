package webil.runtime.common;

import com.google.gwt.user.client.rpc.IsSerializable;

public class WilNumber implements IsSerializable {
    private Number number;
    
    public WilNumber(Number number) {
        this.number = number;
    }
    
    public WilNumber() {
        this.number = null;
    }
    
    public Number toNumber() {
        return number;
    }
}
