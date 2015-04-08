package dig;

import webil.ui.*;

public class Dig {
	final Panel mainPanel;
	final Grid island;
	final Text message;
	final Text scoreMessage;
	int score;
	final int[] map;
	final Button[] buttons;
	boolean gameOver;

	public static void main() {
		new Dig();
	}

	public Dig() {
		island = new Grid(6, 6);
		mainPanel = RootPanel.getRootPanel();
		message = new Text("");
		scoreMessage = new Text("");
		map = new int[36];
		buttons = new Button[36];

		setupUI();
		newGame();
	}

	private void setupUI() {
		int i, j, index;
		for (i = 0; i < 6; i++) {
			for (j = 0; j < 6; j++) {
				index = i * 6 + j;
				buttons[index] = new Button("" + index, "?");
				buttons[index].addListener(new DigListener(this));
				island.setWidget(i, j, buttons[index]);
			}
		}

		mainPanel.addChild(new Text("Welcome to Treasure Hunt!"));
		mainPanel.addChild(new Text(
				"Feel free to desperately dig for treasure."));
		mainPanel.addChild(island);
		mainPanel.addChild(message);
		mainPanel.addChild(scoreMessage);

		Button newGame = new Button("New game");
		newGame.addListener(new NewGameListener(this));
		mainPanel.addChild(newGame);
	}

	public void newGame() {
		int i, j, index;
		for (i = 0; i < 6; i++) {
			for (j = 0; j < 6; j++) {
				index = i * 6 + j;
				island.setWidget(i, j, buttons[index]);
				map[index] = new java.util.Random().nextInt(3);
				// addMessage(""+index);
				// addMessage(""+map[index]);
			}
		}

		gameOver = false;
		setScore(0);
		setMessage("You're still alive!  So far, so good.");
	}

	public void setScore(int score) {
		this.score = score;
		scoreMessage.setText("Score: " + score);
	}

	public void setMessage(String msg) {
		message.setText(msg);
	}

	private void gameOver() {
		setMessage("Game over!");
		gameOver = true;
	}

	public void dig(int index) {
		if (gameOver)
			return;

		int row = index / 6;
		int column = index % 6;

		String result;
		int revealed = map[index];
		if (revealed == 0) {
			result = "$";
			setScore(score + 1);
			setMessage("You found some treasure!  Score++!");
		} else if (revealed == 1) {
			result = "X";
			gameOver();
			setMessage("You find a box of one dozen starving, crazed weasels.  You die.  Game over.");
		} else {
			result = "@";
			setMessage("You got a pile of dirt.  But, you're still alive!  So far, so good.");
		}

		island.setWidget(row, column, new Text(result));
	}

	public void addMessage(String s) {
		RootPanel.getRootPanel().addChild(new Text(s));
	}
}

class DigListener implements ClickListener {
	final Dig app;

	public DigListener(Dig v) {
		this.app = v;
	}

	public void onClick(Widget w) {
		Button b = (Button) w;
		String s = b.getID();
		int index = Integer.parseInt(s);
		app.addMessage("Digging " + index + "...");
		app.dig(index);
	}
}

class NewGameListener implements ClickListener {
	final Dig app;

	public NewGameListener(Dig v) {
		this.app = v;
	}

	public void onClick(Widget w) {
		app.newGame();
	}
}
