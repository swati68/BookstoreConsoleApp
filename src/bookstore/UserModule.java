package bookstore;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Scanner;

/*
 * Functions of User/Customer ------
 *   1) View all books in the repository
 *   	--> Order Book
 *   	--> Add to cart
 *   	--> Go Back
 *   2) Search book by Title
 *   	--> Order Book
 *   	--> Add to cart
 *   	--> Go Back
 *   3) Search book by Author Name
 *   	--> Order Book
 *   	--> Add to cart
 *   	--> Go Back
 *   4) Search book by Genre Name
 *   	--> Order Book
 *   	--> Add to cart
 *   	--> Go Back
 *   5) View Orders
 *   	--> Cancel Order
 *   	--> Go Back
 *   6) View Cart
 *   	--> Order Book
 *   	--> Go Back
 *   7) Logout
 */

public class UserModule {
	public Connection conn;
	public static Scanner sc = new Scanner(System.in);
	private int id;
	private String username;
	
	//Constuctor to initialize connection variable
	UserModule(Connection conn){
		this.conn = conn;
	}
	
	//getters and setters
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}
	
	public void searchAllBooks() throws SQLException {
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
		do {
			System.out.println("Select an Action---->");
			System.out.println("1. Order a Book");
			System.out.println("2. Add to Cart");
			System.out.println("3. Go Back");
			
			System.out.print("Your Choice: ");
			ch1 = sc.nextInt();
			
			if(ch1==1) {
				System.out.print("Enter the book id to order:");
				int bid = sc.nextInt();
				orderBook(bid);
			}
			else if(ch1==2) {
				System.out.print("Enter the book id to Add to cart:");
				int bid = sc.nextInt();
				addCart(bid);
			}
		}while(ch1!=3);
		
	}
	
	private void addCart(int bid) throws SQLException {
		PreparedStatement stmt2 = conn.prepareStatement("Select b_quantity from book_info where id=?");
		stmt2.setInt(1, bid);
		ResultSet rs2 = stmt2.executeQuery();
		
		if(rs2.next()) {
			if(rs2.getInt(1)>0) {
				PreparedStatement stmt = conn.prepareStatement("Insert into user_cart(user_id,book_id) values (?,?)");
				stmt.setInt(1, id);
				stmt.setInt(2, bid);
				stmt.executeUpdate();
				
				System.out.println("Book added to cart successfully");
			}
			else {
				System.out.println("Book Out of Stock!!");
				System.out.println("Cannot add to cart");
			}
		}
	}

	public void orderBook(int bid) throws SQLException {
		PreparedStatement stmt2 = conn.prepareStatement("Select b_quantity from book_info where id=?");
		stmt2.setInt(1, bid);
		ResultSet rs2 = stmt2.executeQuery();
		
		if(rs2.next()) {
			if(rs2.getInt(1)>0) {
				Timestamp timestamp = new Timestamp(System.currentTimeMillis()); 
				PreparedStatement stmt = conn.prepareStatement("Insert into order_detail(user_id,book_id,date) values (?,?,?)");
				stmt.setInt(1, id);
				stmt.setInt(2, bid);
				stmt.setTimestamp(3, timestamp);
				stmt.executeUpdate();
				
				int newq = rs2.getInt(1)-1;
				PreparedStatement stmt3 = conn.prepareStatement("Update book_info set b_quantity=? where id=?");
				stmt3.setInt(1, newq);
				stmt3.setInt(2, bid);
				stmt3.execute();
				System.out.println("Order placed successfully");
			}
			else {
				System.out.println("Book Out of Stock!!");
				System.out.println("Cannot place order");
			}
		}
		
	}
	
	public void searchUsingBook() throws SQLException{
		System.out.println("Enter the book name to search:");
		sc.nextLine();
		String book = sc.nextLine();
		PreparedStatement stmt = conn.prepareStatement("Select * from book_info where b_name=?");
		stmt.setString(1, book);
		ResultSet rs = stmt.executeQuery();
		if(rs.next()) {
			PreparedStatement stmt1 = conn.prepareStatement("Select * from author_info where id=?");
			stmt1.setInt(1, rs.getInt(3));
			ResultSet rs1 = stmt1.executeQuery();
			
			PreparedStatement stmt2 = conn.prepareStatement("Select * from genre_info where id=?");
			stmt2.setInt(1, rs.getInt(4));
			ResultSet rs2 = stmt2.executeQuery();
			if(rs1.next() && rs2.next())
				System.out.println(rs.getInt(1)+") "+rs.getString(2)+" , "+rs1.getString(2)+" , "+rs2.getString(2)+" , "+rs.getInt(5)+" , "+rs.getInt(6));
			
			int ch1;
				System.out.println();
				System.out.println("Select an Action-->");
				System.out.println("1. Order a Book");
				System.out.println("2. Add to Cart");
				System.out.println("3. Go Back");
				
				System.out.print("Your Choice: ");
				ch1 = sc.nextInt();
				
				if(ch1==1) {
					orderBook(rs.getInt(1));
				}
				else if(ch1==2) {
					addCart(rs.getInt(1));
				}
			
			
		}else {
			System.out.println("No books available by the name "+book);
		}
		
	}
	
	public void searchUsingAuthor() throws SQLException{
		System.out.println("Enter the author name to search:");
		sc.nextLine();
		String auth = sc.nextLine();
		PreparedStatement stmt = conn.prepareStatement("Select * from author_info where a_name=?");
		stmt.setString(1, auth);
		ResultSet rs = stmt.executeQuery();
		if(rs.next()) {
			PreparedStatement stmt2 = conn.prepareStatement("Select * from book_info where author_id=?");
			stmt2.setInt(1, rs.getInt(1));
			ResultSet rs2 = stmt2.executeQuery();
			while(rs2.next()) {
				PreparedStatement stmt3 = conn.prepareStatement("Select * from genre_info where id=?");
				stmt3.setInt(1, rs2.getInt(4));
				ResultSet rs3 = stmt3.executeQuery();
				if(rs3.next())
					System.out.println(rs2.getInt(1)+") "+rs2.getString(2)+" , "+rs.getString(2)+" , "+rs3.getString(2)+" , "+rs2.getInt(5)+" , "+rs2.getInt(6));
			}
				int ch1;
				do {
					System.out.println();
					System.out.println("Select an Action-->");
					System.out.println("1. Order a Book");
					System.out.println("2. Add to Cart");
					System.out.println("3. Go Back");
					
					System.out.print("Your Choice: ");
					ch1 = sc.nextInt();
					
					if(ch1==1) {
						System.out.print("Enter the book id to order:");
						int bid = sc.nextInt();
						orderBook(bid);
					}
					else if(ch1==2) {
						System.out.print("Enter the book id to add to cart:");
						int bid = sc.nextInt();
						addCart(bid);
					}
				}while(ch1!=3);
				
		}else {
			System.out.println("No books available by the author "+auth);
		}
	}
	
	public void searchUsingGenre() throws SQLException{
		System.out.println("Enter the genre name to search:");
		sc.nextLine();
		String gen = sc.nextLine();
		PreparedStatement stmt = conn.prepareStatement("Select * from genre_info where genre_type=?");
		stmt.setString(1, gen);
		ResultSet rs = stmt.executeQuery();
		if(rs.next()) {
			PreparedStatement stmt2 = conn.prepareStatement("Select * from book_info where genre_id=?");
			stmt2.setInt(1, rs.getInt(1));
			ResultSet rs2 = stmt2.executeQuery();
			while(rs2.next()) {
				PreparedStatement stmt3 = conn.prepareStatement("Select * from author_info where id=?");
				stmt3.setInt(1, rs2.getInt(3));
				ResultSet rs3 = stmt3.executeQuery();
				if(rs3.next())
					System.out.println(rs2.getInt(1)+") "+rs2.getString(2)+" , "+rs3.getString(2)+" , "+rs.getString(2)+" , "+rs2.getInt(5)+" , "+rs2.getInt(6));
			}
				int ch1;
				do {
					System.out.println();
					System.out.println("Select an Action-->");
					System.out.println("1. Order a Book");
					System.out.println("2. Add to Cart");
					System.out.println("3. Go Back");
					
					System.out.print("Your Choice: ");
					ch1 = sc.nextInt();
					
					if(ch1==1) {
						System.out.print("Enter the book id to order:");
						int bid = sc.nextInt();
						orderBook(bid);
					}
					else if(ch1==2) {
						System.out.print("Enter the book id to add to cart:");
						int bid = sc.nextInt();
						addCart(bid);
					}
				}while(ch1!=3);
		}else {
			System.out.println("No books available by the genre "+gen);
		}
	}
	
	public void viewOrders() throws SQLException {
		PreparedStatement stmt = conn.prepareStatement("Select * from order_detail where user_id=?");
		stmt.setInt(1, id);
		ResultSet rs = stmt.executeQuery();
		int i=1;
		if(rs.next()) {
			System.out.println("S.No.  ||  ID   ||  Book Name  ||   Author Name    ||   Genre Type   ||  Price   ||   Date    ||   Status");
			while(rs.next()) {
				PreparedStatement stmt2 = conn.prepareStatement("Select * from book_info where id=?");
				stmt2.setInt(1, rs.getInt(3));
				ResultSet rs2 = stmt2.executeQuery();
				
				if(rs2.next()) {
					PreparedStatement stmt3 = conn.prepareStatement("Select * from author_info where id=?");
					stmt3.setInt(1, rs2.getInt(3));
					ResultSet rs3 = stmt3.executeQuery();
					
					PreparedStatement stmt4 = conn.prepareStatement("Select * from genre_info where id=?");
					stmt4.setInt(1, rs2.getInt(4));
					ResultSet rs4 = stmt4.executeQuery();
					
					if(rs3.next() && rs4.next()) {
						String status;
						if(rs.getInt(5)==0) {
							status = "Yet to be Delivered";
						}
						else if(rs.getInt(5)==1){
							status = "Delivered";
						}
						else {
							status="Order cancelled";
						}
						System.out.println(i+") "+rs.getInt(1)+" , "+rs2.getString(2)+" , "+rs3.getString(2)+" , "+rs4.getString(2)+" , "+rs2.getInt(5)+" , "+rs.getDate(4)+" , "+status);
					}
				}
				i++;
			}
			System.out.println();
			int ch1;
			do {
				System.out.println("Select an Action-->");
				System.out.println("1. Cancel Orders");
				System.out.println("2. Go Back");
				System.out.print("Your Choice:");
				ch1 = sc.nextInt();
				
				if(ch1==1) {
					System.out.println("Enter the order ID to cancel order:");
					int oid = sc.nextInt();
					cancelOrders(oid);
				}
			}while(ch1!=2);
		}
		else {
			System.out.println("No orders placed yet. Kindly view books to start shopping");
		}
		
		
	}
	
	public void cancelOrders(int oid) throws SQLException {
		PreparedStatement stmt = conn.prepareStatement("Update order_detail set status=? where id=?");
		stmt.setInt(1, -1);
		stmt.setInt(2, oid);
		stmt.executeUpdate();
		
		PreparedStatement stmt2 = conn.prepareStatement("Select book_id from order_detail where id=?");
		stmt2.setInt(1, oid);
		ResultSet rs2 = stmt2.executeQuery();
		
		if(rs2.next()) {
			PreparedStatement stmt3 = conn.prepareStatement("Update book_info set b_quantity=b_quantity+1 where id=?");
			stmt3.setInt(1, rs2.getInt(1));
			stmt3.execute();
		}
		System.out.println("Order cancelled successfully!!");
	}
	
	private void viewCart() throws SQLException {
		PreparedStatement stmt = conn.prepareStatement("Select * from user_cart where user_id=? and status=?");
		stmt.setInt(1, id);
		stmt.setInt(2, 0);		
		ResultSet rs = stmt.executeQuery();
		
		if(rs.next()) {
			System.out.println("S.No. ||   Book ID  ||   Book Name   ||  Price");
			int i=1;
			while(rs.next()) {
				PreparedStatement stmt2 = conn.prepareStatement("Select * from book_info where id=?");
				stmt2.setInt(1, rs.getInt(3));
				ResultSet rs2 = stmt2.executeQuery();
				if(rs2.next()) {
					System.out.println(i+" , "+rs.getInt(3)+" , "+rs2.getString(2)+" , "+rs2.getInt(5));
				}
				i++;
			}
			int ch1;
			do {
				System.out.println();
				System.out.println("Select an Action-->");
				System.out.println("1. Place an Order");
				System.out.println("2. Go Back");
				System.out.print("Your Choice: ");
				ch1 = sc.nextInt();
				if(ch1==1) {
					System.out.print("Enter the book id to order:");
					int bid = sc.nextInt();
					
					PreparedStatement stmt3 = conn.prepareStatement("Update user_cart set status=? where user_id=? and book_id=?");
					stmt3.setInt(1, 1);
					stmt3.setInt(2, id);
					stmt3.setInt(3, bid);
					stmt3.execute();
					orderBook(bid);
				}
			}while(ch1!=2);
		}
		else {
			System.out.println("Cart is empty");
		}
		
	}

	public void functionality() throws SQLException{
		int ch;
		do {
			System.out.println();
			System.out.println("--------------------------Choose an action to perform-----------------------");
			System.out.println("1. Search All Books");
			System.out.println("2. Search Using Book Names");
			System.out.println("3. Search Using Author Names");
			System.out.println("4. Search Using Book Genre");
			System.out.println("5. View Cart");
			System.out.println("6. View Orders");
			System.out.println("7. Logout");
			System.out.print("Your Choice:-");
			ch = sc.nextInt();
			System.out.println();
			switch(ch) {
			case 1:
				searchAllBooks();
				break;
			case 2:
				searchUsingBook();
				break;
			case 3:
				searchUsingAuthor();
				break;
			case 4:
				searchUsingGenre();
				break;
			case 5:
				viewCart();
				break;
			case 6:
				viewOrders();
				break;
			case 7:
				System.out.println("User is logged out");
				break;
			}
		}while(ch!=7);
	}
}
