package bookstore;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SQLConnection {
	private static Connection conn;
	
	public static Connection getConnection() throws SQLException, ClassNotFoundException{
			Class.forName("com.mysql.jdbc.Driver");
			conn=DriverManager.getConnection("jdbc:mysql://localhost:3308/online_book","root","12345");
			//System.out.println(conn);
			return conn;

	}
}
