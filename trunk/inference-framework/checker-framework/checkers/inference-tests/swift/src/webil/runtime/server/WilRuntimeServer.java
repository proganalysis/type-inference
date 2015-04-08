package webil.runtime.server;

import java.math.BigInteger;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import webil.runtime.common.*;

public abstract class WilRuntimeServer extends WilRuntime {
    protected int currentPlace;

    private boolean withinStoSC = false;
    private Closure sToSCClosure = null;

    protected final Random rand = new Random();

    private String uniqueID = null; 
    private WilObject clientPrincipal; 

    // Generators for secure unique ids to be used for objects and stack frames.
    protected final SecureIdGenerator secureObjectIdGenerator;
    protected final SecureIdGenerator secureStackIdGenerator;

    public ObjectID getClientPrincipalID() {
        currentPlace = PLACE_S;
        return referenceToID(getClientPrincipal(), null);
    }


    public WilRuntimeServer() {
        super(true);
        secureObjectIdGenerator = 
            new SecureIdGenerator(20, new BigInteger(159, rand));
        secureStackIdGenerator = 
            new SecureIdGenerator(20, new BigInteger(159, rand));
    }

    public synchronized final void setUniqueID(String uniqueID) {
        if (this.uniqueID == null) {
            this.uniqueID = uniqueID;
        }        
    }
    public synchronized final WilObject getClientPrincipal() {
        if (clientPrincipal == null) {
            clientPrincipal = createInstance(getClassID("webil.runtime.common.WebILClientPrincipal"));
        }
        return clientPrincipal;
    }

    public static WilRuntimeServer getRuntime() {
        return WilServiceServlet.getThreadLocalRuntime();
    }

    protected boolean first = true;
    protected boolean prevTrusted = false;

    public ExtendedClosure executeClosures(ExtendedClosure eclosure) {
        if(withinStoSC) {
            if(!eclosure.withinStoSC) {
                throw new WilRuntimeException("Client not replying with the right message for S->SC");
            } // don't apply the stack and heap updates here
        } else {
            prevTrusted = false;
            entry(eclosure); // applies stack and heap updates
        }
        checkPointStackSize(); // checkpoints stack size
        Closure closure;
        if(withinStoSC) {
            closure = sToSCClosure;
        } else {
            closure = getLocalClosure(eclosure.incoming);
        }
        currentPlace = getPlacement(closure);
        while (currentPlace != WilRuntime.PLACE_C) {
            // First check if the closure is legit
            boolean currentTrusted = isHighIntegrity(closure);
//          System.err.println(prevTrusted + " " + currentTrusted);
//          System.err.println(closure.continuationID + " has type " + getContinuationType(closure.continuationID));
//          printStack();
//          System.err.print("server: " + closure.continuationID + " -> ");

            if (first) first = false;
            else {
                
                if((prevTrusted || withinStoSC) && isCFI(closure)) {
                    // simply remove the cfi closure from the stack
                    popStack("Popping CFI Closure before execution");
                } else if(!prevTrusted && currentTrusted && !checkClosure(closure)) {
                    throw new WilRuntimeException("Invalid closure execution request");
                }
            }

            if(withinStoSC) {
                sToSCClosure = null;
                withinStoSC = false;
            }


            prevTrusted = currentTrusted;

            debug("Executing Closure " + closure);
            closure = dispatch(closure);

            int nextPlace = getPlacement(closure);

            if (currentPlace == WilRuntime.PLACE_S && nextPlace == WilRuntime.PLACE_SC) {
                withinStoSC = true;
                sToSCClosure = closure;
                break;
            }

            currentPlace = nextPlace;
        }

        // set current place back to S so that stack/heap updates create secure
        // ids for objects.
        currentPlace = PLACE_S;
//      System.err.print("Stack before back to client: "); 
//      printStack();
        ExtendedClosure toReturn = getStackHeapUpdate(getMinDepthOfStack(), closure);
        toReturn.withinStoSC = withinStoSC;
        toReturn.prevTrusted = prevTrusted;
        debug("Sending message to client for Closure " + toReturn + ": S->SC =  " + withinStoSC);
        return toReturn;
    }

    class StoSCState {
        int currentPlace;
        int nextPlace;
        Closure closure;
        boolean prevTrusted;
    }

    protected Closure getClosureFromException(ExceptionResult result, boolean isHighContext) {
        // stack will be updated here too
        int index = stack.size() - 1;
        while (true) {
            List<?> closureVector = (List<?>) stack.get(index);
            Closure topClosure = (Closure) closureVector.get(0);
            if(closureVector.size() != 1 || (closureVector.size() == 1
                && topClosure instanceof ExceptionClosure
//              && ((Closure)closureVector.get(0)).type != Closure.HANDLER // XXX What if the closure type is == HANDLER?  
            )) {               
                // bunch of exception handlers
                for(Iterator<?> it = closureVector.iterator(); it.hasNext();) {
                    ExceptionClosure exClosure = (ExceptionClosure) it.next();
                    Throwable thrownException = result.exceptionThrown;
                    boolean foundException = false;
                    try {
                        foundException = Class.forName(exClosure.exceptionType).isAssignableFrom(thrownException.getClass());
                    } catch (ClassNotFoundException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } 
                    if(foundException) { // found a good catch handler
//                      if(thrownException.isInstanceOf(exClosure.exceptionType)) { 
                        exClosure.argument = thrownException;
//                        stack.remove(index);
                        popStack("Found an exception handler for exception " + thrownException);
                        if(!isHighContext) {
                            if(isHighIntegrity(exClosure)) {
                                throw new WilRuntimeException("A low integrity throw being handled by a high integrity catch: " + exClosure);
                            }
//                          // add the first high integrity block in exClosure to the stack
//                          // XXX The map should return the same continuation block if the block is already high integrity
//                          Integer postDomInteger = (Integer)postDominatorMap.get(new Integer(exClosure.continuationID));
//                          if(postDomInteger != null) {
//                          int postDom = postDomInteger.intValue();
//                          addHighIntegrityClosure(postDom, exClosure.stackFrameID); 
//                          // this is required in the case where the first statement in the catch block is low integrity
//                          }
                        }
                        return exClosure;
                    }
                }
            } else if(!isHighContext && topClosure instanceof CFIClosure) { // closureVector.size() == 1 
                // if its in a high context then CFI closures are not required
                CFIClosure cfiClosure = (CFIClosure)topClosure;
                cfiClosure.exceptionThrown = referenceToID(result.exceptionThrown, null);
                if(isRunningOnServer) {
//                  stack.remove(index); // we're on the server, so we just pop it off and execute it
                    if(!CFIClosureApproval(cfiClosure)) {
                        throw new WilRuntimeException("CFI Approval not obtained");
                    }

                } else {
                    return topClosure; // need to go back to the server to approve this cfi closure
                }

            } else if(isRunningOnServer && !isHighContext && isHighIntegrity(topClosure)) { 
                throw new WilRuntimeException("Control Flow Integrity Violated");
            }

//            stack.remove(index);
            popStack("Trying to find an exception handler for " + result.exceptionThrown);
            index--;
            if(index < 0) {
                break;
            }
        }
        result.exceptionThrown.printStackTrace();
        throw new WilRuntimeException("Uncaught Exception: "
            + result.exceptionThrown.getMessage());
    }


    protected boolean CFIClosureApproval(CFIClosure cfiClosure) {
        try {
            Throwable exceptionObject;
            exceptionObject = (Throwable)objectStore.getObject(cfiClosure.exceptionThrown);
            boolean isAllowed = false;
            for(Iterator<?> it = cfiClosure.allowedEntries.iterator(); it.hasNext();) {
                String allowedExceptionType = (String)it.next();
                if(exceptionObject != null) {
                    if(Class.forName(allowedExceptionType).isAssignableFrom(exceptionObject.getClass())) {
                        isAllowed = true;
                        break;
                    }
                } else {
                    if(allowedExceptionType.equals("#normal#") || allowedExceptionType.equals("#return#")) {
                        isAllowed = true;
                    }
                }
            }
            if(!isAllowed) {
                throw new WilRuntimeException("CFI Closure reports violation of control flow integrity");
            }
            for(Iterator<?> it = cfiClosure.disallowedEntries.iterator(); it.hasNext();) {
                String disallowedExceptionType = (String)it.next();
                if(exceptionObject != null) {
                    if(Class.forName(disallowedExceptionType).isAssignableFrom(exceptionObject.getClass())) {
                        throw new WilRuntimeException("CFI Closure reports violation of control flow integrity");
                    }
                } else {
                    if(disallowedExceptionType.equals("#normal#") || disallowedExceptionType.equals("#return#")) {
                        throw new WilRuntimeException("CFI Closure reports violation of control flow integrity");                        
                    }
                }
            }
            debug(" Executing CFI Closure - " + cfiClosure);
            return true;
        } catch (ClassCastException e) {
            throw new WilRuntimeException("CFI Closure does not have an exception object's ID");
        } catch (ClassNotFoundException e) {
            throw new WilRuntimeException("CFI Closure mentions an exception type string for an exception that is not available");
        }
    }

    @Override
    protected ObjectID getUniqueObjectID() {
        if (currentPlace == PLACE_S) {
            return new ObjectID(secureObjectIdGenerator.getNextId());
        } else {
            return new ObjectID(objectIdGenerator.getNextId());
        }
    }

    @Override
    protected StackFrameID getUniqueSFID() {
        if (currentPlace == PLACE_S) {
            return new StackFrameID(secureStackIdGenerator.getNextId());
        } else {
            return new StackFrameID(stackIdGenerator.getNextId());
        }
    }


}
