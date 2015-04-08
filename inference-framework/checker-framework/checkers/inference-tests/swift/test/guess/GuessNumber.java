package guess;

import webil.ui.*;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class GuessNumber {
	private Text message;
	private Panel guesses;
	public NumberTextBox guessbox;
	private Button button;
	private Button newGame;
	Panel mainPanel;

	private int secret;
	int tries;

	public GuessNumber() {
		message = new Text("");
		guessbox = new NumberTextBox();
		button = new Button("Guess");
		newGame = new Button("New Game");
		Button b = button;
		Button ng = newGame;
		mainPanel = RootPanel.getRootPanel();
		GuessListener guessLi = new GuessListener(this);
		if (b != null)
			b.addListener(guessLi);
		NewGameListener ngLi = new NewGameListener(this);
		if (ng != null)
			ng.addListener(ngLi);
	}

	/**
	 * This is the entry point method.
	 */
	public static void main() {
		final GuessNumber gn = new GuessNumber();

		gn.newGame();
	}

	void newGame() {
		this.tries = 3;
		secret = new java.util.Random().nextInt(10) + 1;
		constructAddControls();
	}

	void makeGuess(Integer s) {
		int i = 0;

		Integer ts = s;
		if (ts != null) {
			i = ts.intValue();
		}

		if (i >= 1 && i <= 10) {
			if (i == secret && this.tries > 0) {

				this.tries = 0;
				finishApp("Bingo. You Win!");

			} else {

				this.tries = this.tries - 1;
				if (this.tries > 0) {
					Text m = message;
					Panel g = guesses;
					if (m != null && g != null) {
						m.setText("Try again");
						g.addChild(new Text(Integer.toString(i)));
					}
				} else {
					finishApp("Sorry! Tries Exhausted. Game Over");
				}
			}
		} else {
			Text m = message;
			if (m != null) {
				m.setText("Number out of Range");
			}
		}
	}

	void finishApp(String s) {
		final Panel mp = this.mainPanel;
		if (mp != null) {
			mp.removeAllChildren();
			mp.addChild(new Text(s));
			mp.addChild(newGame);
		}
	}

	private void constructAddControls() {
		HorizontalPanel input;
		VerticalPanel controls;

		guesses = new VerticalPanel();
		input = new HorizontalPanel();
		input.addChild(guessbox);
		input.addChild(button);
		input.addChild(new Text(" "));

		final Text m = message;

		if (m != null) {
			m.setText("");
		}

		input.addChild(message);

		controls = new VerticalPanel();
		controls.addChild(new Text("Enter a number between 1 and 10"));
		controls.addChild(new Text("You are allowed " + tries + " tries."));
		controls.addChild(input);
		controls.addChild(guesses);
		controls.addChild(newGame);

		final Panel mp = this.mainPanel;

		if (mp != null) {
			mp.removeAllChildren();
			mp.addChild(controls);
		}
	}
}

class GuessListener implements ClickListener {
	final GuessNumber guessApp;

	public GuessListener(GuessNumber g) {
		this.guessApp = g;
	}

	public void onClick(Widget w) {
		if (this.guessApp != null) {
			NumberTextBox tb = guessApp.guessbox;
			if (tb != null) {
				guessApp.makeGuess(tb.getInteger());
				tb.setText("");
				tb.setFocus(true);
			}
		}
	}
}

class NewGameListener implements ClickListener {
	final GuessNumber guessApp;

	public NewGameListener(GuessNumber g) {
		this.guessApp = g;
	}

	public void onClick(Widget w) {
		if (guessApp != null) {
			guessApp.newGame();
		}
	}
}
