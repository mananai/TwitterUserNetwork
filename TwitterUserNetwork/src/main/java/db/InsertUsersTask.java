package db;

import java.util.Collection;
import java.util.concurrent.Callable;

import twitter4j.User;

public class InsertUsersTask implements Callable<Integer> {
	private final Collection<User> users;
	
	public InsertUsersTask(Collection<User> users) {
		super();
		this.users = users;
	}

	@Override
	public Integer call() throws Exception {
		return UserDAO.insertUsers(users);
	}

}
