package twitter.friendnetwork;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.function.Function;

import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.Trend;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.User;

public class SearchTweetCallable implements Callable<Integer> {
	private static final int SEARCH_CALL_DELAY = 5*1000;

	private final Twitter twitter;
	private final int maxTweets;
	private final int numPages;
	private final String lang;
	private final BlockingQueue<Trend> trendsQueue;
	private final Function<Collection<User>, Future<Integer>> addUsersFunc;

	
	public SearchTweetCallable(Twitter twitter, int maxTweets, int numPages, String lang,
			BlockingQueue<Trend> trendsQueue, Function<Collection<User>, Future<Integer>> addUsersFunc) {
		super();
		this.twitter = twitter;
		this.maxTweets = maxTweets;
		this.numPages = numPages;
		this.lang = lang;
		this.trendsQueue = trendsQueue;
		this.addUsersFunc = addUsersFunc;
	}

	@Override
	public Integer call() {
		Trend trend;
		List<Future<Integer>> taskResults = new ArrayList<>();
		while ((trend = this.trendsQueue.poll()) != null) {
        	Query query = new Query(trend.getQuery());
        	query.setCount(maxTweets);
        	if ( lang != null)
        		query.setLang(lang);
        	
        	for ( int page=0 ; page < numPages ; page++) {
            	QueryResult queryResult;
    			try {
    				queryResult = twitter.search(query);
    			} catch (TwitterException e1) {
    				e1.printStackTrace();
    				break;
    			}
            	Set<User> users = new HashSet<>();
            	for (var status : queryResult.getTweets()) {
            		users.add(status.getUser());
            		if (status.isRetweet()) {
            			users.add(status.getRetweetedStatus().getUser());
            		}
            		if ( status.getQuotedStatus() != null ) {
            			users.add(status.getQuotedStatus().getUser());
            		}
            	}
//        		var filteredUsers = users.stream().filter(user->{
//        			var isThai = userUtils.isThai(user);
//        			return isThai.isEmpty() || isThai.get();
//        		}).collect(Collectors.toSet());

            	
            	taskResults.add(this.addUsersFunc.apply(users));
            	
            	try {
    				Thread.sleep(SEARCH_CALL_DELAY);
    			} catch (InterruptedException e) {
    				e.printStackTrace();
    				break;
    			}
            	if (!queryResult.hasNext() )
            		break;
            	else
            		query = queryResult.nextQuery();
            	
        	}
        	
		}
		int count = 0;
		for ( var taskResult: taskResults) {
			try {
				count += taskResult.get();
			} catch (InterruptedException | ExecutionException e) {
				e.printStackTrace();
			}
		}
		
		System.out.printf("Added %,d users\n", count);
		return count;
	}
}
