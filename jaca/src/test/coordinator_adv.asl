!test.

+!test
  <- makeArtifact("a0","c4jtest.CommonArtifact");
     in("ready",From1);
     println(From1," is ready");
     .send(From1,achieve,go("a1"));
     in("ready",From2);
     println(From2," is ready");
     .send(From2,achieve,go("a2")).
     
    
     
      
      
