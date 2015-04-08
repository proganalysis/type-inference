package webil.ui;

import webil.signature.Client;

public class TextImpl extends ClickableWidgetImpl implements Client {
    
    public TextImpl(Object wilWidget) { 
        super(wilWidget);
    }
    
    
    protected void initGWTWidget() {
        this.gwtWidget = new com.google.gwt.user.client.ui.Label();        
    }
    
    public String getText() {
        com.google.gwt.user.client.ui.Label t = 
            (com.google.gwt.user.client.ui.Label) gwtWidget;
        return t.getText();
    }
    
    public void setText(String text) {
        com.google.gwt.user.client.ui.Label t = 
            (com.google.gwt.user.client.ui.Label) gwtWidget;
        t.setText(text);
    }
}
