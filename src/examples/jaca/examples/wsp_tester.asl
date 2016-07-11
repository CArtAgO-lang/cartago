!test_wsp. 

+!test_wsp 
  <- println("creating new workspaces...");
     createWorkspace("w0");
     createWorkspace("w1");
     joinWorkspace("w0",WspID0);
     println("hello in ",WspID0);
     makeArtifact("myCount","c4jexamples.Counter",[],ArtId);
     joinWorkspace(w1,WspID1);
     println("hello in ",Name2);
     println("using the artifact of another wsp...");
     inc [artifact_id(ArtId)];
     cartago.set_current_wsp(WspID1);
     println("hello again in ",WspID1);
     println("quit..");
     quitWorkspace;
     ?current_wsp(_,Name3,_);
     println("back in ",Name3);
     quitWorkspace;
     cartago.set_current_wsp(Id0);
     ?current_wsp(_,Name4,_);     
     println("...and finallly in ",Name4," again.").
          
