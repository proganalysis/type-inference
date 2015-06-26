package secretKeeper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import checkers.inference2.jcrypt.quals.*;

public class SecretKeeper {

	public static SKUser[] userDB;
	public static int userNum;
	public static final int MAX_USERS = 100;
	/*@Poly*/ private String username, password;

	public static void main(String[] args) throws NullPointerException,
			IOException {
		/*@Sensitive*/ SecretKeeper mapp = new SecretKeeper();
		mapp.init();
	}

	private BufferedReader br;

	public SecretKeeper() {
		/* initialize userDB */
		if (userDB == null) {
			userDB = new SKUser[MAX_USERS];
			userNum = 0;
		}
	}

	public void init() throws IOException {
		System.out.print("Enter (i) to Sign in, or (u) to Sign up: ");
		br = new BufferedReader(new InputStreamReader(System.in));
		String type = br.readLine();
		if (type.equals("i")) {
			signin();
		} else if (type.equals("u")) {
			signup();
		} else {
			System.out.println("Invalid Input!");
			init();
		}
	}

	public void getInfo() throws IOException {
		System.out.println("Please enter your username and password.");
		System.out.print("Username-> ");
		String s = br.readLine();
		username = s;
		System.out.print("Password-> ");
		s = br.readLine();
		password = s;
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
		String s = br.readLine();
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
				String s = br.readLine();
				/*@Sensitive*/ String secret = s;
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
			String s = br.readLine();
			if (s.equals("y")) {
				System.out.print("Update secret-> ");
				String ss = br.readLine();
				/*@Sensitive*/ String secret = ss;
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
