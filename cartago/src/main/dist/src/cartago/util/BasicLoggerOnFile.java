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
 */package cartago.util;
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
 
import java.io.*;

import cartago.*;
import cartago.events.ArtifactObsEvent;

/**
 * Adapter for logging components.
 * 
 * @author aricci
 *
 */
public class BasicLoggerOnFile implements  ICartagoLogger {

	private FileWriter fw;
	
	public BasicLoggerOnFile(String fileName) throws IOException{
		fw = new FileWriter(fileName);
	}
	
	@Override
	public void agentJoined(long when, AgentId id) {
		log(when,"agent "+id+" joined the workspace.");
	}

	@Override
	public void agentQuit(long when, AgentId id) {
		log(when,"agent "+id+" quit the workspace.");
	}

	@Override
	public void artifactCreated(long when, ArtifactId id, 
			AgentId creator) {
		log(when,"artifact "+id.getName()+" type: "+id.getArtifactType()+" has been created by "+creator);
	}

	@Override
	public void artifactDisposed(long when, ArtifactId id, AgentId disposer) {
		log(when,"artifact "+id+" has been disposed by "+disposer);
	}

	@Override
	public void artifactFocussed(long when, AgentId who, ArtifactId id,
			IEventFilter ev) {
		log(when,"artifact "+id+" focussed by "+who);
	}

	@Override
	public void artifactNoMoreFocussed(long when, AgentId who, ArtifactId id) {
		log(when,"artifact "+id+" no more focussed by "+who);
	}

	@Override
	public void artifactsLinked(long when, AgentId id, ArtifactId linking,
			ArtifactId linked) {
		log(when,"artifact "+linking+" linked to "+linked+" by "+id);
	}

	@Override
	public void newPercept(long when, ArtifactId aid, Tuple signal,
			ArtifactObsProperty[] added, ArtifactObsProperty[] removed,
			ArtifactObsProperty[] changed) {
		StringBuffer buffer = new StringBuffer("new percept generated about artifact "+aid+" ");
		if (signal!=null){
			buffer.append("signal: "+signal+" ");
		}
		if (added!=null){
			buffer.append("added properties: "+added);
		}
		if (changed!=null){
			buffer.append("changed properties: "+changed);
		}
		if (removed!=null){
			buffer.append("removed properties: "+removed);
		}
		log(when,buffer.toString());
	}

	@Override
	public void opCompleted(long when, OpId oid, ArtifactId aid, Op op) {
		log(when,"operation "+op+" completed in artifact "+aid);
	}

	@Override
	public void opFailed(long when, OpId oid, ArtifactId aid, Op op, String msg,
			Tuple descr) {
		log(when,"operation "+op+" failed in artifact "+aid+" - msg:"+msg+" desc:"+descr);
	}

	@Override
	public void opRequested(long when, AgentId who, ArtifactId aid, Op op) {
		StringBuffer buffer = new StringBuffer("operation "+op+" requested ");
		if (aid!=null){
			buffer.append("in artifact "+aid+" ");
		}
		if (who!=null){
			buffer.append("by agent "+who+" ");
		}
		log(when,buffer.toString());
	}

	@Override
	public void opResumed(long when, OpId oid, ArtifactId aid, Op op) {
		log(when,"operation "+op+" resumed in artifact "+aid);
	}

	@Override
	public void opStarted(long when, OpId oid, ArtifactId aid, Op op) {
		log(when,"operation "+op+" started in artifact "+aid);
	}

	@Override
	public void opSuspended(long when, OpId oid, ArtifactId aid, Op op) {
		log(when,"operation "+op+" suspended in artifact "+aid);
	}
	
	protected void log(long when, String msg){
		try {
			fw.write("[ "+when+" ] "+msg+"\n");
			fw.flush();
		} catch (Exception ex){
			
		}
	}
}
