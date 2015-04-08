package webil.ui;

public abstract class Widget {
    
    /*-@Poly*/ protected String domId;
    protected WidgetImpl widgetImpl;

    public Widget() {
        this.domId = null;
        initWidget();
    }

    public Widget(String id) {
        this.domId = id;
        initWidget();
    }
    public String getID() {
        return domId;
    }

    public void setStyleName(String style) {
        widgetImpl.setStyleName(style);
    }
    
    public void setVisible(boolean visible) {
        widgetImpl.setVisible(visible);
    }
    
    protected abstract void initWidget();

}
