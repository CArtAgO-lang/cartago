package cartago.security;

import cartago.ArtifactId;
import cartago.Op;
import cartago.AgentId;

public class AlwaysAllowUsePolicy implements IArtifactUsePolicy {

	public boolean allow(AgentId aid, ArtifactId id, Op opDetail) {
		return true;
	}


}
