package data;

import java.util.Collection;
import java.util.concurrent.Future;

import twitter4j.User;

public interface FriendsFollowersFutureData{
	//For friends or followers processing
	Future<Integer> insertUserFriends(long userId, long[] ids) ;
	Future<Integer> insertUserFollowers(long userId, long[] ids);
	Future<Integer> insertUsers( Collection<User> users) ;
	Future<User> getNextPendingUser();
	Future<Integer> updateUserStatus(long id, int status);
	void close();
}
