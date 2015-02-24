/**
 * 
 */
package checkers.inference2;

import static com.esotericsoftware.minlog.Log.info;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.lang.model.element.AnnotationMirror;

import checkers.inference2.Reference.FieldAdaptReference;
import checkers.inference2.Constraint.EqualityConstraint;
import checkers.inference2.Constraint.SubtypeConstraint;
import checkers.inference2.Constraint.UnequalityConstraint;
import checkers.inference2.Reference.AdaptReference;

/**
 * @author huangw5
 *
 */
public abstract class AbstractConstraintSolver<Checker extends InferenceChecker> implements ConstraintSolver {

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
        
    protected Checker checker;
    
    protected Constraint currentConstraint;
        
    private boolean needTrace = true;
        
    /** for storing traces */
    private Thread worker;
            
    private boolean stop = false;
        
    private PrintStream out;

    private Deque<Object> queue = new ArrayDeque<Object>();
        
    private final String DB_SCRIPT;
    
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
    
    
    public AbstractConstraintSolver(Checker t) {
        this.checker = t;
        needTrace = !(System.getProperty("noTrace") != null);
        DB_SCRIPT = InferenceMain.outputDir + File.separator + t.getName() + "-traces.sql";
        info(this.getClass().getSimpleName(), "needTrace = " + needTrace);
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
		if (hasUpdate) {
			System.out.println(c.toString("aaa", "bbb"));
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
        Reference sub = c.getLeft();
        Reference sup = c.getRight();

		boolean hasUpdate = false;		
		
		// Get the annotations
		Set<AnnotationMirror> subAnnos = getAnnotations(sub);
		Set<AnnotationMirror> supAnnos = getAnnotations(sup);

		// First update the left: If a left annotation is not 
		// subtype of any right annotation, then remove it. 
		for (Iterator<AnnotationMirror> it = subAnnos.iterator(); 
				it.hasNext();) {
			AnnotationMirror subAnno = it.next();
			boolean isFeasible = false;
			for (AnnotationMirror supAnno : supAnnos) {
                if (checker.getQualifierHierarchy().isSubtype(subAnno, supAnno)) {
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
		if (checker.isStrictSubtyping() && checker.getFailureStatus(c) != FailureStatus.WARN) {
			for (Iterator<AnnotationMirror> it = supAnnos.iterator(); 
					it.hasNext();) {
				AnnotationMirror supAnno = it.next();
				boolean isFeasible = false;
				for (AnnotationMirror subAnno : subAnnos) {
                    if (checker.getQualifierHierarchy().isSubtype(subAnno, supAnno)) {
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
		Reference left = c.getLeft();
		Reference right = c.getRight();
		
		// Get the annotations
		Set<AnnotationMirror> leftAnnos = getAnnotations(left);
		Set<AnnotationMirror> rightAnnos = getAnnotations(right);
		
		Set<AnnotationMirror> interAnnos = leftAnnos;
        interAnnos.retainAll(rightAnnos);
		
		if (interAnnos.isEmpty()) {
            throw new SolverException("ERROR: solve " + c + " failed becaue of an empty set.");
		}
		// update both
		return setAnnotations(left, interAnnos)
				|| setAnnotations(right, interAnnos);
    }

	protected boolean handleInequalityConstraint(UnequalityConstraint c) throws SolverException {
		Reference left = c.getLeft();
		Reference right = c.getRight();
		
		// Get the annotations
		Set<AnnotationMirror> leftAnnos = getAnnotations(left);
		Set<AnnotationMirror> rightAnnos = getAnnotations(right);
	
		// The default intersection of Set doesn't work well
		Set<AnnotationMirror> differAnnos = leftAnnos;
        differAnnos.removeAll(rightAnnos);
		
		if (differAnnos.isEmpty()) {
			throw new SolverException("ERROR: solve " + c 
					+ " failed becaue of an empty set.");
		}
		// Update the left
        return setAnnotations(left, differAnnos);
    }

	protected Set<AnnotationMirror> getAnnotations(Reference av) {
		return av.getAnnotations(checker);
	}

	/**
	 * Return true if there are updates
	 * @param av
	 * @param annos
	 * @return
	 * @throws SolverException
	 */
	protected boolean setAnnotations(Reference av, Set<AnnotationMirror> annos)
			throws SolverException {
        Set<AnnotationMirror> oldAnnos = av.getAnnotations(checker);
		if (av instanceof AdaptReference) {
			return setAnnotations((AdaptReference) av, annos);
		}
		if (oldAnnos.equals(annos)) {
			return false;
		}

        if (needTrace()) {
			insertObject(new Trace(av.getId(), oldAnnos.toString(),
					annos.toString(), getCurrentConstraint().getId()));
        }

        av.setAnnotations(annos, checker);

        return true;
    }

	protected boolean setAnnotations(AdaptReference aav, Set<AnnotationMirror> annos)
			throws SolverException {
        Reference context = aav.getContextRef();
        Reference decl = aav.getDeclRef();

		Set<AnnotationMirror> contextAnnos = context.getAnnotations(checker);
		Set<AnnotationMirror> declAnnos = decl.getAnnotations(checker);

		// First iterate through contextAnnos and remove infeasible annotations
		for (Iterator<AnnotationMirror> it = contextAnnos.iterator(); it.hasNext();) {
			AnnotationMirror contextAnno = it.next();
			boolean isFeasible = false;
			for (AnnotationMirror declAnno : declAnnos) {
				AnnotationMirror outAnno = null;
				if (aav instanceof FieldAdaptReference) {
					outAnno = checker.adaptField(contextAnno, declAnno);
				} else {
					outAnno = checker.adaptMethod(contextAnno, declAnno);
				}
				if (outAnno != null && annos.contains(outAnno)) {
					isFeasible = true;
					break;
				}
			}
			if (!isFeasible)
				it.remove();
		}
		
		if (contextAnnos.isEmpty()) {
			throw new SolverException("ERROR: Empty set for contextRef in AdaptConstraint");
		}
		
		// Now iterate through declAnnos and remove infeasible annotations
		for (Iterator<AnnotationMirror> it = declAnnos.iterator(); it.hasNext();) {
			AnnotationMirror declAnno = it.next();
			boolean isFeasible = false;
			for (AnnotationMirror contextAnno : contextAnnos) {
				AnnotationMirror outAnno = null;
				if (aav instanceof FieldAdaptReference) {
					outAnno = checker.adaptField(contextAnno, declAnno);
				} else {
					outAnno = checker.adaptMethod(contextAnno, declAnno);
				}
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

	@Deprecated
    protected void insertValue(AdaptReference av) {
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

    protected void insertValue(Reference av) {
        if (av instanceof AdaptReference) {
//            insertValue((AdaptValue) av);
        }
        try {
            StringBuilder sb = new StringBuilder();
            sb.append("insert into ").append(VALUE_TABLE_NAME)
                .append(" values (");
            sb.append(av.getId()).append(",\"");
            sb.append(av.getIdentifier().replace('\"', '_')).append("\",\"");
            sb.append(av.getAnnotations(checker).toString()).append("\",\"");
            sb.append(av.getType()).append("\",\"");
            sb.append(av.getKind()).append("\",\"");
            if (av.getElement() != null) {
                sb.append(av.getElement().toString().replace('\"', '_')).append("\",\"");
            } else if (av.getTree() != null) {
                sb.append(av.getTree().toString().replace('\"', '_')).append("\",\"");
            } else {
            	sb.append("null\", \"");
            }
            sb.append(av.getEnclosingType()).append("\",\"");
            sb.append(av.getName().replace('\"', '_')).append("\",\"");
            sb.append("").append("\");");
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
                            else if (o instanceof Reference)
                                insertValue((Reference) o);
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
            info(this.getClass().getSimpleName(), "Finished solving. Waiting for log worker thread...");
            stop = true;
            worker.join();
            out.close();    
        } catch (Exception e) {
        }
    }
    
    
    public Set<Constraint> solve() {
		info(checker.getName(), "Solving " + checker.getConstraints().size()
				+ " constraints in total");
        Set<Constraint> set;
        try {
            if (needTrace)
                initLog();
        
            set = solveImpl();
    
            if (needTrace()) {
                // dump annotated values
                for (Reference av: checker.getAnnotatedReferences().values()) {
                    insertObject(av);
                }
//                BitSet inserted = new BitSet(Reference.maxId());
                // dump constraints
                for (Constraint c : checker.getConstraints()) {
                    insertObject(c);
                    // also insert locals
//                    Reference[] avs = new Reference[]{c.getLeft(), c.getRight()};
//                    for (Reference av : avs) {
//                        if (av instanceof AdaptReference) {
//                            av = ((AdaptReference) av).getContextRef();
//                        }
//                        if (av.getKind() == Kind.LOCAL && !inserted.get(av.getId())) {
//                            insertObject(av);
//                            inserted.flip(av.getId());
//                        }
//                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            endLog();
        }
		info(checker.getName(), "Finished solving constraints. " + set.size() + " error(s).");
        return set;
     }

     protected abstract Set<Constraint> solveImpl(); 


}
