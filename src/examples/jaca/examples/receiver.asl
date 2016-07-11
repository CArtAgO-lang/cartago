!receive_msgs.

+!receive_msgs : true
  <- makeArtifact("receiverPort","c4jexamples.Port",[25000],Id);
     receiveMsg(Msg,Sender);
     println("received ",Msg," from ",Sender);
     focus(Id);
     startReceiving.

     
+new_msg(Msg,Sender)
  <- println("received ",Msg," from ",Sender).
     