package twitter.v2.data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Queue;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.twitter.clientlib.model.Tweet;

class TwitterSQLiteDB implements TwitterDAO {	
	private final Connection connection;
	private Logger logger = Logger.getLogger(this.getClass().getName());
	private int totalCount = 0;

	TwitterSQLiteDB(String jdbcURL) throws SQLException {
		connection = DriverManager.getConnection(jdbcURL);
		connection.setAutoCommit(false);
		createTweetTable();
	}
	
	public void close() throws SQLException {
		connection.close();
		logger.info("Inserted or updated " + totalCount + " tweets to DB");
	}
	
	public boolean createTweetTable() throws SQLException {
		var stmt = connection.createStatement();
		final String sql = 
				"CREATE TABLE IF NOT EXISTS Tweet(\n" + 
				"    id            VARCHAR(25) PRIMARY KEY,\n" + 
				"    author_id     VARCHAR(25),\n" + 
				"    created_at    VARCHAR (25),\n" + 
				"    lang          VARCHAR(10),\n" + 
				"    text          TEXT,\n" + 
				"    retweeted_id  VARCHAR(25),\n" + 
				"    quoted_id     VARCHAR(25),\n" + 
				"    replied_to_id VARCHAR(25),\n" + 
				"    retweet_count INT,\n" + 
				"    quote_count   INT,\n" + 
				"    reply_count   INT,\n" + 
				"    like_count    INT\n" + 
				");";
		boolean result = stmt.execute(sql);
		stmt.close();
		return result;
	}

	public int saveTweets(Queue<Tweet[]> queue) throws SQLException {
		if (queue.isEmpty())
			return 0;
		
		PreparedStatement stmt = this.prepare();
		while ( !queue.isEmpty()) {
			Tweet[] tweets = queue.remove();
			saveTweetAndReferenced(stmt, tweets[0], tweets[1], tweets[2], tweets[3]);
		}
		int results[] = stmt.executeBatch();
		connection.commit();
		int count = Arrays.stream(results).sum();
		totalCount += count;
		if ( logger.isLoggable(Level.FINE))
			logger.fine("Saved " + count + " tweets");
		return count;
	}

	private PreparedStatement prepare() throws SQLException {
		return connection.prepareStatement(
				"INSERT OR REPLACE INTO tweet " + 
				"(id, author_id, created_at, lang, text, " + 
				"retweeted_id, quoted_id, replied_to_id, "  + 
				"retweet_count, quote_count, reply_count, like_count) " +
				"values (?,?,?,?,?,?,?,?,?,?,?,?);");
	}
	
	private void saveTweetAndReferenced(PreparedStatement stmt, Tweet tweet, Tweet retweeted, Tweet quoted, Tweet repliedTo) throws SQLException {
		
		this.saveTweet(stmt, tweet, 
				retweeted==null ? null : retweeted.getId(), 
				quoted == null ? null : quoted.getId(),
				repliedTo == null ? null : repliedTo.getId());
		if ( retweeted !=null ) {
			this.saveTweet(stmt, retweeted, null, null, null);
		}
		if ( quoted !=null) {
			this.saveTweet(stmt, quoted, null, null, null);
		}
		if (repliedTo!=null) {
			this.saveTweet(stmt, repliedTo, null, null, null);
		}
	}

	private void saveTweet(PreparedStatement stmt,
			Tweet tweet, String retweetedId, String quotedId, String repliedToId) 
					throws SQLException {
		stmt.setString(1, tweet.getId());
		stmt.setString(2, tweet.getAuthorId());
		stmt.setString(3, tweet.getCreatedAt().atZoneSameInstant(ZoneId.systemDefault()).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
		stmt.setString(4, tweet.getLang());
		stmt.setString(5, retweetedId == null ? tweet.getText() : null);
		stmt.setString(6, retweetedId);
		stmt.setString(7, quotedId);
		stmt.setString(8, repliedToId);
		stmt.setObject(9, tweet.getPublicMetrics() != null ? tweet.getPublicMetrics().getRetweetCount() : null);
		stmt.setObject(10, tweet.getPublicMetrics() != null ? tweet.getPublicMetrics().getQuoteCount() : null);
		stmt.setObject(11, tweet.getPublicMetrics() != null ? tweet.getPublicMetrics().getReplyCount() : null);
		stmt.setObject(12, tweet.getPublicMetrics() != null ? tweet.getPublicMetrics().getLikeCount() : null);
		stmt.addBatch();
	}
}
