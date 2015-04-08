package webil.runtime.server;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import webil.runtime.client.WilService;
import webil.runtime.common.ExtendedClosure;
import webil.runtime.common.ObjectID;
import webil.runtime.common.WilRuntimeException;
import webil.runtime.common.WilServiceException;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * Abstract base implementation of WilService. For each application, getRuntime
 * should be overridden to return the appropriate WilRuntime implementation.
 */
public abstract class WilServiceServlet extends RemoteServiceServlet
        implements WilService {
    
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
     * Returns a new instance of the appropriate WilRuntime implementation.
     * @return a new instance of WilRuntime.
     */
    protected abstract WilRuntimeServer getNewRuntime() throws WilRuntimeException;
    
    private static final ThreadLocal<WilRuntimeServer> localRuntime = new ThreadLocal<WilRuntimeServer>();
    
    public static WilRuntimeServer getThreadLocalRuntime() {
        return (WilRuntimeServer) localRuntime.get();
    }
    
    public ObjectID getClientPrincipal(int subSession) throws WilServiceException {
        try {
            HttpServletRequest req = getThreadLocalRequest();
            WilRuntimeServer rt = runtime(req.getSession(), subSession);
            localRuntime.set(rt);
            
            synchronized (rt) {
                return rt.getClientPrincipalID();
            }
        } catch (Exception e) {
            throw toServiceException(e);
        }
    }
    
    /**
     * Invokes the given closure on the server, and returns the next closure to
     * be executed on the client.
     */
    public final ExtendedClosure getClosure(ExtendedClosure eclosure) 
            throws WilServiceException {
        try {
            HttpServletRequest req = getThreadLocalRequest();
            WilRuntimeServer rt = runtime(req.getSession(), eclosure.subSession);        
            localRuntime.set(rt);
    
            synchronized (rt) {
                return rt.executeClosures(eclosure);
            }
        } catch (Exception e) {
            throw toServiceException(e);
        }
    }
    
    protected final WilRuntimeServer runtime(HttpSession session, 
            int subsessionID) throws WilRuntimeException {
        synchronized (session) {
            WilRuntimeServer rt = (WilRuntimeServer) session.getAttribute(
                "runtime$" + subsessionID);
            
            if (rt == null) {
                // create the runtime
                rt = getNewRuntime();
                System.err.println("New server runtime created for session " + 
                    session.getId() + "-" + subsessionID);
                String uniqueID = session.getId() + '$' + subsessionID; 
                rt.setUniqueID(uniqueID);
                session.setAttribute("runtime$" + subsessionID, rt);
            }
            
            return rt;
        }
    }

    protected WilServiceException toServiceException(Throwable e) {
        String s = e + "\n";
        
        for (StackTraceElement t : e.getStackTrace()) {
            s += "\tat " + t + "\n";
        }
        
        return new WilServiceException(s);
        
    }
    
}
