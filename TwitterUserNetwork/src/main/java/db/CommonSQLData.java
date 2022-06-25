package db;

import java.sql.SQLException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

class CommonSQLData {
	private ExecutorService execSvc;
	
	public CommonSQLData(String jdbcURL) {
    	DBResources.newInstance(jdbcURL);
		try {
			DBResources.getInstance().getConnection().setAutoCommit(false);
		} catch (SQLException e1) {
			e1.printStackTrace();
			throw new RuntimeException(e1);
		}
	}
	
	ExecutorService getExecutorService() {
		if (execSvc == null) {
			execSvc = Executors.newSingleThreadExecutor(); //Executors.newFixedThreadPool(4);
		}
		return execSvc;
	}
	
	public void close() {
		if (execSvc!=null) {
			execSvc.shutdown();
			try {
				execSvc.awaitTermination(60, TimeUnit.SECONDS);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		DBResources.getInstance().close();
		System.out.println("DB Data Store cleanup work is done");
	}
}
