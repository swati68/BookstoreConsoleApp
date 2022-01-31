package bookstore;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;
/*
 *  Functions of Admin --------
 *  1) View All Books
 *  	--> Add a New Book
 *  	--> Delete a Book
 *  	--> Alter Book Details
 *  2) View All Orders
 *  	--> Change Order Details
 *  3) View All Customers
 *  	--> View Individual Customer's Orders
 *  4) Logout
 */

public class AdminModule {
	public Connection conn;
	public static Scanner sc = new Scanner(System.in);
	
	AdminModule(Connection conn){
		this.conn = conn;
	}
	
	public void viewAllBooks() throws SQLException {
		Statement stmt = conn.createStatement();
		ResultSet rs = stmt.executeQuery("Select * from book_info");
		System.out.println();
		System.out.println("ID   ||   Book Title   ||  Author  ||  Genre  ||   Price   ||   Quantity");
		while(rs.next()) {
			PreparedStatement stmt1 = conn.prepareStatement("Select * from author_info where id=?");
			stmt1.setInt(1, rs.getInt(3));
			ResultSet rs1 = stmt1.executeQuery();
			
			PreparedStatement stmt2 = conn.prepareStatement("Select * from genre_info where id=?");
			stmt2.setInt(1, rs.getInt(4));
			ResultSet rs2 = stmt2.executeQuery();
			
			if(rs1.next() && rs2.next())
				System.out.println(rs.getInt(1)+") "+rs.getString(2)+" , "+rs1.getString(2)+" , "+rs2.getString(2)+" , "+rs.getInt(5)+" , "+rs.getInt(6));
		}
		
		int ch1;
		
		System.out.println();
		System.out.println("Select an Action-->");
		System.out.println("1. Add a new book");
		System.out.println("2. Delete a Book");
		System.out.println("3. Alter Book Info");
		System.out.println("4. Go Back");
		
		System.out.print("Your Choice: ");
		ch1 = sc.nextInt();
		
		switch(ch1) {
		case 1:
			addBook();
			break;
		case 2:
			System.out.print("Enter book id to delete:");
			int bid = sc.nextInt();
			deleteBook(bid);
			break;
		case 3:
			System.out.print("Enter book id to alter info:");
			int b = sc.nextInt();
			alterBook(b);
			break;
		default:
			break;
		}
		
	}
	
	private void alterBook(int b) throws SQLException {
		System.out.println("Select an Action-->");
		System.out.println("1. Change Author");
		System.out.println("2. Change Price");
		System.out.println("3. Alter price");
		System.out.println("4. Alter Quantity");
		System.out.println("5. Go Back");
		System.out.print("Your Choice: ");
		int ch = sc.nextInt();
		switch(ch) {
		case 1:
			System.out.println("Select Author:");
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery("Select * from author_info");
			System.out.println("ID    ||    Author Name");
			while(rs.next()) {
				System.out.println(rs.getInt(1)+" ) "+rs.getString(2));
			}
			System.out.println();
			int ch1;
			System.out.print("Enter author id to choose author:");
			ch1 = sc.nextInt();
			
			PreparedStatement stmt2 = conn.prepareStatement("Update book_info set author_id=? where id=?");
			stmt2.setInt(1, ch1);
			stmt2.setInt(2, b);
			stmt2.execute();
			
			System.out.println("Author Updated Successfully");
			break;
			
		case 2:
			System.out.println("Select Genre:");
			Statement stmt3 = conn.createStatement();
			ResultSet rs3 = stmt3.executeQuery("Select * from genre_info");
			System.out.println("ID || Genre Name");
			while(rs3.next()) {
				System.out.println(rs3.getInt(1)+" ) "+rs3.getString(2));
			}
			System.out.println();
			int ch2;
			System.out.print("Enter genre id to choose genre:");
			ch2 = sc.nextInt();
			
			PreparedStatement stmt4 = conn.prepareStatement("Update book_info set genre_id=? where id=?");
			stmt4.setInt(1, ch2);
			stmt4.setInt(2, b);
			stmt4.execute();
			
			System.out.println("Genre Updated Successfully");
			break;
			
		case 3:
			System.out.print("Enter updated price:");
			int p = sc.nextInt();
			
			PreparedStatement stmt5 = conn.prepareStatement("Update book_info set b_price=? where id=?");
			stmt5.setInt(1, p);
			stmt5.setInt(2, b);
			stmt5.execute();
			
			System.out.println("Price Updated Successfully");
			break;
			
		case 4:
			System.out.print("Enter updated quantity:");
			int q = sc.nextInt();
			
			PreparedStatement stmt6 = conn.prepareStatement("Update book_info set b_quantity=? where id=?");
			stmt6.setInt(1, q);
			stmt6.setInt(2, b);
			stmt6.execute();
			
			System.out.println("Quantity Updated Successfully");
			break;
			
		default:
			break;
		}
	}

	private void deleteBook(int bid) throws SQLException {
		PreparedStatement stmt = conn.prepareStatement("Delete from book_info where id=?");
		stmt.setInt(1, bid);
		stmt.execute();
		System.out.println("Book with id "+bid+" deleted successfully");
	}

	private void addBook() throws SQLException {
		String bname;
		int aid = 0;
		int gid =0;
		int price;
		int quantity;
		System.out.println("Enter the book information:--");
		System.out.println("Book Name:");
		sc.nextLine();
		bname = sc.nextLine();
		System.out.println("Select Author:");
		Statement stmt = conn.createStatement();
		ResultSet rs = stmt.executeQuery("Select * from author_info");
		System.out.println("ID || Author Name");
		while(rs.next()) {
			System.out.println(rs.getInt(1)+" ) "+rs.getString(2));
		}
		System.out.println();
		int ch1;
		System.out.println("Select an Action-->");
		System.out.println("1. Enter author id to choose author");
		System.out.println("2. Add a new Author");
		System.out.println("Your Choice:");
		ch1 = sc.nextInt();
		if(ch1==1) {
			System.out.print("Enter author id:");
			aid = sc.nextInt();
		}
		else {
			System.out.print("Enter the author name to add:");
			sc.nextLine();
			String aname = sc.nextLine();
			
			PreparedStatement stmt2 = conn.prepareStatement("Insert into author_info(a_name) values (?)");
			stmt2.setString(1, aname);
			stmt2.executeUpdate();
			
			Statement stmt3 = conn.createStatement();
			ResultSet rs3 = stmt3.executeQuery("Select max(id) from author_info");
			if(rs3.next()) {
				aid = rs3.getInt(1);
			}
		}
		
		System.out.println("Select Genre:");
		Statement stmt4 = conn.createStatement();
		ResultSet rs4 = stmt4.executeQuery("Select * from genre_info");
		System.out.println("ID   ||    Genre Name");
		while(rs4.next()) {
			System.out.println(rs4.getInt(1)+" ) "+rs4.getString(2));
		}
		System.out.println();
		int ch2;
		System.out.println("Select an Action-->");
		System.out.println("1. Enter genre id to choose genre");
		System.out.println("2. Add a new Genre");
		System.out.println("Your Choice:");
		ch2 = sc.nextInt();
		if(ch2==1) {
			System.out.print("Enter genre id:");
			gid = sc.nextInt();
		}
		else {
			System.out.print("Enter the genre name to add:");
			sc.nextLine();
			String gname = sc.nextLine();
			PreparedStatement stmt5 = conn.prepareStatement("Insert into genre_info(genre_type) values (?)");
			stmt5.setString(1, gname);
			stmt5.executeUpdate();
			
			Statement stmt6 = conn.createStatement();
			ResultSet rs6 = stmt6.executeQuery("Select max(id) from genre_info");
			if(rs6.next()) {
				gid = rs6.getInt(1);
			}
		}
		
		System.out.print("Price: ");
		price = sc.nextInt();
		
		System.out.print("Quantity: ");
		quantity = sc.nextInt();
		
		PreparedStatement stmt7 = conn.prepareStatement("Insert into book_info(b_name,author_id,genre_id,b_price,b_quantity) values (?,?,?,?,?)");
		stmt7.setString(1, bname);
		stmt7.setInt(2, aid);
		stmt7.setInt(3, gid);
		stmt7.setInt(4, price);
		stmt7.setInt(5, quantity);
		stmt7.executeUpdate();
		System.out.println("Book "+bname+" inserted successfully.");
	}

	private void viewCustomers() throws SQLException {
		Statement stmt = conn.createStatement();
		ResultSet rs = stmt.executeQuery("Select * from login where first_name<>'Admin'");
		
		System.out.println("ID   ||   First Name   ||  Last Name  ||  Email");
		while(rs.next()) {
			System.out.println(rs.getInt(1)+") "+rs.getString(2)+" , "+rs.getString(3)+" , "+rs.getString(4));
		}
		
		System.out.println();
		System.out.println("Select an Action-->");
		System.out.println("1. View a customer's order");
		System.out.println("2. Go Back");
		System.out.print("Your Choice: ");
		int ch = sc.nextInt();
		if(ch==1) {
			System.out.print("Enter the customer id:");
			int cid = sc.nextInt();
			PreparedStatement stmt2 = conn.prepareStatement("Select * from order_detail where user_id=?");
			stmt2.setInt(1, cid);
			ResultSet rs2 = stmt2.executeQuery();
			
			System.out.println("ID   ||   Customer ID   ||  Customer Name    ||    Book ID  ||  Book Name   ||   Date  ||   Status");
			while(rs2.next()) {
					PreparedStatement stmt3 = conn.prepareStatement("Select first_name from login where id=?");
					stmt3.setInt(1, rs2.getInt(2));
					ResultSet rs3 = stmt3.executeQuery();
					
					PreparedStatement stmt4 = conn.prepareStatement("Select b_name from book_info where id=?");
					stmt4.setInt(1, rs2.getInt(3));
					ResultSet rs4 = stmt4.executeQuery();
					
					String status;
					if(rs2.getInt(5)==0) {
						status="Not yet delivered";
					}
					else if(rs2.getInt(5)==1) {
						status="Delivered";
					}
					else {
						status="Order Cancelled";
					}
					
					if(rs3.next() && rs4.next()) {
						System.out.println(rs2.getInt(1)+") "+rs2.getInt(2)+" , "+rs3.getString(1)+" , "+rs2.getInt(3)+" , "+rs4.getString(1)+" , "+rs2.getTimestamp(4)+" , "+status);
					}
				}
			}
		}

	private void viewOrders() throws SQLException {
		Statement stmt = conn.createStatement();
		ResultSet rs = stmt.executeQuery("Select * from order_detail");
		System.out.println();
		System.out.println("ID   ||   Customer ID   ||  Customer Name    ||    Book ID  ||  Book Name   ||   Date  ||   Status");
		
		while(rs.next()) {
			PreparedStatement stmt1 = conn.prepareStatement("Select first_name from login where id=?");
			stmt1.setInt(1, rs.getInt(2));
			ResultSet rs1 = stmt1.executeQuery();
			
			PreparedStatement stmt2 = conn.prepareStatement("Select b_name from book_info where id=?");
			stmt2.setInt(1, rs.getInt(3));
			ResultSet rs2 = stmt2.executeQuery();
			
			String status;
			if(rs.getInt(5)==0) {
				status="Not yet delivered";
			}
			else if(rs.getInt(5)==1) {
				status="Delivered";
			}
			else {
				status="Order Cancelled";
			}
			
			if(rs1.next() && rs2.next()) {
				System.out.println(rs.getInt(1)+") "+rs.getInt(2)+" , "+rs1.getString(1)+" , "+rs.getInt(3)+" , "+rs2.getString(1)+" , "+rs.getTimestamp(4)+" , "+status);
			}
		}
		
		int ch;
		System.out.println();
		System.out.println("Select an Action-->");
		System.out.println("1. Change Order Status");
		System.out.println("2. Go Back");
		System.out.print("Your Choice: ");
		ch = sc.nextInt();
		if(ch==1) {
			System.out.print("Enter the order id to change: ");
			int oid = sc.nextInt();
			
			PreparedStatement stmt3 = conn.prepareStatement("Select status from order_detail where id=?");
			stmt3.setInt(1, oid);
			ResultSet rs3 = stmt3.executeQuery();
			if(rs3.next()) {
				if(rs3.getInt(1)==1 || rs3.getInt(1)==-1) {
					System.out.println("No changes can be made. Order Already Delivered or Cancelled");
				}
				else {
					PreparedStatement stmt4 = conn.prepareStatement("Update order_detail set status=? where id=?");
					stmt4.setInt(1, 1);
					stmt4.setInt(2, oid);
					stmt4.execute();
					System.out.println("Order Status changed successfully");
				}
			}
		}
	}
	
	public void functions() throws SQLException {
		int ch;
		do {
			System.out.println();
			System.out.println("---------Choose an action to perform-------");
			System.out.println("1. View All Books");
			System.out.println("2. View Orders");
			System.out.println("3. View Customers");
			System.out.println("4. Logout");
			System.out.print("Your Choice:");
			ch = sc.nextInt();
			System.out.println();
			switch(ch) {
			case 1:
				viewAllBooks();
				break;
			case 2:
				viewOrders();
				break;
			case 3:
				viewCustomers();
				break;
			case 4:
				System.out.println("User is logged out");
				break;
			}
		}while(ch!=4);
	}
}
