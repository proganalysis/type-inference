import checkers.inference2.jcrypt.quals.*;

public class FieldSensitivity2 {
	protected void something() {
		/*-@Sensitive*/ Data dt = new Data();
		/*@Sensitive*/ String sim = new String("Something");

		dt.set(sim);
		Data dt2 = dt;
		String sim2 = dt2.get();
		
		if (sim.equals(sim2)) {
			
		}
	}
}
