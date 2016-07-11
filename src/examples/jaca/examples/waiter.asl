philo(0,"philo1",0,1).
philo(1,"philo2",1,2).
philo(2,"philo3",2,3).
philo(3,"philo4",3,4).
philo(4,"philo5",4,0).

!prepare_table.

+!prepare_table
  <- for ( .range(I,0,4) ) {
       out("fork",I);
       ?philo(I,Name,Left,Right);
       out("philo_init",Name,Left,Right);
     };
     for ( .range(I,1,4) ) {
       out("ticket");
     };
     println("done.").
     