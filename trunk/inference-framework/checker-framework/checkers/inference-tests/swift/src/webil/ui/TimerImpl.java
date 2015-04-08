package webil.ui;

import java.util.ArrayList;

import webil.runtime.client.WilRuntimeClient;
import webil.runtime.common.StackFrame;
import webil.runtime.common.WilObject;
import webil.signature.Client;

public class TimerImpl extends WidgetImpl implements Client {
    
    private final TimerHandler handler = new TimerHandler();
    private final ArrayList listeners = new ArrayList();

    public TimerImpl(Object wilWidget) { 
        super(wilWidget);
    }

    
    protected void initGWTWidget() {
    }

    
    public void schedule(int millis, boolean repeat) {
        if (repeat) {
            handler.scheduleRepeating(millis);
        } else {
            handler.schedule(millis);
        }
    }
    
    public void cancel() {
        handler.cancel();
    }

    public void addListener(final Object l) {
        listeners.add(l);
    }

    public void setStyleName(String style) {
    }
    
    public void setVisible(boolean visible) {
    }
    
    private class TimerHandler extends com.google.gwt.user.client.Timer {

        public void run() {
            WilRuntimeClient rt = WilRuntimeClient.getRuntime();
            
            for (int i = 0; i < listeners.size(); i++) {
                WilObject l = (WilObject) listeners.get(i);
                int c = l.getContinuation("onTimer@void@webil.ui.Timer");
                StackFrame f = rt.createStackFrame(c);
                f.objectIDs[0] = TimerImpl.this.wilWidget;
                rt.executeMethodContinuation(l, c, f);
            }
        }
        
    }
    
}
