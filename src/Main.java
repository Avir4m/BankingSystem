import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Scanner;

public class Main {


	public static Scanner input = new Scanner(System.in);

	public static void main(String[] args) throws Exception {

		createTable();

		while (true) {
			System.out.println("What would you like to do signup, login or quit?");
			String action = input.nextLine();
            switch (action) {
                case "q", "quit" -> System.exit(0);
                case "signup" -> signup();
                case "login" -> {
                    String username = login();
                    if (!username.isEmpty()) {
                        while (true) {
                            System.out.println("would you like to: withdraw, deposit, balance, delete account or quit?");
                            String loggedAction = input.nextLine();
                            switch (loggedAction) {
                                case "q", "quit" -> System.exit(0);
                                case "withdraw" -> withdraw(username);
                                case "deposit" -> deposit(username);
                                case "balance" -> getBalance(username);
                                case "delete account" -> deleteAccount(username);
                                default -> System.out.println("Invalid input, please try again.");
                            }
                        }
                    }
                }
                default -> System.out.println("Invalid input, please try again.");
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

	public static String login() throws Exception{
		System.out.println("Please enter your username");
		String name = input.nextLine();
		Connection conn = getConnection();
		Statement stmt = conn.createStatement();
		ResultSet rs=stmt.executeQuery("SELECT * FROM user WHERE name = '"+name+"'");
		while(rs.next()) {
			String userName = rs.getString("name");
			String userPassword = rs.getString("password");
			if (name.equals(userName)) {
				while (true) {
					System.out.println("Please enter your password");
					String password = input.nextLine();
					if (password.equals(userPassword)) {
						return userName;
					} else {
						System.out.println("Password is wrong, please try again.");
					}
				}
			} else {
				System.out.println("username does not exists.");
			}
			return "";
		}
		return "";
		

	}

	public static String signup() {

		System.out.println("Please enter a username ( username > 1 )");
		String username = input.nextLine();
		if (username.isEmpty()) {
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

	public static void deposit(String username) throws Exception {
		System.out.println("What is the amount you want to deposit?");
		double amount = input.nextDouble();
		input.nextLine();
		Connection conn = getConnection();
		Statement stmt = conn.createStatement();
		ResultSet rs=stmt.executeQuery("SELECT * FROM user WHERE name = '"+username+"'");
		while(rs.next()) {
			double balance = rs.getDouble("balance");
			double newBalance = balance + amount;
			PreparedStatement withdraw = conn.prepareStatement("UPDATE user SET balance = '"+newBalance+"' WHERE name = '"+username+"'");
			withdraw.executeUpdate();
		}
	}

	public static void withdraw(String username) throws Exception {
		System.out.println("What is the amount you want to withdraw?");
		double amount = input.nextDouble();
		input.nextLine();
		Connection conn = getConnection();
		Statement stmt = conn.createStatement();
		ResultSet rs=stmt.executeQuery("SELECT * FROM user WHERE name = '"+username+"'");
		while(rs.next()) {
			double balance = rs.getDouble("balance");
			double newBalance = balance - amount;
			if (newBalance < 0) {
				System.out.println("You can't withdraw more then your current balance'");
			} else {
				PreparedStatement withdraw = conn.prepareStatement("UPDATE user SET balance = '"+newBalance+"' WHERE name = '"+username+"'");
				withdraw.executeUpdate();
			}
		}
	}

	public static void getBalance(String name) throws Exception {
		Connection conn = getConnection();
		Statement stmt = conn.createStatement();
		ResultSet rs=stmt.executeQuery("SELECT * FROM user WHERE name = '"+name+"'");
		while(rs.next()) {
			double balance = rs.getDouble("balance");
			System.out.println(balance);
		}
	}

	private static void deleteAccount(String username) throws Exception {
		System.out.println("Are you sure you want to delete account? y/n");
		String accountDeleteConfirm = input.nextLine();
		if (accountDeleteConfirm.equals("y")) {
			Connection conn = getConnection();
			Statement stmt = conn.createStatement();
			ResultSet rs=stmt.executeQuery("SELECT * FROM user WHERE name = '"+username+"'");
			while(rs.next()) {
				String userPassword = rs.getString("password");
				while (true) {
					System.out.println("Please enter your password(q to quit):");
					String password = input.nextLine();
					if (password.equals(userPassword)) {
						PreparedStatement st = conn.prepareStatement("DELETE FROM user WHERE name = '"+username+"';");
						st.executeUpdate();
						System.out.println("Your account has been deleted.");
						System.exit(0);
					} else if (password.equals("q") || password.equals("quit")) {
						System.exit(0);
					}
					else {
						System.out.println("Wrong password, please try again.");
					}
				}
			}
		}
	}
}