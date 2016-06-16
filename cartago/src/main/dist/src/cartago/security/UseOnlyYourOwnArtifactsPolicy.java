package cartago.security;

import cartago.*;
import cartago.security.*;

public class UseOnlyYourOwnArtifactsPolicy implements IArtifactUsePolicy {

	public boolean allow(AgentId aid, ArtifactId id, Op opDetail) {
		return aid.getAgentName().equals(id.getCreatorId().getAgentName());
	}

}
