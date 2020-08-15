/*
 * Testing wsp model.
 * 
 * Note: no remote cases - for that see the specific acme.
 * 
 */

// !test_new_wsp_model.  // OK
// !test_wsp_multiple_intentions. // OK
// !test_wsp_classic. 	// OK
!test_implicit_action2.

+!test_new_wsp_model
  <- lookupArtifact("workspace", WspArtId);
	 focus(WspArtId);
  	 createWorkspace("w0");
     println("created w0");
	 createWorkspace("w1");
	 println("created w1");
	 println("joining w0...");
	 joinWorkspace("w0",W0);
	 println("joined w0");
	 println("joining w1...");
	 joinWorkspace("/main/w1",W1);
	 println("joined w1");
	 lookupArtifact("workspace", IdWspArt);
	 focus(IdWspArt);
	 println("start observing the workspace artifact of w1");
     println("making a Counter artifact called my_counter (in current wsp)... ");
  	 makeArtifact("my_counter","acme.Counter", [], Id1);
     println("make succeeded - id: ", Id1);
	 println("looking up an artifact (my_counter)  using just the name (i.e. in current wsp)")
     lookupArtifact("my_counter", Id2);
     println("lookup succeeded - id:  ", Id2)
	 println("looking up an artifact using abs path: /main/w1/my_counter")
     lookupArtifact("/main/w1/my_counter",Id3);
     println("lookup succeeded -  id: ", Id3);
     focus(Id3);
     println("making a new Counter artifact in w0 as /main/w0/my_counter2")
     makeArtifact("/main/w0/my_counter2","acme.Counter", [], Id4);
     println("make succeed - id: ", Id4);
     println("joining again w0 using a relative path - this is useful just to set the current wsp...")
     joinWorkspace("../w0",Id5);  	
     println("joined succeeded - now the current wsp is ", Id5);
     println("now looking up an artifact with relative path: ../w1/my_counter ")
     lookupArtifact("../w1/my_counter",Id6);
     println("lookup succeded - id: ", Id6);
	 println("now trying to lookup an artifact which is not in last wsp joined (w0) ");
 	 !test_fail;
     println("now quitting from w0");
     quitWorkspace(Id5);
     println("quit succeeded.").
     
+joinedWsp(WspId, Name, FullName) 
	<- println("joined new wsp: ", Name, " full name: ", FullName).
	
+focusing(ArtId, ArtName, ArtType, WspId, WspName, WspFullName)
	<- println("start focusing: ", ArtName, " of type ", ArtType, " in wsp ", WspId, " name: ", WspName, " full name: ", WspFullName).
		
+childWsp(WspName)
	<- println("new wsp created ", WspName).

+linkedWsp(LinkName, WspPath)
	<- println("new wsp linked ", LinkName, " ", WspPath).

+artifact(Id,Name,Type)[workspace(WspFullName, WspId)]
	<- println("new artifact available: ", Id, " name: ", " type: ", Type, " in wsp: ", WspFullName).
	

+!test_fail 
	<-  lookupArtifact("my_counter", X).
-!test_fail	
	<-  println("ok intercepted failure").
	
          
-!test_wsp [error_msg(Msg)]
  <- println("OOOPS failed: ",Msg).
 
+!test_wsp_classic 
  <- createWorkspace("w0");
     println("joining...");
     joinWorkspace("w0",WspID);
     println("making artifact...");
     makeArtifact("a0","acme.MyArtifactA",[],Id2);
     println("looking up...");
     lookupArtifact("a0",Id3);
     println("hello ",Id3);
     createWorkspace("w1");
     println("joining w1...");
     joinWorkspace("w1",WspID2);
     println("computing...");
     compute(5,X,Y)[wsp("w0")];
     println(X).

+joined_wsp(Name,Id)	
	<- println("joined wsp: ",Name," ",Id).          
          
-!test_wsp_classic [error_msg(Msg)]
  <- println("OOOPS failed: ",Msg).
  
// test working in multiple wsps with multiple intentions

+!test_wsp_multiple_intentions
	<-  joinWorkspace("/main");
		println("testing working in multiple wsps with different intentions")
		createWorkspace("w0");
		createWorkspace("w1");
	   !!firstGoal;
	   !!secondGoal.
		
+!firstGoal
	<-	println("first goal, creating and joining wsp w0..");
	    joinWorkspace("/main/w0", Wid);
		println("joined w0 from first intention");
		makeArtifact("counter", "acme.Counter",[], Id);
		focus(Id);
		inc.

+!secondGoal
	<-	println("second goal, creating and joining wsp w1..");
	    joinWorkspace("/main/w1", Wid);
		println("joined w1 from second intention");
		makeArtifact("counter", "acme.Counter",[],Id);
		focus(Id);
		inc.
			
+count(X)
	<- println("count: ", X).	  
  
// test implicit actions executed on artifact out of the current wsp

+!test_implicit_action2
	<-	println("creating a counter in the home wsp");
	    makeArtifact("c3", "acme.Counter", [], Id);
        println("creating a new wsp w0");
	    createWorkspace("w0");
	    joinWorkspace("w0",Wid);
	    println("now current wsp is w0");
	    println("executing an implicit action not found in the current wsp");
	    inc;
	    println("done");
	    makeArtifact("c4","acme.MyArtifactA",[],Id2);
	    println("created a new artifact in the new wsp w0");
	    joinWorkspace("/main",_);
	    println("now the current wsp is /main");
	    compute(5,X,Y);
	    println("executed an implicit action on the artifact in w0.. ", X, " ", Y);
		!test_wrong_action.
		    
+!test_wrong_action
	<- println("now executing an action which is not provided by any artifact..");
	   pop.

-!test_wrong_action
	<- println("action failure handled.").	
	    
+count(X)[artifact_name(c3)]
	<- println("count: ", c3).
       
// not handled events      

+Ev [source(s)] : true <-
  .print(Ev," from ",S).
