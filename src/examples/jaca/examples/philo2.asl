!start.

+!start
  <- .my_name(Me);
     in("philo_init",Me,Left,Right);
     +my_left_fork(Left);
     +my_right_fork(Right);
     println(Me," ready.");
     !!living.
     
+!living
 <- !thinking;
    !eating;
    !!living.

+!eating 
 <- !acquireRes;
    !eat;
    !releaseRes.
      
+!acquireRes : 
  my_left_fork(F1) & my_right_fork(F2) 
  <- getATicket;
     //println("got a ticket");
     grabAsap [artifact_id(F1)];
     grabAsap [artifact_id(F2)].
  
+!releaseRes: 
  my_left_fork(F1) & my_right_fork(F2) 
 <-  releaseTicket;
     release [artifact_id(F1)];
     release [artifact_id(F2)].    
      
+!thinking 
  <- .println("Thinking").
+!eat 
  <- .println("Eating").
