!start.

+!start<-
	!do_link_local;
	!do_link_remote;
	?art_id(linker,L);
	dummyOp [artifact_id(L)].
	
+!do_link_local<-
	lookupArtifact("workspace",LocalWspID);
	+wsp_art_id(local,LocalWspID);
	makeArtifact("myLinkerArt", "test.LinkerArt", [], LinkerArtId);
	+art_id(linker,LinkerArtId);
	makeArtifact("linkableArt", "test.LinkableArt", [], LinkableArtID);
	.println("artifacts created");
	.println("A");
	linkArtifacts(LinkerArtId, "out", LinkableArtID)[aid(LocalWspID)];
	.println("first link done").

+!do_link_remote<-
	joinRemoteWorkspace("main", "localhost:20100", RemoteWspID);
	?art_id(linker,LinkerArtId);
	?wsp_art_id(local,LocalWspID);
	.println("join remote done");
	lookupArtifact("remoteLinkableArt", RemoteLinkableArtID)[wid(RemoteWspID)];
	.println("Remote artifact found ",RemoteLinkableArtID);
	.println("local wsp: ",LocalWspID);
	linkArtifacts(LinkerArtId, "out", RemoteLinkableArtID)[aid(LocalWspID)];
	.println("second link done").