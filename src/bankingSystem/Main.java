package bankingSystem;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.Scanner;

public class Main {

	public static Scanner input = new Scanner(System.in);

	public static void main(String[] args) throws Exception {

		createTable();

		while (true) {
			System.out.println("What would you like to do signup, login or quit?");
			String action = input.nextLine();
			if (action.equals("q") || action.equals("quit")) {
				System.exit(0);
			} else if (action.equals("signup")) {
				signup();
			} else if (action.equals("login")) {
				boolean loggedIn = login();
				if (loggedIn) {
					while (true) {
						System.out.println("would you like to withdraw, deposit or quit?");
						String loggedAction = input.nextLine();
						 if (loggedAction.equals("q") || loggedAction.equals("quit")) {
							System.exit(0);
						 } else if (loggedAction.equals("withdraw")) {
							withdraw();
						 } else if (loggedAction.equals("deposit")) {
							deposit();
						 } else {
							System.out.println("Invalid input, please try again.");
						 }
					}
				}
			} else {
				System.out.println("Invalid input, please try again.");
			}
		}
	}

	public static Connection getConnection() throws Exception {
        try {
            String driver = "com.mysql.cj.jdbc.Driver";
            String url = "jdbc:mysql://localhost:3306/bankingsystem";
            String username = "root";
            String password = "1234";
            Class.forName(driver);

            Connection conn = DriverManager.getConnection(url, username, password);
            System.out.println("Connected");
            return conn;

        } catch (Exception e) {
            System.out.println(e);
        }

        return null;
    }

	public static void createTable() throws Exception {
		try {
			Connection conn = getConnection();
			PreparedStatement create = conn.prepareStatement("CREATE TABLE IF NOT EXISTS user(id int NOT NULL AUTO_INCREMENT, name varchar(255) NOT NULL, password varchar(255) NOT NULL, balance INT, PRIMARY KEY(id))");
			create.executeUpdate();
		} catch (Exception e) {
			System.out.println(e);
		}
	}

	public static boolean login() throws Exception{

		System.out.println("Please enter your username");
		String username = input.nextLine();

		if (username == "username") {
			while (true) {
				System.out.println("Please enter your password");
				String password = input.nextLine();
				if (password == "password") {
					return true;
				} else {
					System.out.println("Password is wrong, please try again.");
				}
			}
		} else {
			System.out.println("username does not exists.");
		}
		return false;
	}

	public static String signup() {

		System.out.println("Please enter a username ( username > 1 )");
		String username = input.nextLine();
		if (username.length() < 1) {
			System.out.println("Your username is too short, needs to be at least 1 characters.");
			signup();
		}

		while (true) {
			System.out.println("Please enter a password ( password > 6 )");
			String password = input.nextLine();
			System.out.println("Please confirm your password");
			String password2 = input.nextLine();
			if (password.length() < 6) {
				System.out.println("Your password is too short, needs to be at least 6 characters.");
			} else if (!password.equals(password2)) {
				System.out.println("Your passwords don't match");
			} else {
				try {
					Connection conn = getConnection();
					PreparedStatement posted = conn.prepareStatement("INSERT INTO user (name, password) VALUES ('"+username+"', '"+password+"')");
					posted.executeUpdate();
				} catch (Exception e) {
					System.out.println(e);
				}
			}
			return username;
		}
	}

	public static void deposit() {
		System.out.println("What is the amount you want to deposit?");
		double amount = input.nextDouble();
		System.out.println(amount);
	}

	public static void withdraw() {
		System.out.println("What is the amount you want to withdraw?");
		double amount = input.nextDouble();
		System.out.println(amount);
	}

}
