package webil.ui;

import webil.signature.Client;

public class HorizontalPanelImpl extends PanelImpl implements Client {
    
    public HorizontalPanelImpl(Object wilWidget) { 
        super(wilWidget);
    }
        
    protected void initGWTWidget() {
        this.gwtWidget = new com.google.gwt.user.client.ui.HorizontalPanel();
    }

}
