!test.

+!g 
	<-  !!g1;
		.wait(done);
		.println("done").

+!g1 
	<- 	.wait(5000);
		+done.

+!g2 
	<-  !!g3;
		.wait(1000);
		.wait({+done});
		.println("done").

+!g3 
	<- 	+done.
				
+!test 
	<- !!g4;
	+index(0);
	while (index(I)){
		.println(I);	
		-+index(I+1)
	}.			

+!g4
	<- 	.wait(1000);
		.suspend(test);
		.println("stopped").	