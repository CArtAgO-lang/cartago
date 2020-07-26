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

import java.util.*;

import cartago.events.ArtifactObsEvent;

/**
 * Manager of the logging components.
 * 
 * @author aricci
 *
 */
class CartagoLoggerManager implements ICartagoLoggerManager {

	private boolean isLogging;
    LinkedList<ICartagoLogger> loggers;
	
    public CartagoLoggerManager(){
    	isLogging = false;
    	loggers = new LinkedList<ICartagoLogger>();
    }
    
	public boolean isLogging(){
		return isLogging;
	}
	
	public   void registerLogger(ICartagoLogger logger){
		loggers.add(logger);
		isLogging = true;
	}

	public   void unregisterLogger(ICartagoLogger logger){
		loggers.remove(logger);
		if (loggers.size()==0){
			isLogging = false;
		}
	}

	@Override
	public void agentJoined(long when, AgentId id) {
		Iterator<ICartagoLogger> it = loggers.iterator();
		while (it.hasNext()){
			ICartagoLogger l = it.next();
			try {
				l.agentJoined(when, id);
			} catch (Exception ex){
				it.remove();
			}
		}
	}

	@Override
	public void agentQuit(long when, AgentId id)  {
		Iterator<ICartagoLogger> it = loggers.iterator();
		while (it.hasNext()){
			ICartagoLogger l = it.next();
			try {
				l.agentQuit(when, id);
			} catch (Exception ex){
				it.remove();
			}
		}
	}

	@Override
	public void artifactCreated(long when, ArtifactId id, AgentId creator)  {
		Iterator<ICartagoLogger> it = loggers.iterator();
		while (it.hasNext()){
			ICartagoLogger l = it.next();
			try {
				l.artifactCreated(when, id, creator);
			} catch (Exception ex){
				it.remove();
			}
		}
	}

	@Override
	public void artifactDisposed(long when, ArtifactId id, AgentId disposer) {
		Iterator<ICartagoLogger> it = loggers.iterator();
		while (it.hasNext()){
			ICartagoLogger l = it.next();
			try {
				l.artifactDisposed(when, id, disposer);
			} catch (Exception ex){
				it.remove();
			}
		}
	}

	@Override
	public void artifactFocussed(long when, AgentId who, ArtifactId id,
			IEventFilter ev) {
		Iterator<ICartagoLogger> it = loggers.iterator();
		while (it.hasNext()){
			ICartagoLogger l = it.next();
			try {
				l.artifactFocussed(when, who, id, ev);
			} catch (Exception ex){
				it.remove();
			}
		}
	}

	@Override
	public void artifactNoMoreFocussed(long when, AgentId who, ArtifactId id) {
		Iterator<ICartagoLogger> it = loggers.iterator();
		while (it.hasNext()){
			ICartagoLogger l = it.next();
			try {
				l.artifactNoMoreFocussed(when, who, id);
			} catch (Exception ex){
				it.remove();
			}
		}
	}

	@Override
	public void artifactsLinked(long when, AgentId id, ArtifactId linking,
			ArtifactId linked) {
		Iterator<ICartagoLogger> it = loggers.iterator();
		while (it.hasNext()){
			ICartagoLogger l = it.next();
			try {
				l.artifactsLinked(when, id, linking, linked);
			} catch (Exception ex){
				it.remove();
			}
		}
	}

	@Override
	public void newPercept(long when, ArtifactId aid, Tuple signal,
			ArtifactObsProperty[] added, ArtifactObsProperty[] removed,
			ArtifactObsProperty[] changed) {
		Iterator<ICartagoLogger> it = loggers.iterator();
		while (it.hasNext()){
			ICartagoLogger l = it.next();
			try {
				l.newPercept(when, aid, signal, added, removed, changed);
			} catch (Exception ex){
				it.remove();
			}
		}
	}

	@Override
	public void opCompleted(long when, OpId oid, ArtifactId aid, Op op) {
		Iterator<ICartagoLogger> it = loggers.iterator();
		while (it.hasNext()){
			ICartagoLogger l = it.next();
			try {
				l.opCompleted(when, oid, aid, op);
			} catch (Exception ex){
				it.remove();
			}
		}
	}

	@Override
	public void opFailed(long when, OpId oid, ArtifactId aid, Op op, String msg,
			Tuple descr) {
		Iterator<ICartagoLogger> it = loggers.iterator();
		while (it.hasNext()){
			ICartagoLogger l = it.next();
			try {
				l.opFailed(when, oid, aid, op, msg, descr);
			} catch (Exception ex){
				it.remove();
			}
		}
	}

	@Override
	public void opRequested(long when, AgentId who, ArtifactId aid, Op op) {
		Iterator<ICartagoLogger> it = loggers.iterator();
		while (it.hasNext()){
			ICartagoLogger l = it.next();
			try {
				l.opRequested(when, who, aid, op);
			} catch (Exception ex){
				it.remove();
			}
		}
	}

	@Override
	public void opResumed(long when, OpId oid, ArtifactId aid, Op op) {
		Iterator<ICartagoLogger> it = loggers.iterator();
		while (it.hasNext()){
			ICartagoLogger l = it.next();
			try {
				l.opResumed(when, oid, aid, op);
			} catch (Exception ex){
				it.remove();
			}
		}
	}

	@Override
	public void opStarted(long when, OpId oid, ArtifactId aid, Op op) {
		Iterator<ICartagoLogger> it = loggers.iterator();
		while (it.hasNext()){
			ICartagoLogger l = it.next();
			try {
				l.opStarted(when, oid, aid, op);
			} catch (Exception ex){
				it.remove();
			}
		}
	}

	@Override
	public void opSuspended(long when, OpId oid, ArtifactId aid, Op op) {
		Iterator<ICartagoLogger> it = loggers.iterator();
		while (it.hasNext()){
			ICartagoLogger l = it.next();
			try {
				l.opSuspended(when, oid, aid, op);
			} catch (Exception ex){
				it.remove();
			}
		}
	}
		
}
