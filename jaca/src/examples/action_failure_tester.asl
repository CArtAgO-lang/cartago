!test_op_failure.

+!test_op_failure 
  <- println("test op failure");
     makeArtifact("a0","c4jexamples.ArtifactWithFailure");
     testFail [artifact_name("a0")];
     println("This should not be printed.").
     
-!test_op_failure [error_msg(Msg),fake_descr(X,Y)]    
  <- println("got it: ",Msg," ",X,Y).   