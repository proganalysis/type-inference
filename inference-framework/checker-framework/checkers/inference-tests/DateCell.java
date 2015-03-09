import checkers.inference2.reimN.quals.*;

class DateCell {

    public static class Date {
	public int hours;
	public Date() {}
	public Date(int h) { hours = h; }
	public int getHours() { return hours; }
	public void setHours(/*MutMut*/ Date this, int h) { this.hours = h; }
    }

    Date date;
    /*ReadMut*/ Date getDate() { return date; }
    void cellSetHours(/*@MutMut*/ DateCell this) {
	Date md = this.getDate();
	md.setHours(1);
	Date rd = this.getDate();
	rd.getHours();
    }
    //int cellGetHours() {
    //	Date rd = getDate();
    //	int hour = rd.getHours();
    //	return hour;
    //}
}
