package webil.ui;

import webil.signature.Client;

public class ImageImpl extends ClickableWidgetImpl implements Client {
    
    public ImageImpl(Object wilWidget) { 
        super(wilWidget);
    }
    
    protected void initGWTWidget() {
        this.gwtWidget = new com.google.gwt.user.client.ui.Image();        
    }
    
    public String getUrl() {
        com.google.gwt.user.client.ui.Image i = 
            (com.google.gwt.user.client.ui.Image) gwtWidget;
        return i.getUrl();
    }
    
    public void setUrl(String url) {
        com.google.gwt.user.client.ui.Image i = 
            (com.google.gwt.user.client.ui.Image) gwtWidget;
        i.setUrl(url);
    }
}
