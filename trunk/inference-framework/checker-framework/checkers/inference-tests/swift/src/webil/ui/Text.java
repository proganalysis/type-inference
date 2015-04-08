package webil.ui;


public class Text extends ClickableWidget {
    
    public Text(String text) {
        super();
        setText(text);
    }
    
    public Text(String id, String text) { 
        super(id);
        setText(text);
    }
    
    protected void initWidget() {
        this.widgetImpl = new TextImpl(this);        
    }
    
    public String getText() {
        TextImpl twi = (TextImpl) widgetImpl;
        return twi.getText();
    }
    
    public void setText(String text) {
        TextImpl twi = (TextImpl) widgetImpl;
        twi.setText(text);
    }
    
}
