package webil.ui;

public abstract class ClickableWidget extends Widget {

    public ClickableWidget() {
    }

    public ClickableWidget(String id) {
        super(id);
    }

    public void addListener(final ClickListener l) {
        ClickableWidgetImpl cwi = (ClickableWidgetImpl)widgetImpl;        
        cwi.addListener(l);
    }
 
}
