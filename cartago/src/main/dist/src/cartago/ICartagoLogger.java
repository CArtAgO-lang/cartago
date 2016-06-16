/**
 * CArtAgO - DEIS, University of Bologna
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

/**
 * Interface for implementing logging components.
 * 
 * @author aricci
 *
 */
public interface ICartagoLogger {
	
	
	void opRequested(long when, AgentId who, ArtifactId aid, Op op) throws CartagoException;
	void opStarted(long when, OpId id, ArtifactId aid, Op op)  throws CartagoException;
	void opSuspended(long when, OpId id, ArtifactId aid, Op op)  throws CartagoException;
	void opResumed(long when, OpId id, ArtifactId aid, Op op)  throws CartagoException;
	void opCompleted(long when, OpId id, ArtifactId aid, Op op)  throws CartagoException;
	void opFailed(long when, OpId id, ArtifactId aid, Op op, String msg, Tuple descr)  throws CartagoException;
	void newPercept(long when, ArtifactId aid, Tuple signal, ArtifactObsProperty[] added, ArtifactObsProperty[] removed, ArtifactObsProperty[] changed)  throws CartagoException;
    
	void artifactCreated(long when, ArtifactId id,  AgentId creator)  throws CartagoException;
	void artifactDisposed(long when, ArtifactId id, AgentId disposer)  throws CartagoException;
	void artifactFocussed(long when, AgentId who, ArtifactId id, IEventFilter ev)  throws CartagoException;
	void artifactNoMoreFocussed(long when, AgentId who, ArtifactId id)  throws CartagoException;
	void artifactsLinked(long when, AgentId id, ArtifactId linking, ArtifactId linked)  throws CartagoException;
	
	void agentJoined(long when, AgentId id)  throws CartagoException;
    void agentQuit(long when, AgentId id)  throws CartagoException;
	
}
