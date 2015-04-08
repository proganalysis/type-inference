package webil.ui;

import webil.signature.Client;
import checkers.inference2.jcrypt.quals.*;

public class PasswordTextBoxImpl extends WidgetImpl implements Client {

    public PasswordTextBoxImpl(Object wilWidget) { 
        super(wilWidget);
    }


    /*
     * (non-Javadoc)
     * 
     * @see webil.ui.Widget#initWidget()
     */
    protected void initGWTWidget() {
        this.gwtWidget = new /*@Sensitive*/ com.google.gwt.user.client.ui.PasswordTextBox();
    }

    public String getText(/*@Sensitive*/ PasswordTextBoxImpl this) {
        return ((com.google.gwt.user.client.ui.PasswordTextBox)gwtWidget)
            .getText();
    }

    public void setText(String text) {
        ((com.google.gwt.user.client.ui.PasswordTextBox)gwtWidget).setText(text);
    }

    public void setFocus(boolean state) {
        ((com.google.gwt.user.client.ui.PasswordTextBox)gwtWidget).setFocus(state);
    }
}
