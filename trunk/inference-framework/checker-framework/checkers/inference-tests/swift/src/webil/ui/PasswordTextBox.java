package webil.ui;



public class PasswordTextBox extends Widget {

    public PasswordTextBox() {}

    public PasswordTextBox(String id) {
        super(id);
    }

    /*
     * (non-Javadoc)
     * 
     * @see webil.ui.Widget#initWidget()
     */
    protected void initWidget() {
        this.widgetImpl = new PasswordTextBoxImpl(this);
    }

    public String getText() {
        return ((PasswordTextBoxImpl)widgetImpl)
            .getText();
    }

    public void setText(String text) {
        ((PasswordTextBoxImpl)widgetImpl).setText(text);
    }

    public void setFocus(boolean state) {
        ((PasswordTextBoxImpl)widgetImpl).setFocus(state);
    }

}
