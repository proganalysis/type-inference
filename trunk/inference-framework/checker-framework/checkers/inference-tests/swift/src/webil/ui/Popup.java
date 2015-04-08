package webil.ui;



public class Popup extends Panel {
    private boolean autoHide = false;
    public Popup() {
        super();
    }
    public Popup(boolean autoHide) {
        super();
        this.autoHide = autoHide;
        initWidget();
    }

    public Popup(String id) {
        super(id);
    }

    public void hide() {
        ((PopupImpl)widgetImpl).hide();
    }
    
    public void show() {
        ((PopupImpl)widgetImpl).show();
    }
    
    public static void showMessage(String msg) {
        Popup popup;
        popup = new Popup(true);
        Panel panel;
        panel = new VerticalPanel();
        popup.addChild(panel);        
        panel.addChild(new Text(msg));
        popup.show();
    }

    protected void initWidget() {
        this.widgetImpl = new PopupImpl(this, this.autoHide);
    }

}
