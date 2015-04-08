package webil.ui;

public class TextBox extends ClickableWidget {
    
    public TextBox(String text) { 
        super();
        setText(text);
    }
    
    public TextBox(String id, String text) { 
        super(id);
        setText(text);
    }
    
    protected void initWidget() {
        this.widgetImpl = new TextBoxImpl(this);
    }
    
    public String getText() {
        TextBoxImpl t = 
            (TextBoxImpl) widgetImpl;
        return t.getText();
    }
    
    public void setText(String text) {
        TextBoxImpl t = 
            (TextBoxImpl) widgetImpl;
        t.setText(text);
    }
    
    public void setFocus(boolean state) {
        TextBoxImpl t = 
            (TextBoxImpl) widgetImpl;
        t.setFocus(state);
    }

}
