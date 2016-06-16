!allocate_task("t0",1000).

+!allocate_task(Task,Timeout)
	<- makeArtifact("task_board","c4jexamples.TaskBoard",[]);
	   println("taskboard allocated");
	   announce(Task,Timeout,CNPBoardName);
	   println("announced: ",Task," on ",CNPBoardName);
	   getBids(Bids) [artifact_name(CNPBoardName)];
	   println("got bids (",.length(Bids),")");
	   !select_bid(Bids,Bid);
	   println("selected: ",Bid);
	   award(Bid)[artifact_name(CNPBoardName)];
	   println("awarded: ",Bid);
	   clear(Task).
	   
+!select_bid([Bid|_],Bid).
	   
	   
	
 