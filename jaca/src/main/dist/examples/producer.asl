item_to_produce(0).

!produce.

+!produce: true <- 
  !setupTools(Buffer);
  !produceItems.
	  
+!produceItems : true <- 
  ?nextItemToProduce(Item);
  put(Item);
  !!produceItems.

+?nextItemToProduce(N) : true 
 <- -item_to_produce(N);
    +item_to_produce(N+1).

+!setupTools(Buffer) : true <- 	
  makeArtifact("myBuffer","c4jexamples.BoundedBuffer",[10],Buffer).

-!setupTools(Buffer) : true <- 	
  lookupArtifact("myBuffer",Buffer).

