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
 * Interface for log managers
 * 
 * @author aricci
 *
 */
public interface ICartagoLoggerManager extends ICartagoLogger {

	boolean isLogging();
	void registerLogger(ICartagoLogger logger);
	void unregisterLogger(ICartagoLogger logger);
	void opRequested(long when, AgentId who, ArtifactId aid, Op op) ;
	void opStarted(long when, OpId id, ArtifactId aid, Op op)  ;
	void opSuspended(long when, OpId id, ArtifactId aid, Op op)  ;
	void opResumed(long when, OpId id, ArtifactId aid, Op op)  ;
	void opCompleted(long when, OpId id, ArtifactId aid, Op op)  ;
	void opFailed(long when, OpId id, ArtifactId aid, Op op, String msg, Tuple descr)  ;
	void newPercept(long when, ArtifactId aid, Tuple signal, ArtifactObsProperty[] added, ArtifactObsProperty[] removed, ArtifactObsProperty[] changed)  ;
    
	void artifactCreated(long when, ArtifactId id,  AgentId creator)  ;
	void artifactDisposed(long when, ArtifactId id, AgentId disposer)  ;
	void artifactFocussed(long when, AgentId who, ArtifactId id, IEventFilter ev)  ;
	void artifactNoMoreFocussed(long when, AgentId who, ArtifactId id)  ;
	void artifactsLinked(long when, AgentId id, ArtifactId linking, ArtifactId linked)  ;
	
	void agentJoined(long when, AgentId id)  ;
    void agentQuit(long when, AgentId id)  ;

}