!test_gui.

+!test_gui
  <-  makeArtifact("gui","c4jexamples.MySimpleGUI",[],Id);
      focus(Id).

+value(V) 
  <- println("Value updated: ",V).
  
+ok : value(V)
  <-  setValue(V+1).
      
+closed
  <-  .my_name(Me);
      .kill_agent(Me).

+mouse_dragged(X,Y)
  <- println("Mouse dragged - pos ",X," ",Y).
  