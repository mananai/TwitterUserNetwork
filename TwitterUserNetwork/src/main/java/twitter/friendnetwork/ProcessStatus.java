package twitter.friendnetwork;

public enum ProcessStatus {
	ToManyRelationships(-5), Foriegn(-4), Skipped(-3), Error(-2), NA(-1), Pending(0), Processing(1), Done(2) ;
	
	public final int value;
	
	ProcessStatus(int value) {
		this.value = value;
	}
}
