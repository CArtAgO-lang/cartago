package cartago.security;

import cartago.ArtifactId;
import cartago.Op;
import cartago.AgentId;

public class AlwaysForbidUsePolicy implements IArtifactUsePolicy {

	public boolean allow(AgentId aid, ArtifactId id, Op opDetail) {
		return false;
	}


}
