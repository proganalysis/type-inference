package webil.runtime.client;

import webil.runtime.common.Closure;
import webil.runtime.common.ExtendedClosure;
import webil.runtime.common.MethodCallResult;
import webil.runtime.common.ObjectID;
import webil.runtime.common.StackFrame;
import webil.runtime.common.WilObject;
import webil.runtime.common.WilRuntime;
import webil.runtime.common.WilRuntimeException;
import webil.runtime.common.WilServiceException;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Random;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.InvocationException;
import com.google.gwt.user.client.rpc.ServiceDefTarget;

public abstract class WilRuntimeClient extends WilRuntime implements AsyncCallback<Object>, EntryPoint {

    protected WilObject clientPrincipal = null;

    public WilRuntimeClient() {
        super(false);
        subSession = Random.nextInt();
    }

    protected static WilRuntimeClient runtime;

    protected int numMessagesSent = 0;
    protected int numMessagesReceived = 0;

    /** Indicated whether we are already executing an event handler. If so, we
     * should not process another one. */
    protected boolean busy;

    private int entryContID;

    public static WilRuntimeClient getRuntime() { 
        return runtime;
    }
    public Object getClientPrincipal() { // this could return null in case there is a server/network failure
        return clientPrincipal;
    }

    private void initializeClientPrincipal() {
        if(clientPrincipal != null) return;
        System.out.println("\nObtaining client principal from the server ");
        // now call the remote service with this closure
        WilServiceAsync server = (WilServiceAsync)GWT.create(WilService.class);
        ServiceDefTarget endpoint = (ServiceDefTarget) server;
        String moduleRelativeURL = GWT.getModuleBaseURL() + "webil"; // TODO decide what this name should be
        endpoint.setServiceEntryPoint(moduleRelativeURL);


        try {
            server.getClientPrincipal(subSession, new ClientPrincipalCallback());
        } catch (WilRuntimeException e) {
            System.out.println("Cannot obtain client principal");
        }

    }
    public abstract void onModuleLoad();

    protected static class QueueElement {
        ExtendedClosure closure;
        boolean retIgnored;

        public QueueElement(ExtendedClosure closure) {
            this(closure, false);
        }

        public QueueElement(ExtendedClosure closure, boolean retIgnored) {
            this.closure = closure;
            this.retIgnored = retIgnored;
        }
    }

//  public void addHighIntegrityClosure(int continuationID, StackFrameID sfID) {
//  // client side code should simply ignore the requests of adding high-integrity code to the stack
//  }

    protected WilQueue queue = new WilQueue();

    protected void entryPoint(int entryContID) {
        this.entryContID = entryContID;
        initializeClientPrincipal(); // TODO - we probably need busy waiting on the client?        
    }

    /**
     * Executes the continuation specified by cid on the object identified by
     * oid, using the given stack frame. Called directly from client UI code,
     * will neither return a value or throw an exception.
     * 
     * @param obj
     *                the receiver object
     * @param cid
     *                the continuation id
     * @param frame
     *                the stack frame
     */
    public void executeMethodContinuation(WilObject obj, int cid, StackFrame frame) { 
        // if another event handler is already executing, ignore.
        // we can possibly also queue the new requests
        if (busy) {
            return;
        } else {
            busy = true;
        }

        Closure ret = new Closure(WilRuntime.HALT_CONTINUATION, null);
        if(frame == null) {
            frame = createStackFrame(cid);
        }
        MethodCallResult call = new MethodCallResult(ret, frame, obj, cid);
        checkPointStackSize();
        try {
            ExtendedClosure closure = new ExtendedClosure(getWireClosure(
                getClosureFromResult(call, null), null), null, null, 
                stackIdGenerator.getSeed(), objectIdGenerator.getSeed(),
                subSession); // TODO looks wasteful to call getlocalclosure
            executeExtendedClosure(closure);
        } catch (WilRuntimeException e) {
            e.printStackTrace();
            busy = false;
        }
    }

    private int prevPlace = WilRuntime.PLACE_C;

    private void callServer(ExtendedClosure closure) {
        debug("Sending message " + (numMessagesSent++) + " to server for Closure " + closure);
        // now call the remote service with this closure
        WilServiceAsync server = (WilServiceAsync)GWT.create(WilService.class);
        ServiceDefTarget endpoint = (ServiceDefTarget) server;
        String moduleRelativeURL = GWT.getModuleBaseURL() + "webil"; // TODO decide what this name should be
        endpoint.setServiceEntryPoint(moduleRelativeURL);

        server.getClosure(closure, this);
    }

    private void callServerQueue(ExtendedClosure closure) {
        if (queue.isEmpty()) {
            // invariant: the closure running on the server is always the head of the queue
            queue.offer(new QueueElement(closure));
            callServer(closure);
        }
        else {
            queue.offer(new QueueElement(closure));
        }
        debug("Adding to client send queue " + closure);
    }

    private void executeExtendedClosure(ExtendedClosure eClosure) {
        boolean prevTrusted = eClosure.prevTrusted;
        boolean withinSToSC = eClosure.withinStoSC;

        Closure closure = getLocalClosure(eClosure.incoming);
        int place = getPlacement(closure);

        while (true) { // for tail recursion
            while (place == prevPlace) {
                if (closure.continuationID == WilRuntime.HALT_CONTINUATION) {
                    // execution finishes
                    busy = false;
                    return;
                }

//              System.err.println("client: " + closure.continuationID);
//              printStack();
//              printStackFrame(closure.stackFrameID);

                // XXX client side also needs to check high-integrity closures, because they are only popped up in checkClosure!
                boolean currentTrusted = isHighIntegrity(closure);
//              System.err.println(closure.continuationID + " is trusted? " + currentTrusted + " " + discount);

                if(prevTrusted && isCFI(closure)) {
                    popStack("Popping CFI Closure before executing");
                } else if (!prevTrusted && currentTrusted && !checkClosure(closure)) {
                    throw new WilRuntimeException("Invalid Closure Execution Request");                        
                }

                prevTrusted = currentTrusted;

                debug("Executing Closure " + closure);
                
                Closure nextClosure = dispatch(closure);

                if (nextClosure != closure) { // this condition should always be true
                    // note, closure == nextClosure means that closure CANNOT 
                    // run on client, i.e. S
                    prevPlace = place;
                    closure = nextClosure;
                    place = getPlacement(closure);
                }
            }

            // now place <> prevPlace
            // and place == placement of closure
            if (prevPlace == WilRuntime.PLACE_C) {
                if (place == WilRuntime.PLACE_S) {
                    callServerQueue(getStackHeapUpdate(getMinDepthOfStack(), 
                        closure));
                    break;
                }
                else { // WilRuntime.Place_SC
                    if (withinSToSC) {
                        withinSToSC = false;
                    } else {
                        callServerQueue(getStackHeapUpdate(getMinDepthOfStack(), 
                            closure));
                    }

                    // XXX set placement to SC in order for the client side to continue
                    prevPlace = WilRuntime.PLACE_SC;
                }
            }
            else { // WilRuntime.PLACE_SC
                if (place == WilRuntime.PLACE_C) {
                    ((QueueElement)queue.peekTail()).retIgnored = true;
                    // SC->C, ignore the stack frames accumulated during the SC block
                    checkPointStackSize();
                    prevPlace = WilRuntime.PLACE_C;
                }
                else { // WilRuntime.PlACE_S
                    break;
                }
            }
        }
    }

    public void onSuccess(Object result) {
        ExtendedClosure closure = (ExtendedClosure)result;

        // Note, queue shouldn't be empty!
        // we only deque when the closure returns back to the client
        QueueElement element = (QueueElement)queue.poll();
        if (element.retIgnored) {
            if (queue.isEmpty()) return;
            ExtendedClosure toSend = ((QueueElement)queue.peek()).closure;
            callServer(toSend);
            return;
        }
        else {
            // returns from S
            prevPlace = WilRuntime.PLACE_C;
            debug("Client: Received message " + (numMessagesReceived++) + " from the server");
        }


        entry(closure); // applies stack and heap updates

        checkPointStackSize();
        if(closure.withinStoSC) {
            // have to send back a message immediately to the server
            closure.subSession = subSession;
            callServerQueue(closure);
        }

        executeExtendedClosure(closure);
    }

    public void onFailure(Throwable caught) {
        try {
            throw caught;
        } catch (InvocationException e) {
            System.err.println("RPC invocation error");
        } catch (WilServiceException e) {
            System.err.println("WebIL run-time error");
        } catch (Throwable e) {
            System.err.println("Unknown error");
        }

        caught.printStackTrace();
        busy = false;
    }

    private class ClientPrincipalCallback implements AsyncCallback<Object> {

        public void onSuccess(Object result) {
            if (result != null) {
                ObjectID res = (ObjectID) result;
                // the ObjectID is the ID of the client principal object.
                // create the client side class for it.

                System.out.println("Obtained client principal");

                clientPrincipal = createInstance(getClassID("webil.runtime.common.WebILClientPrincipal"));
                objectStore.putObject(res, clientPrincipal);
            }

            executeMethodContinuation(null, entryContID, null);
        }

        public void onFailure(Throwable caught) {
            WilRuntimeClient.this.onFailure(caught);
        }

    }

}
