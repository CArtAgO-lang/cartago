// userB agent

!do_test.

+!do_test 
  <- !discover("a0");
     !use_it(10).     
     
+!use_it(NTimes) : NTimes > 0 
  <- update(3);
     println("[userB] updated.");
     !use_it(NTimes - 1).
          
+!use_it(0) 
  <- println("[userB] completed.").
     
+!discover(ArtName)
  <- lookupArtifact(ArtName,_).
-!discover(ArtName)
  <- .wait(10);
     !discover(ArtName).
     
     
     
              