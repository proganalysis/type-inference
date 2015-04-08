package webil.runtime.common;

public class WilRuntimeException extends RuntimeException {

    /**
     * Exceptions specific to the WebIL Runtime
     */
//    private static final long serialVersionUID = 1L;

    public WilRuntimeException(String message) {
        super(message);
    }

    public WilRuntimeException() {}

}
