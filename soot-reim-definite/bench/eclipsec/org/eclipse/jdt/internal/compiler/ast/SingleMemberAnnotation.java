/*******************************************************************************
 * Copyright (c) 2000, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jdt.internal.compiler.ast;
//import checkers.inference.ownership.quals.*;

import org.eclipse.jdt.internal.compiler.ASTVisitor;
import org.eclipse.jdt.internal.compiler.lookup.BlockScope;
import org.eclipse.jdt.internal.compiler.lookup.CompilationUnitScope;

/**
 * SingleMemberAnnotation node
 */
public class SingleMemberAnnotation extends Annotation {
	
	public Expression memberValue;
	private MemberValuePair[] singlePairs; // fake pair set, only value has accurate positions

	public SingleMemberAnnotation(TypeReference type, int sourceStart) {
		this.type = type;
		this.sourceStart = sourceStart;
		this.sourceEnd = type.sourceEnd;
	}
	
	/**
	 * @see org.eclipse.jdt.internal.compiler.ast.Annotation#memberValuePairs()
	 */
	public MemberValuePair[] memberValuePairs() {
		if (this.singlePairs == null) {
			this.singlePairs =  
				new MemberValuePair[]{ 
					new MemberValuePair(VALUE, this.memberValue.sourceStart, this.memberValue.sourceEnd, this.memberValue)
				};
		}
		return this.singlePairs;
	}
	
	public StringBuffer printExpression(int indent, StringBuffer output) {
		super.printExpression(indent, output);
		output.append('(');
		this.memberValue.printExpression(indent, output);
		return output.append(')');
	}
	
	public void traverse(ASTVisitor visitor, BlockScope scope) {
		if (visitor.visit((/*@OwnPar*/ /*@NoRep*/ SingleMemberAnnotation)this, scope)) {
			if (this.memberValue != null) {
				this.memberValue.traverse(visitor, scope);
			}
		}
		visitor.endVisit((/*@OwnPar*/ /*@NoRep*/ SingleMemberAnnotation)this, scope);
	}
	public void traverse(ASTVisitor visitor, CompilationUnitScope scope) {
		if (visitor.visit((/*@OwnPar*/ /*@NoRep*/ SingleMemberAnnotation)this, scope)) {
			if (this.memberValue != null) {
				this.memberValue.traverse(visitor, scope);
			}
		}
		visitor.endVisit((/*@OwnPar*/ /*@NoRep*/ SingleMemberAnnotation)this, scope);
	}
}
