package bookstore;

import java.io.Console;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

import com.mysql.jdbc.Statement;

public class LoginModule{
	public static Scanner sc = new Scanner(System.in);
	public static Connection conn=null;
	
		public static byte[] getSHA(String input) throws NoSuchAlgorithmException  
	    {  
	        /* MessageDigest instance for hashing using SHA256 */  
	        MessageDigest md = MessageDigest.getInstance("SHA-256");  
	  
	        /* digest() method called to calculate message digest of an input and return array of byte */  
	        return md.digest(input.getBytes(StandardCharsets.UTF_8));  
	    }  
		
		public static String passwordEncoder(String pass) {
			try {
				byte[] hash = getSHA(pass);
			
				BigInteger number = new BigInteger(1, hash);  
				  
		        /* Convert the digest into hex value */  
		        StringBuilder hexString = new StringBuilder(number.toString(16));  
		  
		        /* Pad with leading zeros */  
		        while (hexString.length() < 32)  
		        {  
		            hexString.insert(0, '0');  
		        }  
		  
		        return hexString.toString();  
			}catch(Exception e) {
				return "Error:"+e.getMessage();
			}
		}
	
		//Function to login
		public static void login() throws SQLException {
		System.out.print("Email ID:");
		String email = sc.next();
		System.out.print("Password:");
//		char[] p = console.readPassword();
		String password = sc.next();
		String epass = passwordEncoder(password);
		
		Statement stmt = (Statement) conn.createStatement();
		
		//Checks if the username and password exists and is correct
		String query = "Select id,first_name,email,password from login where email='"+email+"' and password='"+epass+"'";
		ResultSet rs = stmt.executeQuery(query);
		
		if(rs.next()) {
			System.out.println("################### Login Successful ##################");
			System.out.println("                  Welcome "+rs.getString(2));
			
			//create an object for adminmodule if admin is logged in
			if(rs.getString(3).equals("admin@gmail.com")) {
				AdminModule a = new AdminModule(conn);
				a.functions();
			}
			else {
				
				//create object for usermodule if normal user is logged in
				UserModule u = new UserModule(conn);
				u.setId(rs.getInt(1));
				u.setUsername(rs.getString(2));
				u.functionality();
			}
		}
		else {
			System.out.println("Incorrect Email-id or password!! Try Again");
			System.out.println("Kindly Register if you are not registered");
		}
	}
	
	public static void register() throws SQLException {
		System.out.println("############################ New Registration ################################");
		System.out.print("Enter your first name:");
		String fname = sc.next();
		System.out.print("Enter your last name:");
		String lname = sc.next();
		System.out.print("Enter email id:");
		String emid = sc.next();
		System.out.print("Create password:");
		String pass1 = sc.next();
		System.out.print("Re-enter password:");
		String pass2 = sc.next();
		if(pass1.equals(pass2)==false) {
			System.out.println("The passwords do not match. Please register with correct passwords.");
		}
		else if(fname.equals("Admin") || emid.equals("admin@gmail.com")) {
			System.out.println("Sorry! Account cannot be created as admin");
		}
		else {
			String epass1 = passwordEncoder(pass1);
			PreparedStatement stmt1 = conn.prepareStatement("Insert into login(first_name,last_name,email,password) values (?,?,?,?)");
			stmt1.setString(1, fname);
			stmt1.setString(2, lname);
			stmt1.setString(3, emid);
			stmt1.setString(4, epass1);
			
			stmt1.executeUpdate();
			
			System.out.println("            User registered successfully!");
			System.out.println("            Kindly use the email id to login");
		}
	}
	public static void main(String[] args) throws SQLException, ClassNotFoundException{
//		Console console = System.console();
//		if (console == null) {
//            System.out.println(
//                "No console available");
//            return;
//        }
		System.out.println("**********************Welcome to the XYZ Book Store************************");
		System.out.println();
		int o;
		System.out.println("Kindly Select Appropriate Option====>");
		System.out.println("1. Login");
		System.out.println("2. Register");
		System.out.print("Your Choice: ");
		o = sc.nextInt();
		
		//creating an object of connection class
		conn= SQLConnection.getConnection();
		switch(o) {
		case 1:
			login();
			break;
		case 2:
			register();
			break;
		default:
			System.out.println("Invalid Choice");
		}
	}

}
