package twitter.func;

import twitter4j.TwitterException;

@FunctionalInterface
public interface TwitterBiFunction<T, U, R> {
	R apply(T t, U u) throws TwitterException;
}
