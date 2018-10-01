package libsvm;

// from here https://github.com/cjlin1/libsvm

public class svm_problem implements java.io.Serializable
{
	public int l;
	public double[] y;
	public svm_node[][] x;
}
