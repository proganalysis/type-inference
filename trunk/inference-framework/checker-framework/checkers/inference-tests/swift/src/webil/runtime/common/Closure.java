package webil.runtime.common;


/**
 * @author kvikram 
 *         Lightweight representation of a closure. Just a pair of
 *         continuation ID and stack frame ID.
 */
public class Closure extends AbstractClosure {
//  an object ID or a serializable value
    public Closure(int continuationID, StackFrameID stackFrameID) {
        super(continuationID, stackFrameID);
    }

    /* The argument makes sense only for return and exception closures
     * For normal closures, this will be null
     */
    public Object argument;
}