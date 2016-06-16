philo(0,"philo1",0,1).
philo(1,"philo2",1,2).
philo(2,"philo3",2,3).
philo(3,"philo4",3,4).
philo(4,"philo5",4,0).

!prepare_table.

+!prepare_table
  <- makeArtifact("ticketDispenser","c4jexamples.TicketDispenser",[4]);
     for ( .range(I,0,4) ) {
       !create_fork(I);
     };
     for ( .range(Philo,0,4) ) {
       !allocate_fork(Philo);
     };
     println("done.").

+!create_fork(I) 
  <- .concat("fork",I,FN);
     makeArtifact(FN,"c4jexamples.Fork",[],ID);
     out("fork",I,ID).
     
 +!allocate_fork(Philo)
   <- ?philo(Philo,Name,Left,Right);
      rd("fork",Left,ID1);
      rd("fork",Right,ID2);
   	  out("philo_init",Name,ID1,ID2).
   	  