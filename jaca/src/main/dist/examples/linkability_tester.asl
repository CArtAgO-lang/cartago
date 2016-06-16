!test_link.

+!test_link
  <-  makeArtifact("myArtifact","c4jexamples.LinkingArtifact",[],Id1);
      makeArtifact("count","c4jexamples.LinkableArtifact",[],Id2);
	  linkArtifacts(Id1,"out-1",Id2);			
	  println("artifacts linked: going to test");
	  test;
      test2(V);
      println("value ",V);
      test3.
