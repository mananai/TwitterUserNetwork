package db;

import java.util.concurrent.Callable;

public class InsertUserFriendsTask implements Callable<Integer> {

	private final long userId;
	private final long[] friendIds;
	
	public InsertUserFriendsTask(long userId, long[] friendIds) {
		super();
		this.userId = userId;
		this.friendIds = friendIds;
	}

	@Override
	public Integer call() throws Exception {
		return UserDAO.insertUserFriends(userId, friendIds);
	}

}
