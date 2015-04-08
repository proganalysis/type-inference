package auction;

import webil.ui.*;
import checkers.inference2.jcrypt.quals.*;

public class Auction {

	/*Poly*/ private static AuctionInfo[] auctions = new AuctionInfo[1000];
	private static int numAuctions;

	public static void main(String[] args) {
		new Auction().run();
	}

	/*Poly*/ private TextBox userbox;
	private Panel userControls;
	private Panel contentPane;
	private ControlListener submit;
	private Timer timer;
	
	/*Poly*/ private String user;
	
	private void run() {
		showLogin();
	}
	
	public Panel getContentPane() {
		return contentPane;
	}
	
	public String getUser() {
		return user;
	}
	
	public int getNumAuctions() {
		return numAuctions;
	}
	
	public AuctionInfo getAuction(int i) {
		return auctions[i];
	}
	
	private void showLogin() {
		HorizontalPanel h = new HorizontalPanel();
		userbox = new TextBox("");
		h.addChild(userbox);
		h.addChild(new Text(" "));
		Button b = new Button("Login");
		submit = new ControlListener(this);
		b.addListener(submit);
		h.addChild(b);
		RootPanel.getRootPanel().addChild(h);
	}
	
	public void login() {
	    /*sensitive*/ String u = userbox.getText();
		
		if (u.length() != 0) {
			user = u;
			showControls();
		}
	}
	
	private void showControls() {
		userControls = new HorizontalPanel();
		Button b = new Button("New Auction");
		b.addListener(submit);
		userControls.addChild(b);
		userControls.addChild(new Text(" "));
		b = new Button("End Auction");
		b.addListener(submit);
		userControls.addChild(b);
		userControls.addChild(new Text(" "));
		b = new Button("View Auctions");
		b.addListener(submit);
		userControls.addChild(b);
		contentPane = new VerticalPanel();
		VerticalPanel v = new VerticalPanel();
		v.addChild(userControls);
		v.addChild(new Text(" "));
		v.addChild(contentPane);
		RootPanel root = RootPanel.getRootPanel();
		root.removeAllChildren();
		root.addChild(v);
		timer = new Timer();
		timer.addListener(new ViewTimer(this));
	}
	
	public void newAuction() {
	    timer.cancel();
		new CreateAuction(this);
	}
	
	public void endAuction() {
	    timer.cancel();
		new EndAuction(this);
	}
	
	public void viewAuctions() {
	    timer.cancel();
		new ViewAuctions(this);
		timer.schedule(5000);
	}
	
	public void addAuction(/*Poly*/ String name, /*Poly*/ int start) {
		/*-@Sensitive*/ AuctionInfo ai = new AuctionInfo();
		ai.owner = user;
		ai.name = name;
		ai.start = start;
		ai.current = -1;
		auctions[numAuctions] = ai;
		numAuctions = numAuctions + 1;
	}
	
	public void endAuction(int id) {
		if (id < 0 || id >= numAuctions) {
			return;
		}
		
		auctions[id].ended = true;
	}
	
	public void makeBid(int id, /*Poly*/ int bid) {
		enterBid(id, bid);
		viewAuctions();
	}
	
	private void enterBid(int id, /*Poly*/ int bid) {
		if (id < 0 || id >= numAuctions) {
			return;
		}
		
		/*Poly*/ AuctionInfo ai;
		ai = auctions[id];
		
		if (ai.ended) {
			return;
		}
		
		if (bid >= ai.start && bid > ai.current) {
			ai.current = bid;
			ai.highbidder = user;
		}
	}
	
}

class AuctionInfo {

	/*Poly*/ public String owner;
	/*Poly*/ public String name;
	/*Poly*/ public int start;
	public boolean ended;
	/*Poly*/ public int current;
	/*Poly*/ public String highbidder;
	
}

class CreateAuction {

	/*Poly*/ private Auction app;
	private Panel pane;
	
	/*Poly*/ private TextBox itembox;
	/*Poly*/ private NumberTextBox startbox;
	private Text message;
	
	public CreateAuction(/*Poly*/ Auction app) {
		this.app = app;
		this.pane = app.getContentPane();
		
		VerticalPanel v = new VerticalPanel();
		pane.removeAllChildren();
		Grid g = new Grid(2, 3);
		g.setWidget(0, 0, new Text("Item name:"));
		itembox = new TextBox("");
		g.setWidget(0, 1, itembox);
		g.setWidget(1, 0, new Text("Starting bid:"));
		startbox = new NumberTextBox(0);
		g.setWidget(1, 1, startbox);
		
		v.addChild(g);
		v.addChild(new Text(" "));
		Button b = new Button("Start Auction");
		b.addListener(new CreateListener(this));
		v.addChild(b);
		v.addChild(new Text(" "));
		message = new Text("");
		v.addChild(message);
		pane.addChild(v);
	}
	
	private void setMessage(String s) {
		message.setText(s);
	}
	
	public void startAuction() {
		/*Sensitive*/ String name = itembox.getText();
		/*Sensitive*/ Integer s = startbox.getInteger();
		
		if (name == null || s == null || name.length() == 0) {
			setMessage("Invalid input");
			return;
		}
		
		/*Sensitive*/ int start = s.intValue();
		
		if (start < 0) {
			setMessage("Invalid input");
			return;
		}
		
		app.addAuction(name, start);
		pane.removeAllChildren();
	}
	
}

class EndAuction {

	/*Poly*/ private Auction app;
	private Grid g;
	
	public EndAuction(/*Poly*/ Auction app) {
		this.app = app;
		
		/*Poly*/ String user = app.getUser();
		
		int n = app.getNumAuctions();
		int numAuctions = 0;
		
		for (int i = 0; i < n; i = i + 1) {
			if (app.getAuction(i).owner.equals(user)) {
				numAuctions = numAuctions + 1;
			}
		}
		
		g = new Grid(numAuctions + 1, 4);
		
		g.setWidget(0, 0, new Text("Item name"));
		g.setWidget(0, 1, new Text("Current bid"));
		g.setWidget(0, 2, new Text("High bidder"));
		
		int curRow = 1;
		
		for (int i = 0; i < n; i = i + 1) {
			AuctionInfo ai = app.getAuction(i);
			
			if (ai.owner.equals(user)) {
				g.setWidget(curRow, 0, new Text(ai.name));
				
				if (ai.current > -1) {
					g.setWidget(curRow, 1, new Text(Integer.toString(ai.current)));
				}
				
				String s;
				s = ai.highbidder;
				
				if (s != null) {
					g.setWidget(curRow, 2, new Text(s));
				}
				
				Button b = new Button("End");
				b.addListener(new EndListener(this, i, curRow));
				g.setWidget(curRow, 3, b);
				
				curRow = curRow + 1;
			}
		}
		
		Panel contentPane = app.getContentPane();
		contentPane.removeAllChildren();
		contentPane.addChild(g);
	}
	
	public void endAuction(int id, int row) {
		app.endAuction(id);
		g.setWidget(row, 3, new Text("Ended"));
	}
	
}

class ViewAuctions {

	/*Poly*/ private Auction app;
	public ViewAuctions(/*Poly*/ Auction app) {
		this.app = app;
		app.getUser();
		
		int n;
		n = app.getNumAuctions();
		Grid g = new Grid(n + 1, 8);
		
		g.setWidget(0, 0, new Text("Item name"));
		g.setWidget(0, 1, new Text("Seller"));
		g.setWidget(0, 2, new Text("Starting bid"));
		g.setWidget(0, 3, new Text("Current bid"));
		g.setWidget(0, 4, new Text("High bidder"));
		g.setWidget(0, 5, new Text("Bid"));
		
		for (int i = 0; i < n; i = i + 1) {
			AuctionInfo ai;
			ai = app.getAuction(i);
			g.setWidget(i + 1, 0, new Text(ai.name));
			g.setWidget(i + 1, 1, new Text(ai.owner));
			g.setWidget(i + 1, 2, new Text(Integer.toString(ai.start)));
			
			if (ai.current > -1) {
				g.setWidget(i + 1, 3, new Text(Integer.toString(ai.current)));
			}
			
			if (ai.highbidder != null) {
				String s;
				s = ai.highbidder;
				g.setWidget(i + 1, 4, new Text(s));
			}
			
			if (ai.ended) {
				g.setWidget(i + 1, 5, new Text("Ended"));
			} else {
				int j = ai.start;
				
				if (ai.current >= j) {
					j = ai.current + 1;
				}
				
				/*Poly*/ NumberTextBox t = new NumberTextBox(j);
				g.setWidget(i + 1, 5, t);
				Button b = new Button("Bid");
				b.addListener(new BidListener(this, i, t));
				g.setWidget(i + 1, 6, b);
			}
		}
		
		Panel contentPane = app.getContentPane();
		contentPane.removeAllChildren();
		contentPane.addChild(g);
	}
	
	public void makeBid(int id, /*Poly*/ int bid) {
		app.makeBid(id, bid);
	}
	
}

class ControlListener implements ClickListener {

	/*Poly*/ private Auction app;
	
	public ControlListener(Auction app) {
		this.app = app;
	}
	
	public void onClick(/*Sensitive*/ Widget w) {
		/*Sensitive*/ String s = ((Button) w).getText();
		
		if (s.equals("Login")) {
			app.login();
		} else if (s.equals("New Auction")) {
		    System.out.println(("New clicked."));
			app.newAuction();
		} else if (s.equals("End Auction")) {
		    System.out.println("End clicked.");
			app.endAuction();
		} else if (s.equals("View Auctions")) {
			app.viewAuctions();
		}
	}
	
}

class CreateListener implements ClickListener {

	/*Poly*/ private CreateAuction ca;
	
	public CreateListener(/*Poly*/ CreateAuction ca) {
		this.ca = ca;
	}
	
	public void onClick(Widget w) {
		ca.startAuction();
	}

}

class EndListener implements ClickListener {

	/*Poly*/ private EndAuction ea;
	private int id;
	private int row;
	
	public EndListener(/*Poly*/ EndAuction ea, int id, int row) {
		this.ea = ea;
		this.id = id;
		this.row = row;
	}
	
	public void onClick(Widget w) {
		ea.endAuction(id, row);
	}

}

class BidListener implements ClickListener {

	/*Poly*/ private ViewAuctions va;
	private int id;
	/*Poly*/ private NumberTextBox bidbox;
	
	public BidListener(/*Poly*/ ViewAuctions va, int id, /*Poly*/ NumberTextBox bidbox) {
		this.va = va;
		this.id = id;
		this.bidbox = bidbox;
	}
	
	public void onClick(Widget w) {
		/*Sensitive*/ Integer i = bidbox.getInteger();
		
		if (i != null) {
			va.makeBid(id, i.intValue());
		}
	}

}

class ViewTimer implements TimerListener {
    
	/*Poly*/ private Auction app;
    
    public ViewTimer(/*Poly*/ Auction app) {
        this.app = app;
    }
    
    public void onTimer(Timer t) {
        app.viewAuctions();
    }
    
}
