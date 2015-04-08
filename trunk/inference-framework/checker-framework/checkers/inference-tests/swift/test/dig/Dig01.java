package dig;

import webil.ui.*;

public class Dig01 {
	final Panel mainPanel;
	final Grid island;
	final Text message;
	final Text scoreMessage;
	int score;
	final int[] map;
	final Button[] buttons;

	public static void main() {
		new Dig01();
	}

	public Dig01() {
		island = new Grid(2, 2);
		mainPanel = RootPanel.getRootPanel();
		message = new Text("");
		scoreMessage = new Text("");
		map = new int[4];
		buttons = new Button[4];

		setupUI();
		newGame();
	}

	private void setupUI() {
		int i, j, index;
		for (i = 0; i < 2; i++) {
			for (j = 0; j < 2; j++) {
				index = i * 2 + j;
				buttons[index] = new Button("" + index, "?");
				buttons[index].addListener(new DigListener01(this));
				island.setWidget(i, j, buttons[index]);
			}
		}

		mainPanel.addChild(new Text("Welcome to Treasure Hunt!"));
		mainPanel.addChild(new Text(
				"Feel free to desperately dig for treasure."));
		mainPanel.addChild(island);
		mainPanel.addChild(message);
		mainPanel.addChild(scoreMessage);
	}

	public void newGame() {
    int i,j,index;
    for (i = 0; i < 2; i++) {
      for (j = 0; j < 2; j++) {
	index = i*2+j;
	island.setWidget(i, j, buttons[index]);
	map[index] = 1;
//	addMessage(""+index);
//	addMessage(""+map[index]);
      }
    }

    setScore(0);
    setMessage("You're still alive!  So far, so good.");
  }

	public void setScore(int score) {
		this.score = score;
	}

	public void setMessage(String msg) {
	}

	public void dig(int index) {
		int row = index / 2;
		int column = index % 2;

		String result;
		int revealed = map[index];
		if (revealed == 0) {
			result = "$";
			setScore(1);
		} else if (revealed == 1) {
			result = "X";
		} else {
			result = "@";
		}

		island.setWidget(row, column, new Text(result));
	}

	public void addMessage(String s) {
		RootPanel.getRootPanel().addChild(new Text(s));
	}
}

class DigListener01 implements ClickListener {
	final Dig01 app;

	public DigListener01(Dig01 v) {
		this.app = v;
	}

	public void onClick(Widget w) {
		Button b = (Button) w;
		String s = b.getID();
		int index = Integer.parseInt(s);
		app.dig(index);
	}
}
