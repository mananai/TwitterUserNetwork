package twitter.friendnetwork;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import data.FriendsFollowersFutureData;
import db.FriendsFollowersSQLFutureData;
import twitter4j.Location;
import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.Trend;
import twitter4j.Trends;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.conf.ConfigurationBuilder;

public class CollectTwitterUsers {
	
	private static final int SEARCH_CALL_DELAY = 5*1000;
	private static final int MAX_TRENDS = 50;
	private static final int MAX_TWEETS_PER_SEARCH = 100;
	private static final int NUM_PAGES = 5;

	public static void main(String[] args) throws IOException, InterruptedException, TwitterException, SQLException  {
		if (args.length < 4 ) {
			throw new IllegalArgumentException();
		}
    	String jdbcURL = args[0];
		String locName = args[1];
		String lang = args[2];
		
    	String[] propertiesFiles = Arrays.copyOfRange(args, 3, args.length);
    	ConfigurationBuilder[] configs  = TwitterConfigFactory.create(propertiesFiles);

		FriendsFollowersFutureData dataAdapter = new FriendsFollowersSQLFutureData(jdbcURL);
		System.out.println("*** Initial users ***");
		new CollectTwitterUsers().run(dataAdapter, configs, MAX_TWEETS_PER_SEARCH, NUM_PAGES, locName, lang);
	}
	
	private void run(FriendsFollowersFutureData dataAdapter, ConfigurationBuilder[] configs , int maxTweets, int numPages, 
			String locName, String lang) throws InterruptedException, TwitterException {
		
		ExecutorService exec = Executors.newCachedThreadPool();
		BlockingQueue<Trend> trendsQueue = new LinkedBlockingQueue<>();

		List<Twitter> twitters = new ArrayList<>();
		for ( var config : configs) {
	        Twitter twitter = new TwitterFactory(config.build()).getInstance();		
	        twitters.add(twitter);
	    }

		List<Trend> trends = this.getTrends(twitters.get(0), locName);
		trendsQueue.addAll(trends);
		
		List<Future<Integer>> taskResults = new ArrayList<>();
		for ( var twitter : twitters) {
	        var searchTask = new SearchTweetCallable(twitter, maxTweets, numPages, lang, trendsQueue, 
	        		users->dataAdapter.insertUsers(users));
	        
	        taskResults.add(exec.submit(searchTask));
		}
		
		int count = 0;
		for ( var result: taskResults) {
			try {
				count += result.get();
			} catch (InterruptedException | ExecutionException e) {
				e.printStackTrace();
			}
		}
		exec.shutdown();
		exec.awaitTermination(30, TimeUnit.SECONDS);
		dataAdapter.close();
		System.out.printf("Added %,d users\n", count);
		
	}
	
	private List<Trend> getTrends(Twitter twitter, String locName) throws TwitterException{
        List<Location> locations = twitter.getAvailableTrends();

      
		Location location = locations.stream().
  		filter(l->l.getName().equals(locName)).findFirst().get();

		System.out.println("Found location: " +location);
      
      
		Trends trends;
		trends = twitter.getPlaceTrends(location.getWoeid());
		Trend[] trendsArray = trends.getTrends();
		System.out.printf("Number of trends=%d\n", trendsArray.length);
      

		List<Trend> trendList = Arrays.asList(trendsArray);
		trendList.stream().forEach(trend->{
			System.out.println(String.format("%s (tweet_volume: %d)", trend.getName(), trend.getTweetVolume()));
		});
		List<Trend> shortenedTrends = trendList.stream().filter(t -> t.getTweetVolume() > 0)
				.collect(Collectors.toList());
		return shortenedTrends;
	}
	
	@Deprecated
	public Set<User> run(String locName, String lang, int maxTweets) {
		try {
            
            Twitter twitter = new TwitterFactory().getInstance();

            List<Location> locations = twitter.getAvailableTrends();

            
			Location location = locations.stream().
        		filter(l->l.getName().equals(locName)).findFirst().get();

            System.out.println("Found location: " +location);
            
            
            Trends trends;
            trends = twitter.getPlaceTrends(location.getWoeid());
            Trend[] trendsArray = trends.getTrends();
            System.out.printf("Number of trends=%d\n", trendsArray.length);

            List<Trend> trendList = Arrays.asList(trendsArray);
            List<Trend> shortenedTrends = trendList.stream().
            								filter(t->t.getTweetVolume()>0).
            								collect(Collectors.toList());
            Collections.sort(shortenedTrends, (t1, t2)-> t2.getTweetVolume() - t1.getTweetVolume());
            System.out.printf("After filtered promoted trends=%d\n", shortenedTrends.size());
            System.out.printf("=== Showing trends for %s ===\n", location.getName());
            shortenedTrends.stream().forEach(t->
            		System.out.printf("%s(%,d)\n", t.getName(), t.getTweetVolume()));
            if (shortenedTrends.size() > MAX_TRENDS)
            	shortenedTrends = shortenedTrends.subList(0, MAX_TRENDS);

            Set<User> users = new TreeSet<>((a, b)->b.getFollowersCount() - a.getFollowersCount());
            for ( Trend trend: shortenedTrends) {
            	Query query = new Query(trend.getQuery());
            	query.setCount(maxTweets);
            	if ( lang != null)
            		query.setLang(lang);
            	QueryResult result = twitter.search(query);
            	for (var status : result.getTweets()) {
            		users.add(status.getUser());
            		if (status.isRetweet()) {
            			users.add(status.getRetweetedStatus().getUser());
            		}
            		if ( status.getQuotedStatus() != null ) {
            			users.add(status.getQuotedStatus().getUser());
            		}
            	}
            	System.out.print(".");
            	try {
					Thread.sleep(SEARCH_CALL_DELAY);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
            }
            return users;
        } catch (TwitterException te) {
            te.printStackTrace();
            System.err.println("Twitter exception: " + te.getMessage());
            System.exit(-1);
        } 
		return null;
	}
}
