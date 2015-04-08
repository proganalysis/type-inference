package webil.runtime.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import com.google.gwt.user.client.rpc.IsSerializable;


/**
 *
 * @author kvikram
 * The runtime support for translated WebIL programs. One runtime object for each session.
 * An instance of this class will probably be stored in the Session state
 *
 */
abstract public class WilRuntime {

    public WilRuntime(boolean server) {
        this.isRunningOnServer = server;
        hostName = server?"Server":"Client";
        this.objectStore = new ObjectStore(this);
        stack = new Vector();
        frames = new HashMap();
        dirtyHeap = new HashMap();
        postDominatorMap = getPostDominatorMap();
    }

//  public abstract jif.lang.Principal getClientPrincipal();

    protected final boolean isRunningOnServer;
    protected String hostName;

    // 0 - prints nothing
    // 1 - prints closures executed and stack operations done
    // 2 - prints the stack at each debug message
    public final int DEBUG_LEVEL = 0;


    public Map postDominatorMap;

    public final ObjectStore objectStore;

    /**
     * The stack of closures.
     * Contains Return Closures, Exception Closures and High Integrity Closures.
     * Contains a set of closures at each level (only for catch handlers a.k.a. exception closures)  
     */
    protected final  Vector stack;

    /**
     * The stack of frames. Used as a map from stack IDs to StackFrame objects.
     */
    protected final HashMap frames;

    public Map getFrames() {
        return frames;
    }

    /**
     * All objects in the heap, which the other machine does not know about
     * Map of object references to class ID. class ID is -1 if this is a Java object
     */
    protected final HashMap dirtyHeap;

    /**
     * The minimum to which the stack reached since the last closure invocation from the
     * other machine
     */
    private int minDepthOfStack;

    // This is a data structure to store dirty stack frames, whose values need to
    // be sent to the other machine. This might also include stack frames of methods which
    // run purely on the client, in which case the actual values needn't be sent.
//  protected HashMap dirtyStack = new HashMap();
    // don't need a dirty stack - since we can send information about all stack frame IDs being sent
    // min depth of stack tells us about dirty stack frames


    // Generators for unique ids to be used for objects and stack frames.
    protected UniqueIdGenerator objectIdGenerator = new UniqueIdGenerator(4);
    protected UniqueIdGenerator stackIdGenerator = new UniqueIdGenerator(4);

    // used to distinguish different windows in the same session. this is
    // assigned a value only on the client, and passed to the server as part of
    // ExtendedClosure.
    protected int subSession;



    /*
     *
     *  SeVeN abstract methods that need to be implemented by the compiler
     *  These methods can be refactored into a separate interface that the generated application implements
     *  |
     *  V
     */

    /**
     *
     * @param closure
     * The closure that should be executed on this machine. This machine will
     * execute as many subsequent closures as possible.
     * @return
     * The next closure that needs to be executed. Will necessarily be on the
     * other machine. This method contains the mapping from continuation IDs to
     * code that should be executed.
     *
     * The contract is that dispatch will update <b>dirtyHeap</b> with any object IDs that
     * the other machine does not yet know about
     * Also, update <b>minDepthOfStack</b> which is the lowest point reached in the stack during
     * the execution of this method.
     * TODO Also, this will add a closure to the top of the stack, if it is high integrity
     */
    abstract public Closure dispatch(Closure closure) throws WilRuntimeException;

    /**
     *
     * @param classID
     * Every class in the source program is given an ID. When such an ID is passed
     * to this method, an instance of that class is created and inserted into
     * the ObjectStore. Will be implemented using a huge switch/case block
     * like in <code>dispatch</code>
     */
    abstract protected WilObject createInstance(int classID);

    /**
     *
     * @param continuationID
     * The continuation ID for which the frame size information is queried
     * @return
     * The frame sizes as a FrameIndices object
     */
    abstract protected FrameIndices getFrameIndices(int continuationID);

    /**
     *
     * @param contID
     * The continuation ID
     * @return
     * Whether this is a return, method call, exception handler or a normal continuation
     */
    abstract protected int getContinuationType(int contID);

    /**
     *
     * @param contID
     * The continuation ID
     * @return
     * true iff the continuation is a high integrity one
     */
    abstract public boolean isHighIntegrityImpl(int contID);

    public boolean isHighIntegrity(Closure closure) {
        if(closure == null) {
            return false;
        }
        if(closure instanceof CFIClosure) {
            return true;
        }
        return isHighIntegrityImpl(closure.continuationID);
    }

    /**
     * 
     * @param contID
     * @return true iff the continuation is the entry continuation of an auto-endorsed method
     */
    abstract public boolean isAutoEndorsed(int contID);

    /**
     * 
     * @param condID
     * @return the high integrity post dominator for this continuation block
     */
    abstract public Map getPostDominatorMap();

    /**
     * 
     * @param typeName fully qualified name of the type for which we want to lookup the integer class ID
     * @return the integer class ID used to represent that type
     */
    abstract public int getClassID(String typeName);


    public boolean isCFI(Closure closure) {
        if(closure instanceof CFIClosure) {
            return true;
        }

        // only if closure matches the top closure on the stack
        // and the top closure on the stack is a cfi closure
        Closure topClosure = (Closure)((List) stack.get(stack.size()-1)).get(0);

        if(topClosure.continuationID == closure.continuationID &&
            topClosure.stackFrameID.equals(closure.stackFrameID)) {
            if(topClosure instanceof CFIClosure) {
                return true;
            }
        }

        return false;

    }

    // invoked both on the client and server
    // on the client, its purpose is to push off high integ closures off the 
    // stack
    // actually needs to be invoked on every closure, not just high integrity closures
    public boolean checkClosure(Closure incoming) {
        // unfinished closures on the client, e.g. high integrity closures and CFI Closures will be filled in here
        // check whether closure can be executed by remote host
        // certain continuation IDs are always executable

//      System.err.println(incoming.continuationID + " is checked!");
        if(!isHighIntegrity(incoming)) {
            return true;
        }

        // for others the stack is checked
        Closure topClosure = (Closure)((List) stack.get(stack.size()-1)).get(0);

        // ignore auto
//      System.err.println(incoming.continuationID + " auto? " + isAutoEndorsed(incoming.continuationID));
        boolean isHighIntegContext = isAutoEndorsed(incoming.continuationID);  
        if (isHighIntegContext) {
            if (incoming.equals(topClosure)) {
                popStack("Popping off AutoEndorse block " + incoming.continuationID);
            }

            /*
            if(!isHighIntegrity(incoming)) {
                pushPostDominator(incoming, isHighIntegContext);
            } 
             */

            return true;
        }

//      System.err.println("check closure:");
//      System.err.println(topClosure.continuationID + " " + incoming.continuationID + " " + topClosure.stackFrameID + " " + incoming.stackFrameID);
//      System.err.println(topClosure.continuationID == incoming.continuationID);
//      System.err.println(topClosure.stackFrameID == incoming.stackFrameID);

        boolean approved = false;

        if (topClosure.equals(incoming)) {
            if(topClosure instanceof CFIClosure) {
                if (isRunningOnServer) {
                    CFIClosure cfi = (CFIClosure) topClosure;
                    approved = CFIClosureApproval(cfi);
                } else {
                    approved = true;
                }
            }
            popStack("High Integrity Closure Checking done");
            approved = true;
        }

        if (!approved) {
            // otherwise return false
            System.err.println("Check failed for Closure " + incoming.continuationID + " in stack frame " + incoming.stackFrameID);
        }

        return approved;
    }


    public final static int PLACE_S = 0;
    public final static int PLACE_C = 1;
    public final static int PLACE_SC = 2;

    /**
     * To be implemented by the generated runtime
     * @param contID
     * Continuation ID
     * @return
     * Where this continuation ID is placed - one of S, C or SC
     */
    abstract public int getPlacementImpl(int contID) throws WilRuntimeException;


    public static final int HALT_CONTINUATION = -1;

    private int getClosureType(Closure closure) {
        return getContinuationType(closure.continuationID);
    }


    private void applyStackUpdate(StackUpdate stackUpdate, Closure currentClosure) throws WilRuntimeException {
        if(stackUpdate == null) return;
        int newBottom = stackUpdate.start;
//      printStack();
//      System.err.println("stack update start: " + stackUpdate.start);
//      for (int i = 0; i < stackUpdate.stackDelta.length; i++) {
//      for (int j = 0; j < stackUpdate.stackDelta[i].length; j++) {
//      System.err.print(stackUpdate.stackDelta[i][j].continuationID + " ");
//      }
//      System.err.println();
//      }
//      System.err.println();
        if(newBottom > stack.size()) {
            // Some stack update in between was missed
            throw new WilRuntimeException("Missing Stack Update");
        }
        for(int index = stack.size() - 1; index >= newBottom; index--) {
            List topVector = (List)stack.get(index);

            Closure topClosure = (Closure)topVector.get(0);
            for(Iterator it = topVector.iterator(); it.hasNext();) {
                topClosure = (Closure) it.next();

                // Its ok to pop handlers off the stack
                if(isRunningOnServer &&
                    !(getClosureType(topClosure) == Closure.HANDLER) && 
                    isHighIntegrity(topClosure)) {
                    throw new WilRuntimeException("Control Flow Integrity Violated");
                }
            }
            stack.remove(index);

            if(index > newBottom) {
                List nextVector = (List)stack.get(index-1);
                Closure nextClosure = (Closure)nextVector.get(0);
                if(nextVector.size() == 1 && getContinuationType(nextClosure.continuationID) == Closure.RETURN) {
                    // that method call is over, so we can pop the frames stack as well
                    frames.remove(topClosure.stackFrameID);
                }

            }
        }

        // now push all closures onto the stack
        for(int index = 0; index < stackUpdate.stackDelta.length; index++) {
            List closureVector = new ArrayList();
            for(int columnIndex = 0; columnIndex < stackUpdate.stackDelta[index].length; columnIndex++) {
                Closure nextClosure = getLocalClosure(stackUpdate.stackDelta[index][columnIndex]);

                // the top stackDelta element is the current closure. don't add it
                // to the stack
                if (index < stackUpdate.stackDelta.length - 1) {
                    if(isRunningOnServer && isHighIntegrity(nextClosure)) {
                        throw new WilRuntimeException("Client attempting to push a high integrity closure");
                    }
                    // false, since the client has pushed it
//                  nextClosure.isHighIntegrity = false;
                    closureVector.add(nextClosure);
                }

                // XXX Some redundant code here .. since all closures in a set of catch handlers have the same frame ID
                // check if stack frame exists
                // This code executes the first time control reaches this host in a method
                stackFrameUpdateHelper(stackUpdate, nextClosure);
            }
            if (index < stackUpdate.stackDelta.length - 1) {
                stack.add(closureVector);
            }
        }
        
        // now update the stack frame information of the closure to be executed
        stackFrameUpdateHelper(stackUpdate, currentClosure);

        // Big TODO : Garbage Collection

    }
    
    private void stackFrameUpdateHelper(StackUpdate update, Closure closure) {
        StackFrameID frameID = closure.stackFrameID;

        if (frameID != null) {
            StackFrame frame = (StackFrame) frames.get(frameID);
            if(frame == null) {
                // if it doesn't exist, then we have to create one
                frame = createStackFrame(closure.continuationID, closure.stackFrameID);
                frames.put(frameID, frame);
            }
            // now update the stack frame
            StackFrameUpdate frameUpdate = (StackFrameUpdate) update.framesDelta.get(frameID);
            if(frameUpdate != null) {
                //System.err.println("Applying frame update for closure: " + nextClosure.continuationID);
                applyStackFrameUpdate(frameUpdate, frame);
            }
        }
        
    }

    // if the method corresponding to the continuation ID runs
    // mostly on the other machine, then a dummy frame is created

    private boolean thisHostRuns(Closure nextClosure) {
        int place = getPlacement(nextClosure);
        if(place == PLACE_SC) {
            return true;
        }
        if(isRunningOnServer && place == PLACE_S) {
            return true;
        }
        if(!isRunningOnServer && place == PLACE_C) {
            return true;
        }

        return false;
    }

    // These are the only two places where a new stack frame is generated
    // So this should be updating dirtystack XXX
    public StackFrame createStackFrame(int continuationID) {
        return createStackFrame(continuationID, getUniqueSFID());
    }

//    public StackFrame createStackFrame(FrameIndices indices, StackFrameID stackFrameID) {
//        return new StackFrame(indices, stackFrameID);
//    }

    private StackFrame createStackFrame(int continuationID, StackFrameID stackFrameID) {
        FrameIndices indices = getFrameIndices(continuationID);
        if(indices!=null) {
            return new StackFrame(indices, stackFrameID);
        } else {
            throw new WilRuntimeException("Asking for stack frame of continuation " + continuationID + " which does not run on " + hostName);
        }
    }

    // This is the place where dirtyHeap is updated
    // new java objects are not created here
    public WilObject newObject(int classID) {
        WilObject newObject = createInstance(classID);
        objectStore.addToHeap(newObject);
        // updating dirtyheap
        dirtyHeap.put(newObject, new Integer(classID));
        return newObject;
    }

    protected ObjectID getUniqueObjectID() { // TODO Use an unpredictable nonce here
        return new ObjectID(objectIdGenerator.getNextId());
    }

    protected StackFrameID getUniqueSFID() { // TODO Use an unpredictable nonce here
        return new StackFrameID(stackIdGenerator.getNextId());
    }

    public void addHighIntegrityClosure(int continuationID, StackFrameID sfID) {
        List vector = new ArrayList();
        vector.add(new Closure(continuationID, sfID));
        stack.add(vector);
        debug("Adding High Integrity Closure " + continuationID + " in stack frame " + sfID);
    }

    public void addCFIClosure(int continuationID, StackFrameID sfID, List allowed,
        List disallowed) {
        List vector = new ArrayList();
        vector.add(new CFIClosure(continuationID, sfID, allowed, disallowed));
        stack.add(vector);
        debug("Adding CFI Closure " + continuationID + " in stack frame " + sfID);
    }


    private void applyStackFrameUpdate(StackFrameUpdate update, StackFrame frame) throws WilRuntimeException {
        frame.applyUpdate(update, this);
    }

    // XXX maybe indices should be part of the frame, just to keep it easy
    // This method is complementary to applyStackFrameUpdate
    // Given a stack frame, it generates the update that has to be sent over the wire
    protected StackFrameUpdate getStackFrameHeapUpdate(StackFrame frame, HeapUpdate heapUpdate) throws WilRuntimeException { // this method adds an update corresponding to this frame to the dirty stack
        return frame.getUpdate(heapUpdate, this);
    }

    protected IsSerializable referenceToSerializable(Object object, HeapUpdate heapUpdate) {
        if(object instanceof ExternalJavaObject) {
            return ((ExternalJavaObject)object).objectID;
        }
        if(isGWTSerializable(object)) {
            return objectToSerializable(object);
        }
        return referenceToID(object, heapUpdate);
    }

    protected Object serializableToReference(IsSerializable serialized) {
        if(serialized instanceof ObjectID) {
            return idToReference((ObjectID)serialized);
        }
        return serializableToObject(serialized);
    }

    protected ObjectID referenceToID(Object object, HeapUpdate heapUpdate) {
        ObjectID id;
        if(object instanceof ExternalJavaObject) {
            id = ((ExternalJavaObject)object).objectID;
        } else {
            id = objectStore.addToHeap(object);
            if(object instanceof WilObject) {
                Integer classID = (Integer)dirtyHeap.get(object);
                if(classID != null) { // this object is dirty
                    dirtyHeap.remove(object);
                    if(heapUpdate != null) {
                        heapUpdate.objectTypes.put(id, classID);
                    }
                }
            }
        }
        return id;
    }

    public Object idToReference(ObjectID id) {
        if (id == null) return null;
        Object object = objectStore.getObject(id);
        if (object != null) {
            return object;
        } 
        ExternalJavaObject externalObject = new ExternalJavaObject(id);            

        // Put the ExternalJavaObject into the store
        // so that every time we dereference the ObjectID id
        // we get the same object.
        objectStore.putObject(id, externalObject);

        return externalObject;
    }

    public static IsSerializable objectToSerializable(Object object) throws WilRuntimeException {
        if(object == null) return null;

        if (object instanceof IsSerializable) {
            return (IsSerializable) object;
        }

        IsSerializable outgoing;
        if(object instanceof Number) {
            outgoing = new WilNumber((Number)object);
        } else if(object instanceof String) {
            outgoing = new WilString((String)object);
        } else if(object instanceof Boolean) {
            outgoing = new WilBoolean((Boolean)object);
        } else throw new WilRuntimeException("Unserializable object");

        return outgoing;

    }

    public static Object serializableToObject(IsSerializable serializable) throws WilRuntimeException {
        if(serializable == null) return null;
        Object object;
        if(serializable instanceof WilNumber) {
            object = ((WilNumber)serializable).toNumber();
        } else if(serializable instanceof WilString) {
            object = ((WilString)serializable).toString();
        } else if(serializable instanceof WilBoolean) {
            object = ((WilBoolean)serializable).toBoolean();
        } else {
            object = serializable;
        }

        return object;
    }

    private void applyHeapUpdate(HeapUpdate heapUpdate) {
        if(heapUpdate == null) return;
        for(Iterator it = heapUpdate.objectTypes.keySet().iterator(); it.hasNext();) {
            ObjectID oid = (ObjectID) it.next();

            // XXX ignore objects that are already in the store
            if (objectStore.containsID(oid)) continue;

            // each type in the source program is assigned an integer value
            Integer value = (Integer) heapUpdate.objectTypes.get(oid);
            WilObject obj = createInstance(value.intValue());
            objectStore.putObject(oid, obj);
        }
    }


    public ExtendedClosure getStackHeapUpdate(int minDepthOfStack, Closure outgoing) throws WilRuntimeException {
        // look at the stack of closures since the last message from the other machine
        // and create an update
        int size = stack.size() - minDepthOfStack;
        StackUpdate toReturn = new StackUpdate(size + 1);
        toReturn.start = getMinDepthOfStack();
        HeapUpdate heapUpdate = new HeapUpdate();
        for(int i = minDepthOfStack; i < stack.size(); i++) {
            List closures = (List)stack.get(i);
            toReturn.stackDelta[i-minDepthOfStack] = new WireClosure[closures.size()];
            int k = 0;
            for(Iterator it = closures.iterator(); it.hasNext();) {
                Closure closure = (Closure) it.next();
                toReturn.stackDelta[i-minDepthOfStack][k++] = getWireClosure(closure, heapUpdate); 
                // check all stack frame IDs being referred to and push them into the stack update
                // once the update has been pushed, we can remove it from the dirty pile

                // stackFrameID will be null for the entry point closure, so don't
                // add this frame to the update (but add the closure).
                if (closure.stackFrameID != null) {
                    StackFrame nextFrame = (StackFrame)frames.get(closure.stackFrameID);

                    if(toReturn.framesDelta.get(closure.stackFrameID) == null && nextFrame != null) {
                        // add the update to the map only if the update is not already present
                        // we don't want to generate an update twice for a stack frame
                        // otherwise the dirty bits will be obliterated
                        
                        StackFrameUpdate nextUpdate = getStackFrameHeapUpdate(nextFrame, heapUpdate);
                        toReturn.framesDelta.put(closure.stackFrameID, nextUpdate);
                    }
                }
            }
        }

        toReturn.stackDelta[size] = new WireClosure[1];
        toReturn.stackDelta[size][0] = getWireClosure(outgoing, heapUpdate);

        if (outgoing.stackFrameID != null) {
            StackFrame nextFrame = (StackFrame)frames.get(outgoing.stackFrameID);
            if(toReturn.framesDelta.get(outgoing.stackFrameID) == null && nextFrame != null) {
                // add the update to the map only if the update is not already present
                // we don't want to generate an update twice for a stack frame
                // otherwise the dirty bits will be obliterated
                StackFrameUpdate nextUpdate = getStackFrameHeapUpdate(nextFrame, heapUpdate);
                toReturn.framesDelta.put(outgoing.stackFrameID, nextUpdate);
            }
        }

        return new ExtendedClosure(getWireClosure(outgoing, heapUpdate), 
            toReturn, heapUpdate, stackIdGenerator.getSeed(), 
            objectIdGenerator.getSeed(), subSession);
    }

    public void checkPointStackSize() {
        minDepthOfStack = stack.size();
        // reset all dirty bits
        for(Iterator it = frames.values().iterator(); it.hasNext();) {
            StackFrame frame = (StackFrame)it.next();
            frame.clearDirtyBits();
        }
    }

    /**
     * This is the only public method in this class and is the method which each runtime
     * invokes on the other
     * On the client, this is always registered as the callback to every AJAX call
     * On the server, this is the method exported by the servlet
     * @param closure
     * The extended closure (closure with updates to the stack and heap) which is to be
     * executed next on this machine
     * @return
     * The extended closure that is to be executed on the other machine
     * @throws WilRuntimeException
     */
//  public ExtendedClosure getClosure(ExtendedClosure closure) throws WilRuntimeException {


//  // Save the current depth of stack, to construct a StackUpdate later
//  // XXX now done in WilRuntimeClient and WilRuntimeServiceServlet subclass
//  //minDepthOfStack = stack.size();
//  entry(closure);
//  checkPointStackSize();

//  // XXX Expect dispatch to update minDepthOfStack to the lowest point in the stack reached
//  Closure outgoing = dispatch(getLocalClosure(closure.incoming));

//  // XXX check if outgoing is S,C or B and operate accordingly
//  // XXX add the stack frame to the dirty stack

//  // if an object ID that is being sent is in the dirty heap, then
//  // add it to the heapUpdate


//  return getStackHeapUpdate(minDepthOfStack, outgoing);
//  }

    public void entry(ExtendedClosure closure) throws WilRuntimeException {
        if(stackIdGenerator.getSeed() < closure.globalSFID) {
            stackIdGenerator.setSeed(closure.globalSFID);
        }

        if(objectIdGenerator.getSeed() < closure.globalOID) {
            objectIdGenerator.setSeed(closure.globalOID);
        }

        // First update the heap, so that the stack frame updates would work fine
        applyHeapUpdate(closure.heapUpdate);

        // Now update the stack
        applyStackUpdate(closure.stackUpdate, getLocalClosure(closure.incoming));
        // The stack and the heap have been updated. Now execute the closure.


    }

    public int getMinDepthOfStack() {
        return minDepthOfStack;
    }

    private boolean isGWTSerializable(Object object) {
        if(object instanceof String) {
            return true;
        }
        if(object instanceof Number) {
            return true;
        }
        if(object instanceof Boolean) {
            return true;
        }
        if(object instanceof IsSerializable) {
            return true;
        }
        return false;
    }

    /**
     * 
     * @param outgoing
     * @param heapUpdate
     * @return returns a representation of the closure that can be sent over the wire to the other host
     * no loss of information happens when translating a return closure or a catch handler closure
     * no loss of information happens for CFI closures and high integrity closures too, but that can be optimized
     * when sending stack updates to the client, since the client need not know the internals of the CFI and HIC 
     * @throws WilRuntimeException
     */
    // TODO change usages of isRunningOnServer so that the subclasses WilRuntimeClient and WilRuntimeServer are used
    public WireClosure getWireClosure(Closure outgoing, HeapUpdate heapUpdate) 
    throws WilRuntimeException {
        IsSerializable object = referenceToSerializable(outgoing.argument, heapUpdate);
        WireClosure wc = new WireClosure(outgoing.continuationID, 
            outgoing.stackFrameID, object);

        if(isRunningOnServer && outgoing instanceof CFIClosure) {
//          wc.type = outgoing.type;
            wc.isCFI = true;
        } else if (!isRunningOnServer && outgoing instanceof CFIClosure) {
            CFIClosure outgoingCFI = (CFIClosure)outgoing;

            if (outgoingCFI.exceptionThrown != null) {
                wc.argument = outgoingCFI.exceptionThrown;
            }

//          wc.type = outgoingCFI.type;
            wc.isCFI = true;
        }

        return wc;
    }

    public Closure getLocalClosure(WireClosure incoming) throws WilRuntimeException {
        Closure toReturn = null;

        if(!isRunningOnServer && incoming.isCFI) {
            toReturn = new CFIClosure(incoming.continuationID, incoming.stackFrameID, null, null);
        } else if (isRunningOnServer && incoming.isCFI) {
            CFIClosure cfi = new CFIClosure(incoming.continuationID, incoming.stackFrameID, null, null);
            cfi.exceptionThrown = (ObjectID) incoming.argument;
            toReturn = cfi;
        } else {
            toReturn = new Closure(incoming.continuationID, incoming.stackFrameID);
        }

        Object serializable = serializableToObject(incoming.argument);
        if(serializable instanceof ObjectID) {
            toReturn.argument = idToReference((ObjectID)serializable);
        } else {
            toReturn.argument = serializable;
        }

        // also, restore the type here!
//      toReturn.type = incoming.type;
//      toReturn.type = getContinuationType(incoming.continuationID);

        return toReturn;
    }

    protected int getPlacement(Closure closure) {
//      if(closure instanceof CFIClosure) {
//      return PLACE_S;
//      }
        return getPlacementImpl(closure.continuationID);
    }

    protected int getPlacement(WireClosure closure) {
        if(closure.isCFI) {
            return PLACE_S;
        }
        return getPlacementImpl(closure.continuationID);
    }

    protected Closure getClosureFromException(ExceptionResult result, boolean isHighContext) {
        result.exceptionThrown.printStackTrace();
        throw new WilRuntimeException("Exceptions can only be thrown on the server for now");
    }

    /**
     * Perform the access control checks present in the CFI Closure and execute
     * the body CFI Closures are executed only while walking down the stack on
     * the server, or when it is requested to be executed by the client.
     */
    protected boolean CFIClosureApproval(CFIClosure cfiClosure) {
        // should be overridden WilRuntimeServer
        throw new WilRuntimeException("executeCFIClosure can only be called " +
        "on the server");
    }


    private Closure getClosureFromNormalResult(Closure result, boolean isHighContext) {
        Closure toReturn = result;

        int index = stack.size() - 1;
        List closureVector = (List) stack.get(index);
        Closure topClosure = (Closure) closureVector.get(0);

        if (topClosure instanceof CFIClosure && topClosure.equals(result)) {

            // if its in a high context then CFI closures are not required
            CFIClosure cfiClosure = (CFIClosure)topClosure;
            cfiClosure.exceptionThrown = null;

            if (isRunningOnServer) {
                popStack("Popping CFI block " + result.continuationID);

                if (!isHighContext && !CFIClosureApproval(cfiClosure)) {
                    throw new WilRuntimeException("CFI Approval not obtained");
                }
            } else {
                toReturn = topClosure;
            }
        }

        return toReturn;
    }

    private Closure getClosureFromReturn(ReturnResult result, boolean isHighContext) {
        debug("return;");

        Closure toReturn = null;
        int index = stack.size() - 1;
        do {
            List closureVector = (List) stack.get(index);
            Closure topClosure = (Closure) closureVector.get(0);

//          if(closureVector.size() != 1) { // XXX This code should be here depending on whether the compiler inserts a deregister before a return statement
//          throw new WilRuntimeException("Top of stack does not contain a return closure");
//          } // size of the closureVector will not be 1 only in the case of catch handlers
//          if(topClosure instanceof ExceptionClosure) {
//          throw new WilRuntimeException("Top of stack does not contain a return closure");
//          // XXX This code should be here depending on whether the compiler inserts a deregister before a return statement
//          }

//          System.err.println(topClosure.continuationID + " type: " + topClosure.type);

            if (closureVector.size() == 1 && 
                getContinuationType(topClosure.continuationID) == Closure.RETURN) {
                if(!isHighIntegrity(topClosure)) {
                    // if the return closure is high integrity, let it remain on the stack
                    popStack("Return closure found");
                }
                toReturn = topClosure; // send the closure result to the other machine

                toReturn.argument = result.returnValue; // the reverse conversion has to be done somewhere here
//              toReturn.isPrimitive = ((ReturnResult)outgoing).isPrimitive; // isprimitive flag is not necessary since the recipient knows what she expects

                // Now that we know which closure is to be executed, add the appropriate high integrity closure onto the stack
//              if(isHighContext) {
//              pushPostDominator(toReturn, isHighContext);
//              }
                // not needed since we have a high-integrity no-op after every high integrith method call

                // XXX this does absolutely nothing
                /*
                if(topClosure instanceof CFIClosure && !isHighContext) {
                    CFIClosure cfiClosure = (CFIClosure)topClosure;
                    cfiClosure.exceptionThrown = null;                
                }
                 */

                return toReturn;
            } else if(!isHighContext && topClosure instanceof CFIClosure) { // closureVector.size() == 1 
                // if its in a high context then CFI closures are not required
                CFIClosure cfiClosure = (CFIClosure)topClosure;
                cfiClosure.exceptionThrown = null;
                if(isRunningOnServer) {
                    if(!CFIClosureApproval(cfiClosure)) {
                        throw new WilRuntimeException("CFI Approval not obtained");
                    }
                } else {
                    return topClosure; // need to go back to the server to approve this cfi closure
                }

            } else if(!isHighContext && isRunningOnServer &&
                !(getClosureType(topClosure) == Closure.HANDLER)
                && isHighIntegrity(topClosure)) { 
                throw new WilRuntimeException("Control Flow Integrity Violated");
            }
            popStack("Trying to find Return closure");
            index--;
            if(index < 0) {
                break;
            }
        } while(true);
        throw new WilRuntimeException("Returning to a non-existent method");
    }

    protected void popStack(String message) {
        stack.remove(stack.size() - 1);
        debug("Popping Stack - " + message);
        if(stack.size() < minDepthOfStack) {// here's where we update minDepthOfStack
            minDepthOfStack = stack.size();
        }
    }

    /**
     * Will be called after the execution of every closure. Do some side-effects bookkeeping here
     * The closure this method returns should have the argument field set, if it is a return
     * closure or a handler closure.
     * @param closureResult
     * @return
     * @throws WilRuntimeException
     */
    public Closure getClosureFromResult(ClosureResult closureResult, Closure previous) throws WilRuntimeException {
        Closure toReturn = null;
        boolean isHighIntegrity = isHighIntegrity(previous);
        if(closureResult instanceof Closure) {
            toReturn = (Closure)closureResult;
        } else if(closureResult instanceof ExceptionResult) {
            toReturn = getClosureFromException((ExceptionResult)closureResult, isHighIntegrity);
        } else if(closureResult instanceof ReturnResult) {
            toReturn = getClosureFromReturn((ReturnResult)closureResult, isHighIntegrity);
        } else if(closureResult instanceof MethodCallResult) {
            MethodCallResult result = (MethodCallResult)closureResult;
//          result.returnClosure.type = Closure.RETURN;
            List vector = new ArrayList();
            vector.add(result.returnClosure);
            stack.add(vector);
            debug(hostName + ": Adding Return Closure " + result.returnClosure);
            frames.put(result.arguments.stackFrameID, result.arguments); // add the stack frame to the runtime right here
            toReturn = new Closure(result.continuationID, result.arguments.stackFrameID);

            // add the first high integ closure within the method body now
            if(isHighIntegrity) {
                pushPostDominator(toReturn, isHighIntegrity);
            }
        }
        if(stack.size() < minDepthOfStack) {// here's where we update minDepthOfStack
            minDepthOfStack = stack.size();
        }

        // so far from the beginning of the method, no high integrity closure should
        // be popped off the stack, since that will now be used in checkClosure
        // check closure will happen from either WilRuntimeClient or WilRuntimeServer
//      if(!isHighIntegrity && !checkClosure(toReturn)) {
//      throw new WilRuntimeException("Attempt to execute a high integrity closure not on the stack, from a low integrity context");
//      }
        return toReturn;
    }


    // Beware! Can only be called when previous closure executed was high integrity
    private void pushPostDominator(Closure lowIntegClosure, boolean isPreviousHighIntegrity) {
        if(!isPreviousHighIntegrity) {
            throw new WilRuntimeException("Pushing a high integrity closure in a low integrity context");
        }
        if(!isHighIntegrity(lowIntegClosure)) { // we need to push something on the stack
            Integer postDomInteger = (Integer)postDominatorMap.get(new Integer(lowIntegClosure.continuationID));
            if(postDomInteger != null) {
                int postDom = postDomInteger.intValue();
                addHighIntegrityClosure(postDom, lowIntegClosure.stackFrameID); // this stack frame id should work, since we won't cross method boundaries here
            }                        
        }
    }

    /**
     * The Vector contains a bunch of ExceptionClosures 
     */
    public void registerCatchHandlers(List handlers) {
//        System.err.println(hostName + ": Adding catch handlers onto the stack");
        stack.add(handlers);
    }

    public void deRegisterHandlers() throws WilRuntimeException {
        int index = stack.size() - 1;
        do {
            List closureVector = (List)stack.get(index);
            if(closureVector.size() == 1 && !(closureVector.get(0) instanceof ExceptionClosure) 
                && getContinuationType(((Closure)closureVector.get(0)).continuationID) != Closure.HANDLER) {
                // this is not a catch handler
                Closure topClosure = (Closure)closureVector.get(0);
                if(isRunningOnServer && isHighIntegrity(topClosure)) { 
                    throw new WilRuntimeException("Control Flow Integrity Violated");
                }
                popStack("Trying to deregister handlers");

            } else {
                for(Iterator it = closureVector.iterator(); it.hasNext();) {
                    Closure catchHandler = (Closure)it.next();
//                  if(isRunningOnServer && isHighIntegrity(catchHandler)) { 
//                  throw new WilRuntimeException("Control Flow Integrity Violated");
//                  }
                    // popping catch handlers is always ok
                }
                popStack("Handlers deregistered");
                // handlers removed
                break;
            }

            index--;
        } while (true);
    }

    // XXX for debugging only
    public void printStackFrame(StackFrameID id) {
        StackFrame sf = (StackFrame)frames.get(id);
        System.err.println(sf.toString());
    }

    // XXX for debugging only
    public void printStack() {
        System.err.print("[");
        for (Iterator it = stack.iterator(); it.hasNext(); ) {
            List closureVector = (List)it.next();
            System.err.print("(");
            for(Iterator jt = closureVector.iterator(); jt.hasNext();) {
                Closure c = (Closure)jt.next();
                String type = "";

                switch (getContinuationType(c.continuationID)) {
                case Closure.RETURN:
                    type = "R"; break;
                case Closure.HANDLER:
                    type = "E"; break;
                default:
                    if (c instanceof CFIClosure) {
                        type = "C";
                    } else if (isAutoEndorsed(c.continuationID)) {
                        type = "A";
                    } else if (isHighIntegrity(c)) {
                        type = "H";
                    }
                }

                System.err.print(c.continuationID + type + " ");
            }
            System.err.print(")");
            if(it.hasNext()) {
                System.err.print(",");
            }
        }
        System.err.println("]");
    }

    protected void debug(String s) {
//        if(DEBUG_LEVEL > 0) {
//            System.err.println(hostName + ": " + s);
//        } else if(DEBUG_LEVEL > 1) {
//            printStack();
//        }
    }
}
