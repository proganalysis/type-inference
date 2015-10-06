package edu.rpi;

import java.util.*;
import java.lang.annotation.*;
import java.io.*;
import java.util.concurrent.*;

import soot.SourceLocator;
import soot.SootClass;
import soot.SootMethod;

import edu.rpi.ConstraintSolver.FailureStatus;
import edu.rpi.ConstraintSolver.SolverException;
import edu.rpi.Constraint.SubtypeConstraint;
import edu.rpi.Constraint.EqualityConstraint;
import edu.rpi.Constraint.UnequalityConstraint;
import edu.rpi.AnnotatedValue.*;
import edu.rpi.*;


public abstract class AbstractConstraintSolver implements ConstraintSolver {

    public static class Trace {
        public int avId; 
        public String oldAnnos; 
        public String newAnnos; 
        public int causeId;
        public Trace(int avId, String oldAnnos, String newAnnos, int causeId) {
            this.avId = avId;
            this.oldAnnos = oldAnnos;
            this.newAnnos = newAnnos;
            this.causeId = causeId;
        }
    }

    protected InferenceTransformer t;

    protected Constraint currentConstraint;

    private boolean needTrace = true;;

    private ThreadFactory tFactory;
    /** for storing traces */
    private Thread worker; 

    private boolean stop = false;

    private PrintStream out;

    private Deque<Object> queue = new LinkedList<Object>();

    private final String DB_NAME, DB_SCRIPT;

    private final String VALUE_TABLE_NAME = "avalues";

    private final String AVALUE_TABLE_NAME = "adaptvalues";

    private final String CONSTRAINT_TABLE_NAME = "constraints";

    private final String TRACE_TABLE_NAME = "traces";

    private final String CREATE_VALUE_TABLE = "create table " + VALUE_TABLE_NAME + "("
        + "id integer, "
        + "identifier string, " 
        + "annos string, " 
        + "type string, "
        + "kind string, "
        + "value string, "
        + "class string, " 
        + "name string, "
        + "method string" + ");\n"
        + "create index " + VALUE_TABLE_NAME + "_idx "
        + "on " + VALUE_TABLE_NAME + "(id);";

    /**
     * kind = 0: field adapt
     * kind = 1: method adapt
     */
    private final String CREATE_AVALUE_TABLE = "create table " + AVALUE_TABLE_NAME + "("
        + "id integer, "
        + "context string, "
        + "decl string, "
        + "context_id integer, "
        + "decl_id integer, "
        + "kind string" + ");";


    /**
     * kind = 0: subkind 
     * kind = 1: equality
     * kind = 2: inequality
     */
    private final String CREATE_CONSTRAINT_TABLE = "create table " + CONSTRAINT_TABLE_NAME + "("
        + "id integer, "
        + "str string, "
        + "left_id integer, " 
        + "right_id integer, " 
        + "cause_1 integer, " 
        + "cause_2 integer, " 
        + "cause_3 integer, " 
        + "kind integer" + ");\n"
        + "create index " + CONSTRAINT_TABLE_NAME + "_idx " 
        + "on " + CONSTRAINT_TABLE_NAME + "(id);";


    /**
     * direction = 0: forward
     * direction = 1: backword
     */
    private final String CREATE_TRACE_TABLE = "create table " + TRACE_TABLE_NAME + "("
        + "value_id integer,"
        + "old string,"
        + "new string,"
        + "constraint_id integer" + ");\n"
        + "create index " + TRACE_TABLE_NAME + "_idx " 
        + "on " + TRACE_TABLE_NAME + "(value_id);";

    public AbstractConstraintSolver(InferenceTransformer t) {
//        needTrace = !(System.getProperty("noTrace") != null);
    	this(t, false);
    }

    public AbstractConstraintSolver(InferenceTransformer t, boolean b) {
    	this.needTrace = b;
        this.t = t;
        DB_NAME = SourceLocator.v().getOutputDir() + File.separator + t.getName() + "-traces.sqlite";
        DB_SCRIPT = SourceLocator.v().getOutputDir() + File.separator + t.getName() + "-traces.sql";
        tFactory =  Executors.defaultThreadFactory();
        System.out.println("INFO: needTrace = " + needTrace);
    }

    public Constraint getCurrentConstraint() {
        return currentConstraint;
    }

    protected boolean needTrace() {
        return needTrace; 
    }

	protected boolean handleConstraint(Constraint c) throws SolverException {
		currentConstraint = c;
		boolean hasUpdate = false;
        try {
            if (c instanceof SubtypeConstraint) {
                hasUpdate = handleSubtypeConstraint((SubtypeConstraint) c);
            } else if (c instanceof EqualityConstraint) {
                hasUpdate = handleEqualityConstraint((EqualityConstraint) c);
            } else if (c instanceof UnequalityConstraint) {
                hasUpdate = handleInequalityConstraint((UnequalityConstraint) c);
            } 
        } finally {
            currentConstraint = null;
        }
        return hasUpdate;
	}

	/**
	 * Return true if there are updates
	 * @param c
	 * @return
	 * @throws SolverException
	 */
	protected boolean handleSubtypeConstraint(SubtypeConstraint c) throws SolverException {
        AnnotatedValue sub = c.getLeft();
        AnnotatedValue sup = c.getRight();

		boolean hasUpdate = false;		
		
		// Get the annotations
		Set<Annotation> subAnnos = getAnnotations(sub);
		Set<Annotation> supAnnos = getAnnotations(sup);

		// First update the left: If a left annotation is not 
		// subtype of any right annotation, then remove it. 
		for (Iterator<Annotation> it = subAnnos.iterator(); 
				it.hasNext();) {
			Annotation subAnno = it.next();
			boolean isFeasible = false;
			for (Annotation supAnno : supAnnos) {
                if (AnnotationUtils.isSubtype(subAnno, supAnno)) {
					isFeasible = true;
					break;
				}
			}
			if (!isFeasible)
				it.remove();
		}
		
		// Now update the right: If a right annotation is not super type 
		// of any left annotation, remove it
		// We only do this if it is strict subtyping
		if (t.isStrictSubtyping()) {
			for (Iterator<Annotation> it = supAnnos.iterator(); 
					it.hasNext();) {
				Annotation supAnno = it.next();
				boolean isFeasible = false;
				for (Annotation subAnno : subAnnos) {
                    if (AnnotationUtils.isSubtype(subAnno, supAnno)) {
						isFeasible = true;
						break;
					}
				}
				if (!isFeasible)
					it.remove();
			}
		}

		if (subAnnos.isEmpty() || supAnnos.isEmpty())
            throw new SolverException("ERROR: solve " + c + " failed becaue of an empty set.");
		
        hasUpdate = setAnnotations(sub, subAnnos) || setAnnotations(sup, supAnnos) || hasUpdate;
		
		return hasUpdate;
    }

	protected boolean handleEqualityConstraint(EqualityConstraint c) throws SolverException {
		AnnotatedValue left = c.getLeft();
		AnnotatedValue right = c.getRight();
		
		// Get the annotations
		Set<Annotation> leftAnnos = getAnnotations(left);
		Set<Annotation> rightAnnos = getAnnotations(right);
		
		Set<Annotation> interAnnos = leftAnnos;
        interAnnos.retainAll(rightAnnos);
		
		if (interAnnos.isEmpty()) {
            throw new SolverException("ERROR: solve " + c + " failed becaue of an empty set.");
		}
		// update both
		return setAnnotations(left, interAnnos)
				|| setAnnotations(right, interAnnos);
    }

	protected boolean handleInequalityConstraint(UnequalityConstraint c) throws SolverException {
		AnnotatedValue left = c.getLeft();
		AnnotatedValue right = c.getRight();
		
		// Get the annotations
		Set<Annotation> leftAnnos = getAnnotations(left);
		Set<Annotation> rightAnnos = getAnnotations(right);
	
		// The default intersection of Set doesn't work well
		Set<Annotation> differAnnos = leftAnnos;
        differAnnos.removeAll(rightAnnos);
		
		if (differAnnos.isEmpty()) {
			throw new SolverException("ERROR: solve " + c 
					+ " failed becaue of an empty set.");
		}
		// Update the left
        return setAnnotations(left, differAnnos);
    }

	protected Set<Annotation> getAnnotations(AnnotatedValue av) {
		if (av instanceof AdaptValue) {
			AdaptValue aav = (AdaptValue) av;
			AnnotatedValue context = aav.getContextValue();
			AnnotatedValue decl = aav.getDeclValue();
			
			if (av instanceof FieldAdaptValue)
				return t.adaptFieldSet(context.getAnnotations(t), 
						decl.getAnnotations(t));
			else
				return t.adaptMethodSet(context.getAnnotations(t), 
						decl.getAnnotations(t));
		} else
			return av.getAnnotations(t);
	}

	/**
	 * Return true if there are updates
	 * @param av
	 * @param annos
	 * @return
	 * @throws SolverException
	 */
	protected boolean setAnnotations(AnnotatedValue av, Set<Annotation> annos)
			throws SolverException {
        Set<Annotation> oldAnnos = av.getAnnotations(t);
		if (av instanceof AdaptValue)
			return setAnnotations((AdaptValue) av, annos);
		if (oldAnnos.equals(annos))
			return false;

        if (needTrace())
            insertObject(new Trace(av.getId(), oldAnnos.toString(), annos.toString(), 
                    getCurrentConstraint().getId()));

        av.setAnnotations(annos, t);

        return true;
    }

	protected boolean setAnnotations(AdaptValue aav, Set<Annotation> annos)
			throws SolverException {
        AnnotatedValue context = aav.getContextValue();
        AnnotatedValue decl = aav.getDeclValue();

		Set<Annotation> contextAnnos = context.getAnnotations(t);
		Set<Annotation> declAnnos = decl.getAnnotations(t);

		// First iterate through contextAnnos and remove infeasible annotations
		for (Iterator<Annotation> it = contextAnnos.iterator(); it.hasNext();) {
			Annotation contextAnno = it.next();
			boolean isFeasible = false;
			for (Annotation declAnno : declAnnos) {
				Annotation outAnno = null;
				if (aav instanceof FieldAdaptValue)
					outAnno = t.adaptField(contextAnno, declAnno);
				else
					outAnno = t.adaptMethod(contextAnno, declAnno);
				if (outAnno != null && annos.contains(outAnno)) {
					isFeasible = true;
					break;
				}
			}
			if (!isFeasible)
				it.remove();
		}
		
		if (contextAnnos.isEmpty())
			throw new SolverException("ERROR: Empty set for contextRef in AdaptConstraint");
		
		// Now iterate through declAnnos and remove infeasible annotations
		for (Iterator<Annotation> it = declAnnos.iterator(); it.hasNext();) {
			Annotation declAnno = it.next();
			boolean isFeasible = false;
			for (Annotation contextAnno : contextAnnos) {
				Annotation outAnno = null;
				if (aav instanceof FieldAdaptValue)
					outAnno = t.adaptField(contextAnno, declAnno);
				else
					outAnno = t.adaptMethod(contextAnno, declAnno);
				if (outAnno != null && annos.contains(outAnno)) {
					isFeasible = true;
					break;
				}
			}
			if (!isFeasible)
				it.remove();
		}
		
		if (declAnnos.isEmpty())
			throw new SolverException("ERROR: Empty set for declRef in AdaptConstraint");
		
		return setAnnotations(context, contextAnnos)
				|| setAnnotations(decl, declAnnos);
	}

    protected void insertValue(AdaptValue av) {
        try {
//            pInsertAValue.setInt(1, av.getId());
//            pInsertAValue.setString(2, av.getContextValue().toString());
//            pInsertAValue.setString(3, av.getDeclValue().toString());
//            pInsertAValue.setInt(4, av.getContextValue().getId());
//            pInsertAValue.setInt(5, av.getDeclValue().getId());
//            pInsertAValue.setString(6, av.getKind().toString());
//            adaptvalueNum--;
//            if (adaptvalueNum == 0) {
//                pInsertAValue.executeBatch();
//                adaptvalueNum = 100;
//            } else 
//                pInsertAValue.addBatch();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void insertValue(AnnotatedValue av) {
        if (av instanceof AdaptValue) {
//            insertValue((AdaptValue) av);
        }
        try {
            StringBuilder sb = new StringBuilder();
            sb.append("insert into ").append(VALUE_TABLE_NAME)
                .append(" values (");
            sb.append(av.getId()).append(",\"");
            sb.append(av.getIdentifier().replace('\"', '_')).append("\",\"");
            sb.append(av.getAnnotations(t).toString()).append("\",\"");
            sb.append(av.getType().toString()).append("\",\"");
            sb.append(av.getKind().toString()).append("\",\"");
            if (av.getValue() != null)
                sb.append(av.getValue().toString().replace('\"', '_')).append("\",\"");
            else 
                sb.append("null\", \"");
            SootClass sc = av.getEnclosingClass();
//            if (sc == null)
//                System.err.println("Enclosing class is null: " + av);
            sb.append(sc != null ? sc.getName() : "").append("\",\"");
            sb.append(av.getName().replace('\"', '_')).append("\",\"");
            SootMethod sm = av.getEnclosingMethod();
            sb.append(sm != null ? sm.getSubSignature() : "").append("\");");
            out.println(sb.toString());

			
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void insertConstraint(Constraint c) {
        try {
            StringBuilder sb = new StringBuilder();
            sb.append("insert into ").append(CONSTRAINT_TABLE_NAME)
                .append(" values (");
            sb.append(c.getId()).append(",\"");
            sb.append(c.toString().replace('\"', '_')).append("\",");
            sb.append(c.getLeft().getId()).append(",");
            sb.append(c.getRight().getId()).append(",");
            List<Constraint> causes = c.getCauses();
            for (int i = 0; i < 3; i++) {
                if (i < causes.size())
                    sb.append(causes.get(i).getId());
                else 
                    sb.append("-1");
                sb.append(",");
            }
            sb.append(c.getKind()).append(");");
            out.println(sb.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void insertTrace(Trace t) {
        try {

            StringBuilder sbd = new StringBuilder();
            sbd.append("delete from ").append(TRACE_TABLE_NAME)
                .append(" where value_id = ").append(t.avId)
                .append(" and old = \"").append(t.oldAnnos).append("\";");
            out.println(sbd.toString());

            StringBuilder sb = new StringBuilder();
            sb.append("insert into ").append(TRACE_TABLE_NAME)
                .append(" values (");
            sb.append(t.avId).append(",\"");
            sb.append(t.oldAnnos).append("\",\"");
            sb.append(t.newAnnos).append("\",");
            sb.append(t.causeId).append(");");
            out.println(sb.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void insertObject(Object o) {
        synchronized(queue) {
            queue.addLast(o);
            queue.notify();
        }
    }

    private void initLog() throws FileNotFoundException {
        out = new PrintStream(DB_SCRIPT);

        out.println(CREATE_VALUE_TABLE);
        out.println(CREATE_CONSTRAINT_TABLE);
        out.println(CREATE_TRACE_TABLE);

        worker = new Thread(new Runnable() {
            public void run() {
                while (!stop || !queue.isEmpty()) {
                    synchronized(queue) {
                        while (queue.isEmpty() && !stop) {
                            try {
                                queue.wait(10);
                            } catch (Exception e) {}
                        }
                        int size = queue.size();
                        while (size > 0) {
                            Object o = queue.removeFirst();
                            if (o instanceof Trace) 
                                insertTrace((Trace) o);
                            else if (o instanceof AnnotatedValue)
                                insertValue((AnnotatedValue) o);
                            else if (o instanceof Constraint)
                                insertConstraint((Constraint) o);
                            size--;
                        }
                    }
                }
            }
        });;
        worker.start();
    }

    private void endLog() {
        try {
            System.out.println("INFO: Finished solving. Waiting for log worker thread...");
            stop = true;
            worker.join();
            out.close();    
        } catch (Exception e) {
        }
    }

    public Set<Constraint> solve() {
        Set<Constraint> set;
        try {
            if (needTrace)
                initLog();

            set = solveImpl();

            if (needTrace()) {
                // dump annotated values
                for (AnnotatedValue av: t.getAnnotatedValues().values()) {
                    insertObject(av);
                }
                BitSet inserted = new BitSet(AnnotatedValue.maxId());
                // dump constraints
                for (Constraint c : t.getConstraints()) {
                    insertObject(c);
                    // also insert locals
                    AnnotatedValue[] avs = new AnnotatedValue[]{c.getLeft(), c.getRight()};
                    for (AnnotatedValue av : avs) {
                        if (av instanceof AdaptValue) {
                            av = ((AdaptValue) av).getContextValue();
                        }
                        if (av.getKind() == Kind.LOCAL && !inserted.get(av.getId())) {
                            insertObject(av);
                            inserted.flip(av.getId());
                        }
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            endLog();
        }
        return set;
    }

    protected abstract Set<Constraint> solveImpl();
}
