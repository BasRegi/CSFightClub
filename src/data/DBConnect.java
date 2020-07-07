package data;

import java.sql.*;

/**
 * 
 * @author rxk592
 * A class just to setup a connection to the database
 */
public class DBConnect {
	
	String username = new String();
	String password = new String();
	String database = new String();
	String url = new String(); 
	Connection conn = null;
	
	public DBConnect() {
		 username = "d3";
	     password = "zergekTuce"; //insecure but much better than having to enter every time
	     database = "d3";
	     url = "jdbc:postgresql://mod-team-project.cs.bham.ac.uk/" + database;
	}
	
	/**
	 * Open the connection to the database
	 * @param state 0 for outputting updates as the method executes, 1 for no messages
	 * @return the Connection to the database
	 */
	public Connection open(int state) {
		try {
			//Load the PostgreSQL JDBC driver
            Class.forName("org.postgresql.Driver");

        } catch (ClassNotFoundException ex) {
            System.out.println("Driver not found");
            return null;
        }
		
		if(state == 0)
        	System.out.println("PostgreSQL driver registered.");

        try {
            conn = DriverManager.getConnection(url, username, password);
        } catch (SQLException ex) {
            System.out.println("Couldn't get a connection");
            return null;
        }

        if (conn != null) {
        	if(state == 0)
        		System.out.println("Database accessed.");
        } else {
            System.out.println("Failed to make connection");
            return null;
        }
        
        return conn;
	}
	
	/**
	 * Close the connection to the database
	 * @param state 0 for outputting updates as the method executes, 1 for no messages
	 */
	public void close(int state) {
		try {
            conn.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
		if(state == 0)
			System.out.println("Database connection closed.");
	}
}
