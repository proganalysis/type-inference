package edu.rpi;

import soot.Local;
import soot.SootField;
import soot.Value;
import soot.jimple.*;

public class TranslatorVisitor extends AbstractStmtSwitch {

	private TranslatorTransformer t;

	public TranslatorVisitor(TranslatorTransformer t) {
		this.t = t;
	}

	@Override
	public void caseInvokeStmt(InvokeStmt stmt) {
		stmt.getInvokeExpr().apply(new ValueVisitor());
	}

	@Override
	public void caseAssignStmt(AssignStmt stmt) {
		Value leftOp = stmt.getLeftOp();
		Value rightOp = stmt.getRightOp();
		rightOp.apply(new ValueVisitor());
		leftOp.apply(new ValueVisitor());
	}

	class ValueVisitor extends AbstractJimpleValueSwitch {
		public ValueVisitor() {
		}
		// @Override
		// public void caseStaticFieldRef(StaticFieldRef v) {
		// }

		@Override
		public void caseInstanceFieldRef(InstanceFieldRef v) {
			Value base = v.getBase();
			assert base instanceof Local;
			SootField field = v.getField();
			if (field == null) {
				System.out.println(
						"WARN: " + base.getType() + " doesn't have field, at" + "\n\t" + t.getVisitorState().getUnit());
				return;
			}
			t.processField(field);
		}
		
	}

}
