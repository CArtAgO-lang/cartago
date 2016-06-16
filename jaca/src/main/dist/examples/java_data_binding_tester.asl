!test_java_api.

+!test_java_api
  <- cartago.new_obj("c4jexamples.FlatCountObject",[10],Id);
     .println(Id);
     cartago.invoke_obj(Id,inc);
     cartago.invoke_obj(Id,getValue,Res);
     println(Res);
     cartago.invoke_obj("java.lang.System",currentTimeMillis,T);
     println(T);
     cartago.invoke_obj("java.lang.Class",forName("c4jexamples.FlatCountObject"),Class);
     println(Class).
