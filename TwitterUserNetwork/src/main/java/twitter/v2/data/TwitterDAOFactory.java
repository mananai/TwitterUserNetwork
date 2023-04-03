package twitter.v2.data;

import java.sql.SQLException;

public class TwitterDAOFactory {
	public static TwitterDAO create(String jdbcURL) throws SQLException {
		return new TwitterSQLiteDB(jdbcURL);
	}
}
