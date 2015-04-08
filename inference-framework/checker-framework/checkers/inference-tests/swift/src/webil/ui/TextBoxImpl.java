package webil.ui;

import webil.signature.Client;
import checkers.inference2.jcrypt.quals.*;

public class TextBoxImpl extends ClickableWidgetImpl implements Client {
    
    public TextBoxImpl(Object wilWidget) { 
        super(wilWidget);
    }
    
    
    protected void initGWTWidget() {
        this.gwtWidget = new com.google.gwt.user.client.ui.TextBox();
    }
    
    public String getText() {
        /*@Sensitive*/ com.google.gwt.user.client.ui.TextBox t = 
            (com.google.gwt.user.client.ui.TextBox) gwtWidget;
        return t.getText();
    }
    
    public void setText(String text) {
        com.google.gwt.user.client.ui.TextBox t = 
            (com.google.gwt.user.client.ui.TextBox) gwtWidget;
        t.setText(text);
    }
    
    public void setFocus(boolean state) {
        com.google.gwt.user.client.ui.TextBox t = 
            (com.google.gwt.user.client.ui.TextBox) gwtWidget;
        t.setFocus(state);
    }
    
}
