!test_double_join.
//!main_test.
//!test_focus_with_filter.
//!test_make_lookup_dispose.
//!test_wsp.
//!test2.
//!test3.
//!test_gui.
//!test_gui2.
//!test_multi_prop.
//!test_new_prop.
//!test_varargs.
//!test_java_api.
//!test_remote.
//!test_shutdown.
//!test_wsp.
//!test_art.
//!test_obs_multiple_artifacts_same_type.
//!test_op_fail.
//!test_wspruleman.
//!test_array_obj.
//!test_ext_interface.

/* test double join */

+!test_double_join 
  <- createWorkspace("w0");
     println("joining...");
     joinWorkspace("w0",WspID);
     joinWorkspace("w0",WspID2)[wsp("main")].
     
-!test_double_join [error_msg(Msg)] 
  <- println("OK, got error");
  	 println("Message: ",Msg).
  
     
/* testing artifact with a direct external interface */

+!test_ext_interface
	<- makeArtifact("myArt","c4jtest.ArtifactWithExtUse",[],Id);
		+count(0);
	   focus(Id).

+a(X) : count(Y) & Y > 10
	<- reset;
		-+count(0).
	
+a(X) 
	<- println(X);
	   ?count(Y);
	   -+count(Y+1).

/* ------- */
	  

+!test_array_obj 
  <-  makeArtifact("myArt","test.ArtifactWithArray",[],Id);
      getCurrentArtifacts(L);
	  println(L);
	  myOp(L).
	  
  
+!main_test
  <-  !test_console;
      !test_make_lookup_dispose;
      !test_use;
      !test_focus; 
      !test_link;
      !test_op_fail.      
    
+!test_console <-
  println("this print is done by the console artifact").

  
+!test_make_lookup_dispose <-
  lookupArtifact("workspace",W);
  focus(W);
  makeArtifact("my_counter","c4jtest.Counter");
  lookupArtifact("my_counter",Id);
  println("artifact created ",Id);
  makeArtifact("my_counter2","c4jtest.Counter",[],Id2);
  println("artifact2 created ",Id2);
  stopFocus(Id2);
  disposeArtifact(Id2);
  println("artifact2 disposed.").

+artifact(Name,Template,Id)
  <- println("new artifact available: ",Name).

-artifact(Name,Template,Id)
  <- println("Artifact removed: ",Name).
  
  
+!test_use <-
  inc;
  println("op inc executed.");
  inc [art("my_counter")];
  println("op inc executed specifying the artifact name.");
  lookupArtifact("my_counter",Id);
  inc [aid(Id)];
  println("op inc executed specifying the artifact Id").  
  
+!test_focus <-
  lookupArtifact("my_counter",Id);
  focus(Id);
  println("focus executed specifying the art name.");
  inc;
  inc;
  stopFocus(Id);
  println("stop focus executed specifying the art name.");
  inc;
  inc;
  focus(Id);
  println("focus executed specifying the artifact unique ID.").
  
+count(V)[art(Id,"my_counter")]
  <- println("count value: ",V," artifact: ",Id).
  
+incremented [percept_type("obs_ev"),art(_,Name)]
  <- println("new incremented event from ",Name).

+focused_art(Wsp,ArtName,ArtId)	
	<- println("Focused: ",ArtName," (id: ",ArtId," ) in ",Wsp).
	 

+!test_focus_with_filter 
  <- makeArtifact("my_counter","c4jtest.Counter",[],Id);
     cartago.new_array("java.lang.String[]",["incremented"],Array);
     cartago.new_obj("cartago.events.SignalFilter",[Array],Filter);
     focus(Id,Filter);
     inc;
     inc.
     
//

+!test_link
  <-  makeArtifact("myArtifact","c4jtest.LinkingArtifact",[],Id1);
      makeArtifact("count","c4jtest.LinkableArtifact",[],Id2);
	  linkArtifacts(Id1,"out-1",Id2);			
	  println("artifacts linked: going to test");
	  test;
      test2(V);
      println("value ",V);
	  println("testing link with multiple artifacts..");
	  makeArtifact("count3","c4jtest.LinkableArtifact",[],Id3);
	  linkArtifacts(Id1,"out-1",Id3);			
	  test;
      println("the test op should have been called on two count artifacts...").	  
	  
      //test3;
    //lookupArtifact("test",Id3);
    //println("test exists! ",Id3).
    //observe_property(Id3,aprop(X));
    //println("obs prop value: ",X).
 
+!test_op_fail 
  <- println("test op failure");
     makeArtifact("a0","c4jtest.MyArtifactA");
     testFail [art("a0")];
     println("This should not be printed.").
-!test_op_fail [error_msg(Msg),env_failure_reason(reason("test",Y))]    
  <- println("got it: ",Msg," ",Y).    

    
//

+!test2
  <- makeArtifact("my_clock","cartago.tools.Clock",[],Id);
     focus(Id);
     setFrequency(10);
     println("starting the clock...");
     start;
     .wait(1000);
     println("stopping the clock...");
     stop.
     
+tick [art(_,"my_clock")]
  <-  println("TICK!").
  
//  

+!test3
  <-  makeArtifact("a0","c4jtest.MyArtifactA",[],Id);
      compute(4,X,Y);
      println(X);
      println(Y).

  
//

+!test_gui
  <-  makeArtifact("gui","c4jtest.MySimpleGUI",[],Id);
      focus(Id).

+value(V) 
  <- println("Value updated: ",V).
  
+ok : value(V)
  <-  setValue(V+1).

+closed
  <-  .my_name(Me);
      .kill_agent(Me).
      
//

+!test_gui2
  <-  makeArtifact("gui","c4jtest.MySimpleGUI",[],Id);
      cartago.new_array("java.lang.String[]",["java"],Ext);
	  selectFileToOpen("/Users","Java files",Ext,FileName);
	  println(FileName).


//

+!test_new_prop
  <- makeArtifact("a0","c4jtest.MyArtifactB",[],Id);
     focus(Id);
     addNewProp("myprop",13).

+myprop(V)
  <- println("here it is the new prop! value: ",V).

//

+!test_multi_prop
  <- makeArtifact("a0","c4jtest.MyArtifactB",[],Id);
     focus(Id);
     update(0,"new_text");
     update(1,500).

+complex_prop(X,Y)
  <- println("prop with multiple values: ",X," ",Y).

//

+!test_varargs
  <- out("p",1,2);
     in("p",X,2);
     println("value: ",X).
     

+!test_java_api
  <- cartago.new_obj("c4jtest.FlatCountObject",[10],Id);
     cartago.invoke_obj(Id,inc);
     cartago.invoke_obj(Id,getValue,Res);
     println(Res);
     cartago.invoke_obj("java.lang.System",currentTimeMillis,T);
     println(T);
     cartago.invoke_obj("java.lang.Class",forName("c4jtest.FlatCountObject"),Class);
     println(Class).
  
+!test_remote <-
  println("testing remote..");
  joinWorkspace("default@localhost");
  println("hello, remote world!");
  !use_remote.
  
+!use_remote <-
  makeArtifact("c0","basic.Counter",Id);
  focus(Id);
  inc;
  inc.

-!use_remote [action_failed(make_artifact(_,_,_),artifact_already_present)] <-
  println("artifact already created ");
  focus("c0");
  inc.
  
+count(V)[art(Id,"c0")]
  <- println("perceived from remote counter: ",V).

  
+!test_wsp 
  <- createWorkspace("w0");
     println("joining...");
     joinWorkspace("w0",WspID);
     println("making artifact...");
     makeArtifact("a0","c4jtest.MyArtifactA",[],Id2);
     println("looking up...");
     lookupArtifact("a0",Id3);
     println("hello",Id3);
     createWorkspace("w1");
     println("joining...");
     joinWorkspace("w1",WspID2);
     compute(5,X,Y);
     println(X).

+joined_wsp(Name,Id)	
	<- println("joined wsp: ",Name," ",Id).          
          
-!test_wsp [error_msg(Msg)]
  <- println("OOOPS failed: ",Msg).
  
+!test_shutdown
  <- println("testing shutdown");
     shutdownNode;
     println("should be down now..").

-!test_shutdown [error_msg(Msg)]
  <- .println("no more environments.. ",Msg).
     
+!test_art <-
    makeArtifact("c0","c4jtest.Counter",[],Id1);
    makeArtifact("c1","c4jtest.Counter",[],Id2);
    makeArtifact("c2","c4jtest.Counter",[],Id3);
    inc [art("c0")];
    inc [art("c1")].
      
+!test_obs_multiple_artifacts_same_type <-
    makeArtifact("c0","c4jtest.Counter",[],Id1);
    makeArtifact("c1","c4jtest.Counter",[],Id2);
    makeArtifact("c2","c4jtest.Counter",[],Id3);
    focus(Id1);
    focus(Id2);
    focus(Id3);
    inc [aid(Id1)];
    inc [aid(Id2)];
    inc [aid(Id3)].
  
// test wsp rule manager

+!test_wspruleman 
	<-	cartago.new_obj("c4jtest.MyWSPRuleEngine",[],Id);
		setWSPRuleEngine(Id);
		println("wsp rule manager set.");
		makeArtifact("c0","c4jtest.Counter",[],_);
		inc;
		inc;
		inc;
		lookupArtifact("c1",Id2);
		focus(Id2);
		?count(Value);
		println(Value);
		inc [aid(Id2)].

-!test_wspruleman [error_msg(M)]
	<- 	println(M).	
  
  
     
// not handled events      

+Ev [source(s)] : true <-
  .print(Ev," from ",S).
