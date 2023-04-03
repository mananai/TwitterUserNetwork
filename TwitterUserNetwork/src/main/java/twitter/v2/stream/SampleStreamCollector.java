package twitter.v2.stream;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Predicate;
import java.util.logging.Logger;

import com.google.common.reflect.TypeToken;
import com.twitter.clientlib.ApiException;
import com.twitter.clientlib.JSON;
import com.twitter.clientlib.TwitterCredentialsBearer;
import com.twitter.clientlib.api.TwitterApi;
import com.twitter.clientlib.model.StreamingTweetResponse;
import com.twitter.clientlib.model.Tweet;

class BearerTokens {
	private final String[]  bearerTokens;
	private int next = 0;
	
	BearerTokens(String[] bearerTokens) {
		super();
		this.bearerTokens = bearerTokens;
		SampleStream2DB.logger.info("Got " + bearerTokens.length + " bearer tokens");
	}
	
	String getNext() {
		SampleStream2DB.logger.fine("Using bearer token#" + (next+1) + " of " + bearerTokens.length);
		String token = bearerTokens[next];
		next = ++next <  bearerTokens.length ? next : 0;
		return token;
	}
}

class SampleStreamCollector implements Callable<Integer> {
	
	private static final int TWITTER_API_FORBIDDEN_ERROR = 403;
	private static final int MAX_RECONNECT_COUNT = 20;
	private static final int SAMPLE_STREAM_REQUEST_DELAY = 15*60*1000/50;


	private final Predicate<Tweet> predicate;
	private final String[] bearerTokenArray;
	private final BlockingQueue<Tweet[]> tweetsQueue;
	private final AtomicBoolean isTerminated;
	private Logger logger = Logger.getLogger(this.getClass().getName());

	SampleStreamCollector(Predicate<Tweet> predicate,
			String[] bearerTokenArray, BlockingQueue<Tweet[]> tweetsQueue, AtomicBoolean isTerminated) {
		super();
		
		if ( tweetsQueue == null || predicate == null || bearerTokenArray == null || isTerminated == null)
			throw new IllegalArgumentException();

		this.predicate = predicate;
		this.bearerTokenArray = bearerTokenArray;
		this.tweetsQueue = tweetsQueue;
		this.isTerminated = isTerminated;
	}


	@Override
	public Integer call() throws Exception {

		int tweetCount = 0;
		BearerTokens bearerTokens = new BearerTokens( bearerTokenArray);
		try {

			Set<String> tweetFields = new HashSet<>(
					Arrays.asList("created_at", "conversation_id", "lang", "public_metrics", "referenced_tweets")); // Set<String>
			Set<String> expansions = new HashSet<>(Arrays.asList("author_id", "referenced_tweets.id")); // Set<String> |
			
			int reconnectCount = 0;
			do {//Reconnecting loop
				BufferedReader tweetReader = null;
				logger.fine((reconnectCount > 0 ? "Reconnecting": "Connecting") + " to Twitter...");
				try {
					TwitterCredentialsBearer bearer = new TwitterCredentialsBearer(bearerTokens.getNext());
					TwitterApi apiInstance = new TwitterApi(bearer);
					if ( reconnectCount > 0) {
						try {
							Thread.sleep(SAMPLE_STREAM_REQUEST_DELAY);
						} catch (InterruptedException e1) {
							e1.printStackTrace();
						}
					}
					InputStream result = apiInstance.tweets().sampleStream().tweetFields(tweetFields)
							.expansions(expansions).execute(MAX_RECONNECT_COUNT);
					logger.fine("Connected");
					
					@SuppressWarnings("serial")
					Type localVarReturnType = new TypeToken<StreamingTweetResponse>() {
					}.getType();
					tweetReader = new BufferedReader(new InputStreamReader(result));
					
					logger.info("Streaming...");
					while ( !isTerminated.get() ) {// Reading data loop
						
						String line = tweetReader.readLine();
						if (line == null) {
							logger.warning("Line is null");
							break;
						}
						else if (line.isEmpty()) 
							continue;
						
						Object jsonObject = JSON.getGson().fromJson(line, localVarReturnType);

						StreamingTweetResponse response = (StreamingTweetResponse) jsonObject;
						Tweet tweet = response.getData();
						if (tweet == null ) {
							logger.warning("Tweet is null");
							continue;
						}

						if ( predicate.test(tweet) ) {//if ( lang.equals(tweet.getLang())) {
							List<Tweet> includedTweets = response.getIncludes().getTweets();
							Tweet retweeted = null;
							Tweet quoted = null;
							Tweet repliedTo = null;
							tweetCount++;
							if (tweet.getReferencedTweets() != null) {
								for (var ref : tweet.getReferencedTweets()) {
									for (Tweet includedTweet : includedTweets) {
										if (includedTweet.getId().equals(ref.getId())) {
											switch (ref.getType()) {
											case RETWEETED:
												retweeted = includedTweet;
												tweetCount++;
												break;
											case QUOTED:
												quoted = includedTweet;
												tweetCount++;
												break;
											case REPLIED_TO:
												repliedTo = includedTweet;
												tweetCount++;
												break;
											}
											break;
										}
									}
								}
							}
							try {
								tweetsQueue.put(new Tweet[] { tweet, retweeted, quoted, repliedTo });
							} catch (InterruptedException e) {
								throw new RuntimeException(e);
							}
						}
					}//while (!isTerminated) 

				} 
				catch (IOException | ApiException e) {	// To handle connection reset
					if ( e instanceof ApiException) {
						if (((ApiException)e).getCode()==TWITTER_API_FORBIDDEN_ERROR) 
							logger.severe("API Forbidden");
						else
							throw e;
					}
					else
						logger.severe("Connection reset or socket timeout");

					e.printStackTrace(System.err);
					reconnectCount++;
				}
				finally {
					if (tweetReader != null ) {
						try {
							tweetReader.close();
						} catch (IOException e1) {
							logger.warning("Exception while closing a reader");
							e1.printStackTrace(System.err);
						}
					}
				}
			} while (!isTerminated.get() && reconnectCount <= MAX_RECONNECT_COUNT ); // do while not terminated
			
		} catch (ApiException e) {
			logger.severe("Exception when calling TweetsApi#sampleStream");
			logger.severe("Status code: " + e.getCode());
			logger.severe("Reason: " + e.getResponseBody());
			logger.severe("Response headers: " + e.getResponseHeaders());
			e.printStackTrace(System.err);
		} catch (Exception e) {
			e.printStackTrace(System.err);
		}
		finally {
			try {
				//Poison pill
				tweetsQueue.put(new Tweet[] {null, null, null, null});
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
			System.out.printf("%s has processed %,d tweets\n", this.getClass().getSimpleName(),  tweetCount);

		}
		return tweetCount;
	}

}
