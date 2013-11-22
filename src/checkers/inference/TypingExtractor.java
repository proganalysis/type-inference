/**
 * 
 */
package checkers.inference;

import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

import checkers.types.AnnotatedTypeMirror;

/**
 * @author huangw5
 *
 */
public interface TypingExtractor {

	/**
	 * Print all variables
	 * @param out
	 */
	public void printAllVariables(PrintWriter out);
	
	/**
	 * Annotate the inferred type with {@code elt}
	 * @param elt
	 * @param type
	 */
//	public void annotateInferredType(Element elt, AnnotatedTypeMirror type);
	
	/** 
	 * Annotate the inferred type with {@code identifier} generated 
	 * by {@link InferenceUtils}
	 * @param identifier
	 * @param type
	 */
	public void annotateInferredType(String identifier, AnnotatedTypeMirror type);
	
	
	/** 
	 * Add the inferred type with {@code identifier} generated 
	 * by {@link InferenceUtils}
	 * @param identifier
	 * @param type
	 */
	public void addInferredType(String identifier, AnnotatedTypeMirror type);
	
	/**
	 * Get the inferred reference by identifier
	 * @param identifier
	 * @return
	 */
	public Reference getInferredReference(String identifier);

	/**
	 * Return inferred references
	 * @return
	 */
	public List<Reference> getInferredReferences();
	
	/**
	 * Return inferred solution 
	 * @return
	 */
	public Map<String, Reference> getInferredSolution();

	/**
	 * Extract concrete typing
	 * @param typeErrorNum TODO
	 */
	public List<Constraint> extractConcreteTyping(int typeErrorNum);
}
