!test_react.

+!test_react
  <-  makeArtifact("gui","test.ReactivityTestGUI",[],Id);
      focus(Id);
      +count(1);
      !work.

+!work
	<- -count(N);
	   +count(N+1);
	   !work.
	   
@plan[atomic]	   
+pressed : count(N)
  <-  println(N).

  
     
// not handled events      

+Ev [source(s)] : true <-
  .print(Ev," from ",S).
