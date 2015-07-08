package full;

import java.io.IOException;
import checkers.inference2.jcrypt.quals.*;
import checkers.inference2.jcrypt2.quals.*;

public class SecretKeeper {

	public static SKUser[] userDB;
	public static int userNum, count = 0;
	public static final int MAX_USERS = 100;
	/*@Poly*/ private String username, password;
	private static /*@BOT*/ String/*@BOT*/[] args;

	public static void main(/*@BOT*/ String/*@BOT*/[] args) throws NullPointerException,
			IOException {
		/*@Sensitive*/ SecretKeeper mapp = new SecretKeeper();
		SecretKeeper.args = new String[args.length];
		for (int i = 0; i < args.length; i++) {
			SecretKeeper.args[i] = args[i];
		}
		mapp.init();
	}

	public SecretKeeper() {
		/* initialize userDB */
		if (userDB == null) {
			userDB = new SKUser[MAX_USERS];
			userNum = 0;
		}
	}

	public void init() throws IOException {
		System.out.print("Enter (i) to Sign in, or (u) to Sign up, or (e) to Exit: ");
		String type = args[count];
		count++;
		if (type.equals("i")) {
			signin();
		} else if (type.equals("u")) {
			signup();
		} else if (type.equals("e")) {
			return;
		} else {
			System.out.println("Invalid Input!");
			init();
		}
	}

	public void getInfo() throws IOException {
		System.out.println("Please enter your username and password.");
		System.out.print("Username-> ");
		username = args[count];
		count++;
		System.out.print("Password-> ");
		password = args[count];
		count++;
	}

	public void signin() throws IOException {
		getInfo();
		final SKUser user = findUser(username, password);
		if (user != null) {
			showPage(user);
		} else {
			printIncorrect();
		}
	}

	public void printIncorrect() throws IOException {
		System.out.print("Sorry, incorrect username/password.\n"
				+ "Please try again (t) or sign up (u): ");
		String s = args[count];
		count++;
		if (s.equals("t"))
			signin();
		else if (s.equals("u"))
			signup();
		else {
			System.out.println("Invalid Input!");
			printIncorrect();
		}
	}

	/**
	 * Create a new user
	 * 
	 * @throws IOException
	 */
	public void signup() throws IOException {
		getInfo();
		if (exist(username)) {
			System.out.println("User name exists! Please sign in.");
			signin();
		} else {
			final SKUser user = new SKUser(username, password);
			if (userNum < MAX_USERS) {
				try {
					SecretKeeper.userDB[userNum] = user;
				} catch (ArrayIndexOutOfBoundsException imposs) {
				} catch (NullPointerException imposs) {
				}
				userNum = userNum + 1;
				System.out.println("Welcome, " + username + "!");
				System.out.print("Tell us a secret-> ");
				/*@Sensitive*/ String secret = args[count];
				count++;
				user.setSecret(secret);
				showPage(user);
			} else {
				System.out.println("Sorry, too many users");
				signin();
			}
		}
	}

	public void showPage(SKUser user) throws IOException {
		System.out.println("Hello, " + user.getName() + "! Here is your secret-> " + user.getSecret());
		while (true) {
			System.out.print("Enter (y) to update secret or (n) to sign out: ");
			String s = args[count];
			count++;
			if (s.equals("y")) {
				System.out.print("Update secret-> ");
				/*@Sensitive*/ String secret = args[count];
				count++;
				user.setSecret(secret);
				showPage(user);
				break;
			} else if (s.equals("n")) {
				System.out.println("You are signed out.");
				init();
				break;
			} else {
				System.out.println("Invalid Input!");
			}
		}
	}

	public boolean exist(String username) {
		for (int i = 0; i < SecretKeeper.userNum; i++) {
			if (SecretKeeper.userDB[i].getName().equals(username)) {
				return true;
			}
		}
		return false;
	}

	/* This performs the sign in operation of delegating to the Client principal */
	public SKUser findUser(String username, String pass) throws IOException {
		boolean found = false;
		int i = 0;
		SKUser user = null;
		while (i < SecretKeeper.userNum && !found) {
			try {
				user = SecretKeeper.userDB[i];
			} catch (ArrayIndexOutOfBoundsException imposs) {
			} catch (NullPointerException imposs) {
			}
			if (username != null && user != null
					&& username.equals(user.getName())
					&& pass.equals(user.getPassword())) {
				found = true;
			} else {
				user = null;
			}
			i = i + 1;
		}
		return user;
	}
}

/* Represents a user of the Secret Keeper application */
class SKUser {

	private String name, password;

	public SKUser(String name, String password) {
		this.name = name;
		this.password = password;
	}

	private String secret; // a secret for this user

	public void setSecret(String sec) {
		this.secret = sec;
	}

	public String getSecret() {
		return this.secret;
	}

	public String getName() {
		return name;
	}

	public String getPassword() {
		return password;
	}
}
