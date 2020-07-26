/**
 * CArtAgO - DISI, University of Bologna
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package cartago;

import cartago.events.ArtifactObsEvent;

/**
 * Adapter for logging components.
 * 
 * @author aricci
 *
 */
public class CartagoLoggerAdapter implements ICartagoLogger {

	@Override
	public void agentJoined(long when, AgentId id) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void agentQuit(long when, AgentId id) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void artifactCreated(long when, ArtifactId id, 
			AgentId creator) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void artifactDisposed(long when, ArtifactId id, AgentId disposer) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void artifactFocussed(long when, AgentId who, ArtifactId id,
			IEventFilter ev) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void artifactNoMoreFocussed(long when, AgentId who, ArtifactId id) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void artifactsLinked(long when, AgentId id, ArtifactId linking,
			ArtifactId linked) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void newPercept(long when, ArtifactId aid, Tuple signal,
			ArtifactObsProperty[] added, ArtifactObsProperty[] removed,
			ArtifactObsProperty[] changed) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void opCompleted(long when, OpId oid, ArtifactId aid, Op op) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void opFailed(long when, OpId oid, ArtifactId aid, Op op, String msg,
			Tuple descr) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void opRequested(long when, AgentId who, ArtifactId aid, Op op) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void opResumed(long when, OpId oid, ArtifactId aid, Op op) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void opStarted(long when, OpId oid, ArtifactId aid, Op op) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void opSuspended(long when, OpId oid, ArtifactId aid, Op op) {
		// TODO Auto-generated method stub
		
	}

	
}
