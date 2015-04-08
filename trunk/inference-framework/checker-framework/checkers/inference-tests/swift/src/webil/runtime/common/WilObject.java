package webil.runtime.common;

/**
 * 
 * @author kvikram
 * All objects in the system should implement this interface.
 * Methods here are used for dynamic dispatch of methods and for
 * querying for the runtime type of an object
 */
public interface WilObject {
	
	/**
	 * Implements a table for every object, returning a continuation
	 * ID for each method in that object. A method is represented as
	 * a string unique for every method
	 * 
	 * @param methIdent
	 *  The String representation of the method declaration
	 *  (including the static types of the arguments)
	 * @return
	 *  An integer that is the continuation ID corresponding to the
	 *  entry point of the method
	 */
	public int getContinuation(String methodDescriptor);
	
	/**
	 * 
	 * @return
	 * returns what the runtime type of this object would have been in the
	 * source language, represented as a String of the fully qualified name 
	 */
	public String getTypeString();
        // uncommented this for exception handling
        
        /**
         * This method will be present on every WilObject and will be used
         * in instanceof checks and during exception handling
         * Will check if the argument matches the output of getTypeString
         * and calls the superclass in case it doesn't
         */
        public boolean isInstanceOf(String anotherType);
	
}