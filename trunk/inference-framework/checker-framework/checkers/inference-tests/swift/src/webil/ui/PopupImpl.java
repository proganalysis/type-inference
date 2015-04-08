package webil.ui;

import webil.signature.Client;

public class PopupImpl extends PanelImpl implements Client {

    public PopupImpl(Object wilWidget) { 
        super(wilWidget);
    }
    public PopupImpl(Object wilWidget, boolean autoHide) {
        super(wilWidget);
        initGWTWidget(autoHide);
    }


    public void hide() {
        ((com.google.gwt.user.client.ui.PopupPanel)gwtWidget).hide();
    }
    
    public void show() {
        ((com.google.gwt.user.client.ui.PopupPanel)gwtWidget).show();
    }
    

    protected void initGWTWidget() {
        this.gwtWidget = new com.google.gwt.user.client.ui.PopupPanel();
    }
    protected void initGWTWidget(boolean autohide) {
        this.gwtWidget = new com.google.gwt.user.client.ui.PopupPanel(autohide);
    }

}
