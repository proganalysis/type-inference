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
import org.eclipse.jdt.internal.compiler.lookup.ClassScope;
import org.eclipse.jdt.internal.compiler.lookup.Scope;



public class JavadocArrayQualifiedTypeReference extends ArrayQualifiedTypeReference {

	public int tagSourceStart, tagSourceEnd;

	public JavadocArrayQualifiedTypeReference(JavadocQualifiedTypeReference typeRef, int dim) {
		super(typeRef.tokens, dim, typeRef.sourcePositions);
	}

	protected void reportInvalidType(Scope scope) {
		scope.problemReporter().javadocInvalidType((/*@OwnPar*/ /*@NoRep*/ JavadocArrayQualifiedTypeReference)this, this.resolvedType, scope.getDeclarationModifiers());
	}
	protected void reportDeprecatedType(Scope scope) {
		scope.problemReporter().javadocDeprecatedType(this.resolvedType, (/*@OwnPar*/ /*@NoRep*/ JavadocArrayQualifiedTypeReference)this, scope.getDeclarationModifiers());
	}

	/* (non-Javadoc)
	 * Redefine to capture javadoc specific signatures
	 * @see org.eclipse.jdt.internal.compiler.ast.ASTNode#traverse(org.eclipse.jdt.internal.compiler.ASTVisitor, org.eclipse.jdt.internal.compiler.lookup.BlockScope)
	 */
	public void traverse(ASTVisitor visitor, BlockScope scope) {
		visitor.visit((/*@OwnPar*/ /*@NoRep*/ JavadocArrayQualifiedTypeReference)this, scope);
		visitor.endVisit((/*@OwnPar*/ /*@NoRep*/ JavadocArrayQualifiedTypeReference)this, scope);
	}
	
	public void traverse(ASTVisitor visitor, ClassScope scope) {
		visitor.visit((/*@OwnPar*/ /*@NoRep*/ JavadocArrayQualifiedTypeReference)this, scope);
		visitor.endVisit((/*@OwnPar*/ /*@NoRep*/ JavadocArrayQualifiedTypeReference)this, scope);
	}
}
