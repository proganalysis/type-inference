package webil.ui;


public abstract class Panel extends Widget {
    
    /** @gwt.typeArgs <webil.ui.Widget>
     */
    private Widget[] children = null;
    private int numChildren = 0;
    
    public Panel() { 
        super();
        initChildren();
    }
    
    public Panel(String id) { 
        super(id);
        initChildren();
    }
    
    private void initChildren() {
        if (children == null) {
            children = new Widget[10];
            numChildren = 0;
        }
    }

    public void addChild(Widget w) {
        initChildren();
        PanelImpl p = (PanelImpl) widgetImpl;
        if (w != null) p.addChild(w.widgetImpl); else p.addChild(null);

        if (numChildren >= children.length) {
            // expand the array.
            Widget[] newChildren = new Widget[children.length * 2];
            int i = 0;
            while (i < numChildren) {
                newChildren[i] = children[i];
                i = i + 1;
            }
            children = newChildren;
        }
        children[numChildren] = w;
        numChildren = numChildren + 1;
    }
    
    public void removeChild(Widget w) {
        PanelImpl p = (PanelImpl) widgetImpl;
        if (w != null) p.removeChild(w.widgetImpl); else p.removeChild(null);
        int i = 0;
        boolean removed = false;
        while (i < numChildren) {
            if (removed == true) {
                children[i] = children[i+1];
            }
            else if (children[i] == w) {
                removed = true;
                children[i] = null;
            }
            i = i + 1;
        }
        if (removed) numChildren = numChildren - 1;
    }
    
    public void removeAllChildren() {
        PanelImpl p = (PanelImpl) widgetImpl;
        p.removeAllChildren();
        int i = 0;
        while (i < numChildren) {
            children[i] = null;
            i = i + 1;
        }
        numChildren = 0;
    }
    
    public Widget getChildById(String id) {
        if (id == null) {
            return null;
        }
        
        int i = 0;
        while (i < numChildren) {
            Widget w = children[i];
            if (w != null && id.equals(w.domId)) {
                return w;
            }
            if (w instanceof Panel) {
                Panel p = (Panel) w;
                Widget c = p.getChildById(id);
                
                if (c != null) {
                    return c;
                }
            }        
             
            i = i + 1;
        }
        return null;

    }
    
}
