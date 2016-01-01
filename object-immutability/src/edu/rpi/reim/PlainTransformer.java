package edu.rpi.reim;

import java.lang.annotation.Annotation;
import java.util.Map;
import java.util.Set;

import soot.Body;
import edu.rpi.AnnotatedValue;
import edu.rpi.AnnotationUtils;
import edu.rpi.Constraint;
import edu.rpi.ConstraintSolver.FailureStatus;
import edu.rpi.InferenceTransformer;
import edu.rpi.InferenceVisitor;
import edu.rpi.ViewpointAdapter;

public class PlainTransformer extends InferenceTransformer {

	private Set<Annotation> sourceAnnos;
	
	public PlainTransformer() {
		sourceAnnos = AnnotationUtils.createAnnotationSet();
	}
	
	@Override
	protected AnnotatedValue createFieldAdaptValue(AnnotatedValue context,
			AnnotatedValue decl, AnnotatedValue assignTo) {
		return decl;
	}

	@Override
	protected AnnotatedValue createMethodAdaptValue(AnnotatedValue receiver,
			AnnotatedValue decl, AnnotatedValue assignTo) {
		return decl;
	}

	@Override
	protected InferenceVisitor getInferenceVisitor(InferenceTransformer t) {
		return new InferenceVisitor(t);
	}

	@Override
	protected boolean isAnnotated(AnnotatedValue v) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public ViewpointAdapter getViewpointAdapter() {
		return new ViewpointAdapter() {
			public Annotation adaptField(Annotation context, Annotation decl) { return decl; }
			public Annotation adaptMethod(Annotation context, Annotation decl) { return decl; }
		};
	}

	@Override
	public Set<Annotation> getSourceLevelQualifiers() {
		// TODO Auto-generated method stub
		return sourceAnnos;
	}

	@Override
	public int getAnnotationWeight(Annotation anno) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean isStrictSubtyping() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public FailureStatus getFailureStatus(Constraint c) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "plain";
	}

	/*
	@Override
	protected void internalTransform(Body arg0, String arg1,
			Map<String, String> arg2) {
		// TODO Auto-generated method stub
		
	}
	*/

}
