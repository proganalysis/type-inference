package webil.ui;

import webil.signature.Client;

public class RadioButtonImpl extends CheckBoxImpl implements Client {
    
    public RadioButtonImpl(Object wilWidget, String group) { 
        super(wilWidget);
	initGWTWidget(group);
    }
    
    protected void initGWTWidget() {
        this.gwtWidget = null;
    }

    protected void initGWTWidget(String group) {
        this.gwtWidget = new com.google.gwt.user.client.ui.RadioButton(group);
    }

}
