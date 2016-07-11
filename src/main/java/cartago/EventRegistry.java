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

import java.util.concurrent.atomic.AtomicLong;
import cartago.events.*;
import java.util.*;

/**
 * Factory of CArtAgO events.
 * 
 *  
 * @author aricci
 *
 */
public class EventRegistry {

	private AtomicLong nextTimestamp;
	
	public EventRegistry(){
		nextTimestamp = new AtomicLong(0);
	}

	/**
	 * Get the timestamp of the next event
	 * 
	 * @return
	 */
	public long getNextTimestamp(){
		return nextTimestamp.longValue();
	}

	public ArtifactObsEvent makeObsEvent(ArtifactId src, Tuple signal, ArtifactObsProperty[] changed, ArtifactObsProperty[] added, ArtifactObsProperty[] removed){
		long id = nextTimestamp.incrementAndGet();
		ArtifactObsEvent ev = new ArtifactObsEvent(id, src, signal, changed, added, removed);
		return ev;
	}

	public FocussedArtifactDisposedEvent makeFocussedArtifactDisposedEvent(ArtifactId target, List<ArtifactObsProperty> props){
		long id = nextTimestamp.incrementAndGet();
		FocussedArtifactDisposedEvent ev = new FocussedArtifactDisposedEvent(id, target, props);
		return ev;
	}

	public ActionSucceededEvent makeActionSucceededEvent(long actionId, ArtifactId aid, Op op){
		long id = nextTimestamp.incrementAndGet();
		ActionSucceededEvent ev = new ActionSucceededEvent(id, actionId, op, aid);
		return ev;
	}

	public ActionFailedEvent makeActionFailedEvent(long actionId, String failureMsg, Tuple failureReason, Op op){
		long id = nextTimestamp.incrementAndGet();
		ActionFailedEvent ev = new ActionFailedEvent(id, actionId, op, failureMsg, failureReason);
		return ev;
	}

	public FocusSucceededEvent makeFocusActionSucceededEvent(long actionId, ArtifactId aid, Op op, ArtifactId target, List<ArtifactObsProperty> props){
		long id = nextTimestamp.incrementAndGet();
		FocusSucceededEvent ev = new FocusSucceededEvent(id, actionId, op, aid, target, props);
		return ev;
	}

	public StopFocusSucceededEvent makeStopFocusActionSucceededEvent(long actionId, ArtifactId aid, Op op, ArtifactId target,List<ArtifactObsProperty> props){
		long id = nextTimestamp.incrementAndGet();
		StopFocusSucceededEvent ev = new StopFocusSucceededEvent(id, actionId, op, aid, target, props);
		return ev;
	}

	public JoinWSPSucceededEvent makeJoinWSPSucceededEvent(long actionId, ArtifactId aid, Op op, WorkspaceId wspId, ICartagoContext ctx) {
		long id = nextTimestamp.incrementAndGet();
		JoinWSPSucceededEvent ev = new JoinWSPSucceededEvent(id, actionId, op, aid, wspId, ctx);
		return ev;
	}

	public QuitWSPSucceededEvent makeQuitWSPSucceededEvent(long actionId, ArtifactId aid, Op op, WorkspaceId wspId) {
		long id = nextTimestamp.incrementAndGet();
		QuitWSPSucceededEvent ev = new QuitWSPSucceededEvent(id, actionId, op, aid, wspId);
		return ev;
	}
	
	public ConsultManualSucceededEvent makeConsultManualSucceededEvent(long actionId, ArtifactId aid, Op op, Manual man){
		long id = nextTimestamp.incrementAndGet();
		ConsultManualSucceededEvent ev = new ConsultManualSucceededEvent(id, actionId, op, aid, man);
		return ev;
	}
	
	public ObsArtListChangedEvent makeObsArtListChangedEvent(List<ObservableArtifactInfo> newFocused, List<ObservableArtifactInfo> noMoreFocused){
		long id = nextTimestamp.incrementAndGet();
		ObsArtListChangedEvent ev = new ObsArtListChangedEvent(id, newFocused, noMoreFocused);
		return ev;
	}
	

}
