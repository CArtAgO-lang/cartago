package test;

import cartago.*;

public class Space2D implements AbstractWorkspaceTopology, java.io.Serializable {
	
	@Override
	public double getDistance(AbstractWorkspacePoint p0, AbstractWorkspacePoint p1) {
		return ((P2d)p0).getDistanceFrom((P2d)p1);
	}

}
