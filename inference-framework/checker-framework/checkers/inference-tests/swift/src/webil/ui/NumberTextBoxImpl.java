package webil.ui;

import webil.signature.Client;

import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import checkers.inference2.jcrypt.quals.*;

public class NumberTextBoxImpl extends TextBoxImpl implements Client {
    
    public NumberTextBoxImpl(Object wilWidget) { 
        super(wilWidget);
    }
    
    
    protected void initGWTWidget() {
        super.initGWTWidget();
        
        ((com.google.gwt.user.client.ui.TextBox) gwtWidget).addKeyboardListener(
            new com.google.gwt.user.client.ui.KeyboardListener() {
                public void onKeyDown(Widget sender, char keyCode, int modifiers) {}
                public void onKeyPress(Widget sender, char keyCode, int modifiers) {
                    if (!Character.isDigit(keyCode) && keyCode != 8) {
                        switch (keyCode) {
                        case 8:  // backspace
                        case 46: // delete
                        case 36: // home
                        case 35: // end
                        case 37: // left
                        case 39: // right
                            break;
                        default:
                        ((com.google.gwt.user.client.ui.TextBox) sender).cancelKey();
                        }
                    }
                }
                public void onKeyUp(Widget sender, char keyCode, int modifiers) {}
            });
    }
    
    public Integer getInteger() {
        /*@Sensitive*/ com.google.gwt.user.client.ui.TextBox tb = (TextBox)gwtWidget;
        try {
            String s = tb.getText();
            if (s == null || s.length() == 0) return null;
            return Integer.valueOf(s);
        }
        catch (NumberFormatException e) {
            return null;            
        }        
    }
    
    public Long getLong() {
        com.google.gwt.user.client.ui.TextBox tb = (TextBox)gwtWidget;
        try {
            String s = tb.getText();
            if (s == null || s.length() == 0) return null;
            return Long.valueOf(s);
        }
        catch (NumberFormatException e) {
            return null;            
        }        
    }
}
