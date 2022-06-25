package twitter.func;

import twitter4j.TwitterException;

@FunctionalInterface
public interface TwitterFunction<T, R> {
	R apply(T t) throws TwitterException;
}
