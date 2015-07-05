package checkers.inference2;

public class Conversion {
	
	private long id;
	private String from;
	private String to;
	
	public Conversion(long id, String from, String to) {
		this.id = id;
		this.from = from;
		this.to = to;
	}
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getFrom() {
		return from;
	}
	public void setFrom(String from) {
		this.from = from;
	}
	public String getTo() {
		return to;
	}
	public void setTo(String to) {
		this.to = to;
	}
	
}
