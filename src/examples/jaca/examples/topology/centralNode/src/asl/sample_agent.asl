

/* Plans */

!start.

+!start : true <-
  println("Starting at node 1");
  lookupArtifact("node", NID);
  focus(NID). //necessary to gain access to topology events and beliefs  
  

+created_workspace(main.test1) : true <- //workspace comes from a new CArtAgO node
  joinWorkspace(main.test1, WId); //node2 
  createWorkspace(main.test2);  //spawned on node1
  createWorkspace(main.test1.test1);  //spawned on node2
  println("on second node")[wsp(main.test1)]; //excecuted at home if not specified
  joinWorkspace("main.test2", WId2); //node1
  println("on first node");
  createWorkspace("main.test1.wx"); //spawned on node 2
  joinWorkspace("main.test1.wx", WId3); //node2
  println("on second node again")[wsp(main.test1.wx)];
  joinWorkspace(main.test1.test1, WId4);
  quitWorkspace(main.test1).


+created_workspace(WSP) : true <-
  println("created ", WSP).
