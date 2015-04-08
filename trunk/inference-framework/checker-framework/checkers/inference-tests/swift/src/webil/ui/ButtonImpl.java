package webil.ui;

import webil.signature.Client;
import checkers.inference2.jcrypt.quals.*;

public class ButtonImpl extends ClickableWidgetImpl implements Client {

    public ButtonImpl(Object wilWidget) {
        super(wilWidget);
    }

    protected void initGWTWidget() {
        gwtWidget = new com.google.gwt.user.client.ui.Button();
        
    }

    public String getText() {
        /*@Sensitive*/ com.google.gwt.user.client.ui.Button b =
            (com.google.gwt.user.client.ui.Button)gwtWidget;
        return b.getText();
    }

    public void setText(String text) {
        com.google.gwt.user.client.ui.Button b =
            (com.google.gwt.user.client.ui.Button)gwtWidget;
        b.setText(text);
    }

    public void setEnabled(boolean enabled) {
        ((com.google.gwt.user.client.ui.Button)gwtWidget).setEnabled(enabled);
    }
}