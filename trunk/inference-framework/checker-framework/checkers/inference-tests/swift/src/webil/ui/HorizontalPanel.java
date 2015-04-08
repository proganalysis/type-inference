package webil.ui;

public class HorizontalPanel extends Panel {
    
    public HorizontalPanel() { 
        super();
    }
    
    public HorizontalPanel(String id) { 
        super(id);
    }
    
    protected void initWidget() {
        this.widgetImpl = new HorizontalPanelImpl(this);
    }

}
