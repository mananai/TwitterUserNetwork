package db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import entities.UserImp;
import twitter.friendnetwork.ProcessStatus;
import twitter.tweet.TweetConsts;
import twitter4j.User;

public class UserDAO {
	
	public static final Long NO_FRIEND = Long.valueOf(-1);
	
	static User getNextPendingUser() throws SQLException {
		Connection connection = DBResources.getInstance().getConnection();
		ResultSet rs = DBResources.getInstance().executeQuery(
				"select id, name, screen_name, description, "+
				"favorites_count, followers_count, friends_count, statuses_count, "+ 
				"lang, location, url, is_protected, is_verified " +
				"from user where status=? order by rowid limit 1", ProcessStatus.Pending.value);
		
		User user = null;
		if (rs.next()) {
			user = new UserImp(rs.getLong(1), rs.getString(2), rs.getString(3), rs.getString(4),
					rs.getInt(5), rs.getInt(6), rs.getInt(7), rs.getInt(8),
					rs.getString(9), rs.getString(10), rs.getString(11), rs.getInt(12)==1, rs.getInt(13)==1);

			DBResources.getInstance().executeUpdate("update user set status=? where id=?", ProcessStatus.Processing.value, user.getId());
			connection.commit();
		}
		rs.close();
		return user;

		
	}

	
	static int updateUserStatus(long id, int status) throws SQLException {
		Connection connection = DBResources.getInstance().getConnection();
		int count =  DBResources.getInstance().executeUpdate("update user set status=? where id=?", status, id);
		connection.commit();
		return count;
	}

	static int insertUserFriends(long userId, long[] friendIds) throws SQLException {
		
		Connection connection = DBResources.getInstance().getConnection();
		PreparedStatement pstmt = connection.prepareStatement(//DBResources.getInstance().prepareStatement(
					"insert or ignore into user_friend(user_id, friend_id, last_modified) "+ 
					"values(?, ?, datetime('now','localtime') )");
		
		for (long friendId: friendIds) {
			
			friendId = (friendId != NO_FRIEND )? friendId : null;
			
			DBResources.getInstance().setStatementParameters(pstmt, userId, friendId);
			pstmt.addBatch();
		}
		int[] result =pstmt.executeBatch();
		
		
		connection.commit();
		pstmt.close();
		return Arrays.stream(result).sum();
	}	
	
	static int insertUsers( Collection<User> users) throws SQLException {
		
		Connection connection = DBResources.getInstance().getConnection();
		
		PreparedStatement pstmt = connection.prepareStatement(//DBResources.getInstance().prepareStatement(
				"insert or ignore into user(id, name, screen_name, description, email, " +
						"favorites_count, followers_count, friends_count, statuses_count, lang, location, " +
						"url, imageurl, "+
						"is_protected, is_verified, created, last_modified) " +
						"values( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? ) ");
		
		for (User user: users) {
			
			DBResources.getInstance().setStatementParameters(pstmt, 
					user.getId(),
					user.getName(), 
					user.getScreenName(),
					user.getDescription(),
					user.getEmail(), 
					user.getFavouritesCount(),
					user.getFollowersCount(), 
					user.getFriendsCount(),
					user.getStatusesCount(),
					user.getLang(),
					user.getLocation(),
					null, //user.getURL(),
					null, //user.get400x400ProfileImageURL(),
					user.isProtected() ?  1: 0,
					user.isVerified() ? 1 : 0,
					TweetConsts.dfISO8601.format(user.getCreatedAt()),
					TweetConsts.dfISO8601.format(new Date()));
			pstmt.addBatch();
		}
			
		int[] result = pstmt.executeBatch();
		connection.commit();
		pstmt.close();
		return Arrays.stream(result).sum();
	}


	static long countUsers() throws SQLException{
		ResultSet rs = DBResources.getInstance().executeQuery("select count(*) from user");
		if (rs.next()) {
			return rs.getLong(1);
		}
		return -1;
	}

	static long countUserFriends() throws SQLException{
		ResultSet rs = DBResources.getInstance().executeQuery("select count(*) from user_friend");
		if (rs.next()) {
			return rs.getLong(1);
		}
		return -1;
	}

	
	static List<Long> getUserIds() throws SQLException{
		List<Long> list = new ArrayList<>();
		ResultSet rs = DBResources.getInstance().executeQuery("select id from user");
		while(rs.next()) {
			list.add(rs.getLong(1));
		}
		return list;
	}

	
	
	
	static boolean friendsExist(long userId) throws SQLException {
		ResultSet rs = DBResources.getInstance().executeQuery(
					"select count(*) from user_friend where user_id=?", userId);
		while(rs.next()) {
			return rs.getInt(1)>0;
		}
		return false;
	}
	
	public static User getUserById(long userId) throws SQLException {
		ResultSet rs = DBResources.getInstance().executeQuery(
				"select id, name, screen_name, description, "+
				"favorites_count, followers_count, friends_count, statuses_count, "+ 
				"lang, location, url, is_protected, is_verified " +
				"from user where id=?", userId);

		while(rs.next()) {
			return new UserImp(rs.getLong(1), rs.getString(2), rs.getString(3), rs.getString(4),
					rs.getInt(5), rs.getInt(6), rs.getInt(7), rs.getInt(8),
					rs.getString(9), rs.getString(10), rs.getString(11), rs.getInt(12)==1, rs.getInt(13)==1);
		}
		return null;
		
	}
	
	static List<Long> getUserIds(String sql) throws SQLException{
		ResultSet resultSet = DBResources.getInstance().executeQuery(sql);
		List<Long> ids = new ArrayList<>();
		while(resultSet.next()) {
			ids.add(resultSet.getLong(1));
		}
		return ids;
	}

	
	
	static List<Long> getUserIdsToBeProcessed() throws SQLException{
		List<Long> list = new ArrayList<>();
		ResultSet rs = DBResources.getInstance().executeQuery(
				"select id\n" + 
				"from user\n" + 
				"where id not in (select user_id from user_friend) \n" + 
				"order by rowid\n" + 
				"limit 10;\n" );
		while(rs.next()) {
			list.add(rs.getLong(1));
		}
		return list;
	}
	
	static List<Long> getUserIdsFromSampleTweets() throws SQLException{
		List<Long> list = new ArrayList<>();
		ResultSet rs = DBResources.getInstance().executeQuery(
				"select user_id\n" + 
				"from sample_tweet\n" + 
				"order by random()\n" + 
				"limit 300;"  );
		
		while(rs.next()) {
			list.add(rs.getLong(1));
		}
		return list;
	}
}
