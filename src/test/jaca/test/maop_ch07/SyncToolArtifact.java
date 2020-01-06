package maop_ch07;

import cartago.*;

public class SyncToolArtifact extends Artifact  {
	
	private int nTotalFriends;
	private int nFriendsArrived;
	
	void init(int nFriends) {
		this.nTotalFriends = nFriends;
		this.nFriendsArrived = 0;
	}
	
	@OPERATION void meet() {
		nFriendsArrived++;
		this.await("allFriendsArrived");
	}
	
	@GUARD boolean allFriendsArrived() {
		return nFriendsArrived == nTotalFriends;
	}

	@OPERATION void reset() {
		this.nFriendsArrived = 0;
	}

}
