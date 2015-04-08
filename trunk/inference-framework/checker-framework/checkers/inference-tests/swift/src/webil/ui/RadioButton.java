package webil.ui;



public class RadioButton extends CheckBox {
    
    public RadioButton(String group, String text) { 
        super(text);
	initWidget(group);
	setText(text); // FIXME: there must be a better way
    }
    
    public RadioButton(String id, String group, String text) { 
        super(id, text);
	initWidget(group);
	setText(text); // FIXME: there must be a better way
    }
    
    protected void initWidget(String group) {
        this.widgetImpl = new RadioButtonImpl(this, group);
    }

}
