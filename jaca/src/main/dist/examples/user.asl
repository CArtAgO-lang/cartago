!create_and_use.

+!create_and_use : true
  <- makeArtifact("c0","c4jexamples.Counter",[],Id);
     inc ;
     inc [artifact_id(Id)].
     
+count(N)
  <- println("perceived new count value: ",N).

