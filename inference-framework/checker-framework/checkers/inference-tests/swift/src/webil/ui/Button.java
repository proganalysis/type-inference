package webil.ui;

public class Button extends ClickableWidget {

    public Button(String text) {
        super();
        setText(text);
    }

    public Button(String id, String text) {
        super(id);
        setText(text);
    }

    protected void initWidget() {
        this.widgetImpl = new ButtonImpl(this);
    }

    public String getText() {
        ButtonImpl b = (ButtonImpl)widgetImpl;
        return b.getText();
    }

    public void setText(String text) {
        ButtonImpl b = (ButtonImpl)widgetImpl;
        b.setText(text);
    }

    public void setEnabled(boolean enabled) {
        ButtonImpl b = (ButtonImpl)widgetImpl;
        b.setEnabled(enabled);
    }

}
