package webil.ui;

import webil.runtime.client.WilRuntimeClient;
import webil.runtime.common.StackFrame;
import webil.runtime.common.WilObject;
import webil.signature.Client;

public abstract class ClickableWidgetImpl extends WidgetImpl implements Client {

    public ClickableWidgetImpl(Object wilWidget) {
        super(wilWidget);
    }

    public void addListener(final Object listener) {
        com.google.gwt.user.client.ui.SourcesClickEvents b = 
            (com.google.gwt.user.client.ui.SourcesClickEvents) gwtWidget;
        
        b.addClickListener(new com.google.gwt.user.client.ui.ClickListener() {
            public void onClick(com.google.gwt.user.client.ui.Widget sender) {
                WilRuntimeClient rt = WilRuntimeClient.getRuntime();
                int c = ((WilObject)listener).getContinuation("onClick@void@webil.ui.Widget");
                StackFrame f = rt.createStackFrame(c);
                f.objectIDs[0] = wilWidget; // XXX This will be picked up as the only method argument
                rt.executeMethodContinuation((WilObject)listener, c, f);
            }
        });
    }
}
