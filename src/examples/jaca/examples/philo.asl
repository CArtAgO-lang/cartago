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
  <- in("ticket");
     in("fork",F1);
     in("fork",F2).
  
+!releaseRes: 
  my_left_fork(F1) & my_right_fork(F2) 
 <-  out("fork",F1);
     out("fork",F2);
     out("ticket").
       
+!thinking 
  <- println("Thinking").
+!eat 
  <- println("Eating").
