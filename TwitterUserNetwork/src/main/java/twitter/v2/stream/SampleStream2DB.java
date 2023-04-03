package twitter.v2.stream;

//Import classes:
import static java.lang.String.format;

//import com.twitter.clientlib.ApiException;
//import com.twitter.clientlib.JSON;
import com.twitter.clientlib.model.*;

//import okhttp3.internal.http2.StreamResetException;

//import com.twitter.clientlib.TwitterCredentialsBearer;
//import com.twitter.clientlib.api.TwitterApi;

//import java.io.InputStream;
//import com.google.common.reflect.TypeToken;
//import java.io.BufferedReader;
//import java.io.IOException;
//import java.io.InputStreamReader;
//import java.lang.reflect.Type;
//import java.net.SocketTimeoutException;
import java.sql.SQLException;
//import java.util.List;
//import java.util.Set;
import java.util.concurrent.BlockingQueue;
//import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Predicate;
import java.util.logging.Logger;

//import javax.net.ssl.SSLException;

import java.util.Arrays;

public class SampleStream2DB {
	private static final int THREAD_POOL_SIZE = 2;

	static Logger logger = Logger.getLogger(SampleStream2DB.class.getName());

	public static void main(String[] args) throws SQLException {
		if (args.length < 3)
			throw new IllegalArgumentException();

		final String jdbcURL = args[0];
		final String lang = args[1];
		final String[] bearerTokenArray = Arrays.copyOfRange(args, 2, args.length);

		logger.info(format("Database url: %s", jdbcURL));
		logger.info(format("Lang: %s", lang));
		logger.info(format("PID: %d", ProcessHandle.current().pid()));
		logger.info(format("Use \"kill\" command to terminate the program"));

		final BlockingQueue<Tweet[]> tweetsQueue = new LinkedBlockingQueue<>();
		final AtomicBoolean isTerminated = new AtomicBoolean(false);
		final Predicate<Tweet> predicate = tweet->lang.equals(tweet.getLang());

		TwitterDBWriter dbWriterTask = new TwitterDBWriter(jdbcURL, tweetsQueue);
		SampleStreamCollector streamCollector = new SampleStreamCollector(predicate, /*lang,*/ bearerTokenArray, tweetsQueue, isTerminated);
		
		ExecutorService executorService = Executors.newFixedThreadPool(THREAD_POOL_SIZE);

		Future<Integer> streamFuture = executorService.submit(streamCollector);
		Future<Integer> dbFuture = executorService.submit(dbWriterTask);
		
		Runtime.getRuntime().addShutdownHook(new Thread() {
			
			private Logger logger = Logger.getLogger(this.getClass().getName());

			@Override
			public void run() {
				logger.warning("!!! Got a request to terminate the program !!!");
				isTerminated.set(true);
				
				executorService.shutdown();
				try {
				    if (!executorService.awaitTermination(30, TimeUnit.SECONDS)) {
				        executorService.shutdownNow();
				    } 
				} catch (InterruptedException e) {
				    executorService.shutdownNow();
				}
				System.out.println("Shutdown process is done");
			}
		});
	}
}