package webil.ui;

import webil.runtime.common.WilObject;
import webil.signature.Client;

public abstract class WidgetImpl implements Client {
    
    protected com.google.gwt.user.client.ui.Widget gwtWidget;
    protected WilObject wilWidget;
    
    protected WidgetImpl(Object wilWidget) {
        this.wilWidget = (WilObject) wilWidget;
        initGWTWidget();
    }

    protected abstract void initGWTWidget();

    public void setStyleName(String style) {
        gwtWidget.setStyleName(style);
    }
    
    public void setVisible(boolean visible) {
        gwtWidget.setVisible(visible);
    }    
}
