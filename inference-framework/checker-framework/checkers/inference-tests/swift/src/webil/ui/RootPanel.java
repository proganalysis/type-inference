package webil.ui;


public class RootPanel extends Panel {
    
    //protected static RootPanel root;

    protected RootPanel() {
        super();
    }

    public static RootPanel getRootPanel() {
        return new RootPanel();
//        if (root == null) {
//            root = new RootPanel();
//        }
//        
//        return root;
    }
    
    protected void initWidget() {
        this.widgetImpl = new RootPanelImpl(this);
    }
    
}
