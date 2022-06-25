package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DBResources {

	private final String jdbcURL;
	private Connection connection;

	private static DBResources instance;
	
	private DBResources(String jdbcURL) {
		super();
		this.jdbcURL = jdbcURL;
		
    	try {
    		connection = DriverManager.getConnection(this.jdbcURL);
    	} catch (SQLException e) {
    		e.printStackTrace();
    		throw new RuntimeException(e);
    	}
	}
	
	public static DBResources newInstance(String jdbcURL) {
		if (instance == null ) {
			instance = new DBResources(jdbcURL);
		}
		return instance;
	}
	

	public static DBResources getInstance() {
		return instance;
	}

	public Connection getConnection() {
		return connection;
	}
	
	public PreparedStatement prepareStatement(String sql) throws SQLException {
		return connection.prepareStatement(sql);
	}
	
	public synchronized int executeUpdate(String sql, Object... values) throws SQLException {
		PreparedStatement pstmt = null;
		try {
			pstmt = connection.prepareStatement(sql);
			setStatementParameters(pstmt, values);
			return pstmt.executeUpdate();
		} finally {
			if (pstmt!=null)
				pstmt.close();
		}
	}
	
	public synchronized ResultSet executeQuery(String sql, Object... values) throws SQLException {
		PreparedStatement pstmt = null;
		pstmt = connection.prepareStatement(sql);
		setStatementParameters(pstmt, values);
		return pstmt.executeQuery();
	}
	
	public void setStatementParameters(PreparedStatement stmt, Object... values) throws SQLException {
		int i = 1;
		for (Object value: values) {
			if (value == null)
				stmt.setObject(i, value);
			else {
				if (value instanceof String) {
					stmt.setString(i, (String)value);
				}
				else if (value instanceof Integer ) {
					stmt.setInt(i, (Integer)value);
				}
				else if (value instanceof Long) {
					stmt.setLong(i, (Long)value);
				}
				else  {
					stmt.setObject(i, value);
				}
			}
			i++;
		}
		
	}

	public void close() {
		
		if (connection != null)
			try {
				connection.close();
				connection = null;
			} catch (SQLException e) {
				e.printStackTrace();
			}
	}
}
