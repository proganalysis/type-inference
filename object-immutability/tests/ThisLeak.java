class AAA {
	ThisLeak f;
}

public class ThisLeak {
   private ThisLeak f;
   
   public void m() {
	   this.f = this;
	   System.out.println("Tests leak into field of this");
   }
   
   public void n(AAA a) {
	   a.f = this;
	   System.out.println("Tests leak into field of parameter");
   }
   
   public static void main(String[] arg) {
	   ThisLeak tl = new ThisLeak();
	   AAA aaa = new AAA();
	   tl.m();
	   tl.n(aaa);
	   
   }
   
}
