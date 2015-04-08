package webil.ui;

import webil.signature.Client;

public abstract class PanelImpl extends WidgetImpl implements Client {
    public PanelImpl(Object wilWidget) { 
        super(wilWidget);
    }
    

    public void addChild(WidgetImpl w) {
        com.google.gwt.user.client.ui.Panel p = 
            (com.google.gwt.user.client.ui.Panel) gwtWidget;
        p.add(w.gwtWidget);
    }
    
    public void removeChild(WidgetImpl w) {
        com.google.gwt.user.client.ui.Panel p = 
            (com.google.gwt.user.client.ui.Panel) gwtWidget;
        p.remove(w.gwtWidget);
    }
    
    public void removeAllChildren() {
        com.google.gwt.user.client.ui.Panel p =
            (com.google.gwt.user.client.ui.Panel) gwtWidget;
        p.clear();
    }
    
}
