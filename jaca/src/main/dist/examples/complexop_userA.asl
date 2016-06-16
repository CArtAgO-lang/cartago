// userA agent

!do_test.

+!do_test     
  <- println("[userA] creating the artifact...");
     makeArtifact("a0","c4jexamples.ArtifactWithComplexOp",[],Id);
     focus(Id);
     println("[userA] executing the action...");
     complexOp(10);
     println("[userA] action completed."). 

+step1_completed
  <- println("[userA] first step completed.").

+step2_completed(C)
  <- println("[userA] second step completed: ",C).

               