package webil.ui;



public class VerticalPanel extends Panel {
    
    public VerticalPanel() { 
        super();
    }
    
    public VerticalPanel(String id) { 
        super(id);
    }
    
    protected void initWidget() {
        this.widgetImpl = new VerticalPanelImpl(this);
    }
    
}
