package webil.ui;


public class Timer extends Widget {
    
    public Timer() { 
        super();
    }

    public Timer(int millis) { 
        super();
        schedule(millis);
    }

    public Timer(int millis, boolean repeat) { 
        super();
        schedule(millis, repeat);
    }
    
    protected void initWidget() {
        this.widgetImpl = new TimerImpl(this);
    }

    public void schedule(int millis) {
        schedule(millis, false);
    }
    
    public void schedule(int millis, boolean repeat) {
        TimerImpl t = (TimerImpl)widgetImpl;
        t.schedule(millis, repeat);
    }
    
    public void cancel() {
        TimerImpl t = (TimerImpl)widgetImpl;
        t.cancel();
    }

    public void addListener(final TimerListener l) {
        TimerImpl t = (TimerImpl)widgetImpl;
        t.addListener(l);
    }

    public void setStyleName(String style) {
    }
    
    public void setVisible(boolean visible) {
    }
    
}
