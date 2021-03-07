!do_test.


+!do_test <-	
	.println("going to kill in 1 sec...");
	.wait(500);
	.kill_agent(tester_killed);
	.println("killed.");
	lookupArtifact("session_tester_killed", ArtId)
	disposeArtifact(ArtId);
	.wait(500);
	.println("recreating...");
	.create_agent(tester_killed);
	.println("alive again.").
	
		

	