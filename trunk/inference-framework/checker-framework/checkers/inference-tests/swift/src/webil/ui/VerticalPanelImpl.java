package webil.ui;

import webil.signature.Client;

public class VerticalPanelImpl extends PanelImpl implements Client {
    
    public VerticalPanelImpl(Object wilWidget) { 
        super(wilWidget);
    }
        
    protected void initGWTWidget() {
        this.gwtWidget = new com.google.gwt.user.client.ui.VerticalPanel();
    }
    
}
