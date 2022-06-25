package db;

import java.util.concurrent.Callable;

public class UpdateUserStatusTask  implements Callable<Integer>{
	private final long id;
	private final int status;
	
	public UpdateUserStatusTask(long id, int status) {
		super();
		this.id = id;
		this.status = status;
	}



	@Override
	public Integer call() throws Exception {
		return UserDAO.updateUserStatus(id, status);
	}

}
