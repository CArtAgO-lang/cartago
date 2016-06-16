!look_for_tasks("t0",1400).

+!look_for_tasks(Task,MaxBidMakingTime) 
	<-  +task_descr(Task);
	    +max_bid_time(MaxBidMakingTime);
	    focusWhenAvailable("task_board");
		println("task board located.").
		
	
+task(Task,CNPBoard) : task_descr(Task)
	<-  println("found a task: ",Task);
		lookupArtifact(CNPBoard,BoardId);
		focus(BoardId);
		!make_bid(Task,BoardId).
		
+winner(BidId) : my_bid(BidId)
	<-  println("awarded!.").

+winner(BidId) : my_bid(X) & not my_bid(BidId)
	<-  println("not awarded.").
	
+!make_bid(Task,BoardId)
	<-   !create_bid(Task,Bid);
	     bid(Bid,BidId)[artifact_id(BoardId)];
		 +my_bid(BidId);
		 println("bid submitted: ",Bid," - id: ",BidId).

-!make_bid(Task,BoardId)
	<- 	println("too late for submitting the bid.");
	    .drop_all_intentions.
	
+!create_bid(Task,Bid) 
	<-	?max_bid_time(Timeout); 
		Num = math.random(Timeout);
	    .wait(Num);
	    .my_name(Name);
		.concat("bid_",Name,Bid).
  		 
 