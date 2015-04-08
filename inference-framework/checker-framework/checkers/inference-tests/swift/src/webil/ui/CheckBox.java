package webil.ui;

public class CheckBox extends ClickableWidget
{
    public CheckBox(String text) { 
        super();
        setText(text);
    }
    
    public CheckBox(String id, String text) { 
        super(id);
        setText(text);
    }
    
    protected void initWidget() {
        this.widgetImpl = new CheckBoxImpl(this);
    }

    public String getText() {
        CheckBoxImpl b = (CheckBoxImpl)widgetImpl;
        return b.getText();
    }
    
    public void setText(String text) {
        CheckBoxImpl b = (CheckBoxImpl)widgetImpl;
        b.setText(text);
    }

    public boolean isChecked() {
        CheckBoxImpl b = (CheckBoxImpl)widgetImpl;
        return b.isChecked();
    }

    public boolean isEnabled() {
        CheckBoxImpl b = (CheckBoxImpl)widgetImpl;
        return b.isEnabled();
    }

    public void setChecked(boolean check) {
        CheckBoxImpl b = (CheckBoxImpl)widgetImpl;
        b.setChecked(check);
    }

    public void setEnabled(boolean enable) {
        CheckBoxImpl b = (CheckBoxImpl)widgetImpl;
        b.setChecked(enable);
    }
    
}
