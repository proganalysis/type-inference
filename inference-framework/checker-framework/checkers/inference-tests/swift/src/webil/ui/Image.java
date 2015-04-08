package webil.ui;


public class Image extends ClickableWidget {
    
    public Image(String url) {
        super();
        setUrl(url);
    }
    
    public Image(String id, String url) { 
        super(id);
        setUrl(url);
    }
    
    protected void initWidget() {
        this.widgetImpl = new ImageImpl(this);        
    }
    
    public String getUrl() {
        ImageImpl i = 
            (ImageImpl) widgetImpl;
        return i.getUrl();
    }
    
    public void setUrl(String url) {
        ImageImpl i = 
            (ImageImpl) widgetImpl;
        i.setUrl(url);
    }
    
}
