package edu.rpi;

import soot.SootClass;
import soot.SootMethod;
import soot.Unit;

public class VisitorState {

    private SootClass sc; 

    private SootMethod sm; 

    private Unit unit; 

    public void setSootClass(SootClass sc) {
        this.sc = sc;
    }

    public SootClass getSootClass() {
        return this.sc;
    }

    public void setSootMethod(SootMethod sm) {
        this.sm = sm;
    }

    public SootMethod getSootMethod() {
        return this.sm;
    }

    public void setUnit(Unit u) {
        this.unit = u;
    }

    public Unit getUnit() {
        return this.unit;
    }
}
