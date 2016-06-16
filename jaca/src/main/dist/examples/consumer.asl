!consume.

+!consume: true 
  <- ?bufferReady;
     !consumeItems.
    
+!consumeItems: true 
  <- get(Item);
     !consumeItem(Item);
     !!consumeItems.

+!consumeItem(Item) : true 
  <- .my_name(Me);
     println(Me,": ",Item).
  
+?bufferReady : true 
  <- lookupArtifact("myBuffer",_).  
-?bufferReady : true 
  <-.wait(50);
     ?bufferReady.
 