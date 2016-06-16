!test_clock.

+!test_clock
  <- makeArtifact("myClock","c4jexamples.Clock",[],Id);
     focus(Id);
     +n_ticks(0);
     start;
     println("clock started.").

@plan1
+tick: n_ticks(30)  
  <- stop;
     println("clock stopped.").

@plan2 [atomic]
+tick: n_ticks(N)  
  <- -+n_ticks(N+1);
     println("tick perceived!").
      