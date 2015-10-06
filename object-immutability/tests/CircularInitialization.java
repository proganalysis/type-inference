class Person {
	String name;
	Person partner;
	
	public Person(String name) {
		this.name = name;
	}
	
	public void m(Person p) {
		this.partner = p;
	}
}

class Couple {
	Person husband;
	Person wife;
	public Couple(Person husband, Person wife) {
		this.husband = husband;
		this.wife = wife;
	}
	public void print() {
		System.out.println(husband.name+" and "+wife.name);
	}
}

public class CircularInitialization {
	public static void main(String[] args) {
		Person bob = new Person("Bob");
		Person sally = new Person("Sally");
		bob.m(sally);
		sally.m(bob);
		Couple c = new Couple(bob,sally);
		c.print();
	}
}
