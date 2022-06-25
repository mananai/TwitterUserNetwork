package db;

import java.sql.SQLException;
import java.util.Collection;
import java.util.concurrent.Future;

import data.FriendsFollowersFutureData;
import twitter4j.User;

public class FriendsFollowersSQLFutureData extends CommonSQLData implements FriendsFollowersFutureData {
	public  FriendsFollowersSQLFutureData(String jdbcURL) throws SQLException {
		super(jdbcURL);
		
		long userCount = UserDAO.countUsers();
		System.out.printf("Existing %,d users\n", userCount);
		long userFriendCount =UserDAO.countUserFriends();
		System.out.printf("Existing %,d user-friend relationships\n", userFriendCount);
	}
	
	@Override
	public Future<Integer> insertUserFriends(long userId, long[] friendIds) {
		return this.getExecutorService().submit(new InsertUserFriendsTask(userId, friendIds));
	}

	@Override
	public Future<Integer> insertUsers(Collection<User> users) {
		return this.getExecutorService().submit(new InsertUsersTask(users));
	}

	@Override
	public Future<Integer> insertUserFollowers(long userId, long[] ids) {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public Future<User> getNextPendingUser() {
		return this.getExecutorService().submit(()->UserDAO.getNextPendingUser());
	}

	@Override
	public Future<Integer> updateUserStatus(long id, int status) {
		return this.getExecutorService().submit(new UpdateUserStatusTask(id, status));
	}

}
