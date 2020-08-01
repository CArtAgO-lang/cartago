!test_transact.

+!test_transact <- 
  println("test transactionality");
  !setup;
  update1;
  update2;
  !try_update3;
  update4.

+!setup <-
  makeArtifact("myArt1","acme.TestTransactArtifact",[],Id);
  focus(Id).
  
-!setup <-
  lookupArtifact("myArt1",Id);
  focus(Id).

+!try_update3 <-
  update3.
  
-!try_update3 <-
  println("recovered.");
  update1.  
  
+first_step_done
  <- .my_name(Name);
     println(Name,": first_step_done").
  
+a(X)
  <- .my_name(Name);
     println(Name,": a changed: ",X).
  
+b(X)
  <- .my_name(Name);
     println(Name,":b changed: ",X).
  
