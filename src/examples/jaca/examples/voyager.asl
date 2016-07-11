!test_remote.

+!test_remote 
  <- ?current_wsp(Id,_,_);
     +default_wsp(Id);
     println("testing remote..");
     joinRemoteWorkspace("default","localhost",WspID2);
     ?current_wsp(_,WName,_);
     println("hello there ",WName);
     !use_remote;
     quitWorkspace.
      
+!use_remote 
  <- makeArtifact("c0","examples.Counter",[],Id);
     focus(Id);
     inc;
     inc.

+count(V) 
  <- ?default_wsp(Id);
     println("count changed: ",V)[wsp_id(Id)].
  
-!use_remote [makeArtifactFailure("artifact_already_present",_)]
  <- ?default_wsp(WId);
     println("artifact already created ")[wsp_id(WId)];
     lookupArtifact("c0",Id);
     focus(Id);
     inc.
     
