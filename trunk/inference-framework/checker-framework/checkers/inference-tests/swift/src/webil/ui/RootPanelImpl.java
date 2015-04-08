package webil.ui;

import webil.signature.Client;

public class RootPanelImpl extends PanelImpl implements Client {
    
    protected RootPanelImpl(Object wilWidget) { 
        super(wilWidget);
    }
    
    protected void initGWTWidget() {
        gwtWidget = com.google.gwt.user.client.ui.RootPanel.get();
    }
    
}
