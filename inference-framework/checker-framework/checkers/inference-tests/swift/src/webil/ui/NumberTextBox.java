package webil.ui;

public class NumberTextBox extends TextBox {
    
    public NumberTextBox() {
        super("");
    }
    
    public NumberTextBox(int num) { 
        super("");
        setNumber(num);
    }
    
    public NumberTextBox(String id, int num) { 
        super(id, "");
        setNumber(num);
    }
    
    protected void initWidget() {
        this.widgetImpl = new NumberTextBoxImpl(this);
    }
    
    public Integer getInteger() {
        return ((NumberTextBoxImpl)this.widgetImpl).getInteger();
    }
    
    public Long getLong() {
        return ((NumberTextBoxImpl)this.widgetImpl).getLong();
    }
    
    public void setNumber(int num) {
        setText(Integer.toString(num));
    }
    
}
