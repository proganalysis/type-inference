package webil.ui;

import webil.signature.Client;

public class CheckBoxImpl extends ClickableWidgetImpl implements Client
{
    public CheckBoxImpl(Object wilWidget) { 
        super(wilWidget);
    }
    
    protected void initGWTWidget() {
        this.gwtWidget = new com.google.gwt.user.client.ui.CheckBox();
    }

    public String getText() {
        com.google.gwt.user.client.ui.CheckBox b = 
            (com.google.gwt.user.client.ui.CheckBox) gwtWidget;
        return b.getText();
    }
    
    public void setText(String text) {
        com.google.gwt.user.client.ui.CheckBox b = 
            (com.google.gwt.user.client.ui.CheckBox) gwtWidget;
        b.setText(text);
    }

    public boolean isChecked() {
	com.google.gwt.user.client.ui.CheckBox b = 
            (com.google.gwt.user.client.ui.CheckBox) gwtWidget;
	return b.isChecked();
    }

    public boolean isEnabled() {
	com.google.gwt.user.client.ui.CheckBox b = 
            (com.google.gwt.user.client.ui.CheckBox) gwtWidget;
	return b.isEnabled();
    }

    public void setChecked(boolean check) {
	com.google.gwt.user.client.ui.CheckBox b = 
            (com.google.gwt.user.client.ui.CheckBox) gwtWidget;
	b.setChecked(check);
    }

    public void setEnabled(boolean enable) {
	com.google.gwt.user.client.ui.CheckBox b = 
            (com.google.gwt.user.client.ui.CheckBox) gwtWidget;
	b.setChecked(enable);
    }
    
}
