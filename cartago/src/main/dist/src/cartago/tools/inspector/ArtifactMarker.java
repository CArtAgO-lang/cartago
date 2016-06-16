package cartago.tools.inspector;

import cartago.*;

public class ArtifactMarker extends Marker {

	private ArtifactId id;
	
	public ArtifactMarker(ArtifactId aid, P2d pos){
		super(pos);
		this.id = aid;
	}

	public ArtifactId getArtifactId(){
		return id;
	}
	
}
