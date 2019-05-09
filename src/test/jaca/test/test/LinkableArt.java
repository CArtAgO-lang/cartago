package test;

import cartago.Artifact;
import cartago.LINK;

public class LinkableArt extends Artifact {

	@LINK void gino(){
		log("linked: gino called ok.");
	}
}