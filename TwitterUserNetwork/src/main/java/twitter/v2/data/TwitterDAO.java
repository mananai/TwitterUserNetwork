package twitter.v2.data;

import java.sql.SQLException;
import java.util.Queue;

import com.twitter.clientlib.model.Tweet;

public interface TwitterDAO {
	boolean createTweetTable() throws SQLException;
	int saveTweets(Queue<Tweet[]> queue) throws SQLException;
	void close() throws SQLException;
}
