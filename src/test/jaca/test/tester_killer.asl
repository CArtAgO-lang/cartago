!do_test.


+!do_test <-
	.println("going to kill in 1 sec...");
	.wait(500);
	.kill_agent(tester_killed);
	.println("killed.");
	//lookupArtifact("session_tester_killed", ArtId)
	//disposeArtifact(ArtId);
	.wait(500);
	.println("recreating...");
	.create_agent(tester_killed,"tester_killed.asl");
	makeArtifact(testc,"acme.Counter",[],CAid);
	focus(CAid);
	.println("alive again.");
	.send(tester_killed,achieve,focus_art(testc));
.
