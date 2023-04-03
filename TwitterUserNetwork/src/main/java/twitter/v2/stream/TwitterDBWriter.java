package twitter.v2.stream;

import java.sql.SQLException;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;

import com.twitter.clientlib.model.Tweet;

import twitter.v2.data.TwitterDAO;
import twitter.v2.data.TwitterDAOFactory;

class TwitterDBWriter implements Callable<Integer> {
	
	private final static int TWEETS_BATCH_SIZE = 20;

	private final BlockingQueue<Tweet[]> tweetsQueue;
	private final Queue<Tweet[]> writingQueue = new LinkedList<>();
	private final TwitterDAO twitterDAO ;

	TwitterDBWriter(String jdbcURL, BlockingQueue<Tweet[]> tweetsQueue) throws SQLException {
		super();
		this.twitterDAO = TwitterDAOFactory.create(jdbcURL);
		this.tweetsQueue = tweetsQueue;
	}

	@Override
	public Integer call() {
		int count = 0;
		try {
			Tweet[] tweets;
			do {
				tweets = tweetsQueue.take();

				if (!isPoisonPill(tweets))
					writingQueue.add(tweets);
				
				if ((writingQueue.size() >= TWEETS_BATCH_SIZE) || (isPoisonPill(tweets) && !writingQueue.isEmpty())) {
					count += twitterDAO.saveTweets(writingQueue);
				}
			} while (!isPoisonPill(tweets));
			
			this.twitterDAO.close();
			System.out.printf("%s has inserted or updated %,d tweets\n", this.getClass().getSimpleName(), count);
		} catch (InterruptedException | SQLException e) {
			e.printStackTrace(System.err);
		}
		return count;
	}
	
	private boolean isPoisonPill(Tweet[] tweetArray) {
		return tweetArray[0] == null;
	}
}
