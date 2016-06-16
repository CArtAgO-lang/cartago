!send_info.

+!send_info : true
  <- makeArtifact("senderPort","c4jexamples.Port",[23000]);
     sendMsg("hello1","localhost:25000");
     sendMsg("hello2","localhost:25000").
  
