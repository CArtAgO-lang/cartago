/**
1 * CArtAgO - DISI, University of Bologna
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
package cartago.infrastructure.web;

import java.io.Serializable;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import cartago.*;
import cartago.events.ActionFailedEvent;
import cartago.events.ActionSucceededEvent;
import cartago.events.ArtifactObsEvent;
import cartago.events.FocusSucceededEvent;
import cartago.events.FocussedArtifactDisposedEvent;
import cartago.events.StopFocusSucceededEvent;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.WebSocket;
import io.vertx.core.json.JsonObject;

import static cartago.infrastructure.web.JsonUtil.*;
/**
 * Class used to adapt Agent Body remote interface to Agent Body
 * interface.
 * 
 * @author aricci
 * 
 */
public class AgentBodyProxy implements ICartagoContext, Serializable {

	private WorkspaceId wspId;
	private WebSocket ws;
	private ICartagoCallback eventListener;
    private String address;
	private Vertx vertx;
	private int port;
	private AgentId aid;
	private ConcurrentHashMap<Long, TryActionReply> acceptedActionsMap;
	
	AgentBodyProxy(Vertx vertx, int port) {
		this.vertx = vertx;
		this.port = port;
	}

	public void init(String address, WebSocket ws, WorkspaceId wspId, ICartagoCallback eventListener) {
		this.ws = ws;
		this.address = address;
		this.eventListener = eventListener;
		this.wspId = wspId;
		acceptedActionsMap = new ConcurrentHashMap<Long, TryActionReply>();
		ws.handler(this::handleEvent);
	}
	
	private void readEventInfo(JsonObject obj, CartagoEvent ev) {
		obj.put("id", ev.getId());
		obj.put("timestamp", ev.getTimestamp());
	}

	private void handleEvent(Buffer buffer) {
		try {
			JsonObject evobj = buffer.toJsonObject();
			CartagoEvent ev = null;
			String evType = evobj.getString("evType");
			long id = evobj.getLong("id");
			long ts = evobj.getLong("timestamp");
			if (evType.equals("actionSucceeded")) {
				long actionId = evobj.getLong("actionId");
				Op op = toOp(evobj.getJsonObject("op"));
				ArtifactId aid = toArtifactId(evobj.getJsonObject("artifactId"));
				ev = new ActionSucceededEvent(id, actionId, op, aid, ts);
			} else if (evType.equals("actionAccepted")) { 
				long actionId = evobj.getLong("actionId");
			
			} else if (evType.equals("actionFailed")) {
				long actionId = evobj.getLong("actionId");
				Op op = toOp(evobj.getJsonObject("op"));
				String failureMsg = evobj.getString("failureMsg");
				Tuple failureReason = toTuple(evobj.getJsonObject("failureReason"));
				ev = new ActionFailedEvent(id, actionId, op, failureMsg, failureReason, ts);
			} else if (evType.equals("artifactObs")) {
				ArtifactId src = toArtifactId(evobj.getJsonObject("src"));
				Tuple signal = toTuple(evobj.getJsonObject("signal"));
				ArtifactObsProperty[] propsChanged = toArtifactObsPropertyArray(evobj.getJsonArray("propsChanged"));
				ArtifactObsProperty[] propsAdded = toArtifactObsPropertyArray(evobj.getJsonArray("propsAdded"));
				ArtifactObsProperty[] propsRemoved = toArtifactObsPropertyArray(evobj.getJsonArray("propsRemoved"));				
				ev = new ArtifactObsEvent(id, src, signal, propsChanged, propsAdded, propsRemoved, ts);
			} else if (evType.equals("focusSucceeded")) {
				long actionId = evobj.getLong("actionId");
				JsonObject jop = evobj.getJsonObject("op");
				Op op = jop != null ? toOp(jop) : null;
				ArtifactId aid = toArtifactId(evobj.getJsonObject("artifactId"));
				ArtifactId targetArtifact = toArtifactId(evobj.getJsonObject("targetArtifact"));
				List<ArtifactObsProperty> props = toArtifactObsPropertyList(evobj.getJsonArray("props"));
				ev = new FocusSucceededEvent(id, actionId, op, aid, targetArtifact, props, ts);
			} else if (evType.equals("stopFocusSucceeded")) {
				long actionId = evobj.getLong("actionId");
				Op op = toOp(evobj.getJsonObject("op"));
				ArtifactId aid = toArtifactId(evobj.getJsonObject("artifactId"));
				ArtifactId targetArtifact = toArtifactId(evobj.getJsonObject("targetArtifact"));
				List<ArtifactObsProperty> props = toArtifactObsPropertyList(evobj.getJsonArray("props"));
				ev = new StopFocusSucceededEvent(id, actionId, op, aid, targetArtifact, props, ts);
			} else if (evType.equals("focussedArtifactDisposed")) {
				ArtifactId src = toArtifactId(evobj.getJsonObject("src"));
				List<ArtifactObsProperty> props = toArtifactObsPropertyList(evobj.getJsonArray("props"));
				ev = new FocussedArtifactDisposedEvent(id, src, props, ts);
			} else {
				log("Event not to be handled: " + evType);
				return;
			}	
			
			eventListener.notifyCartagoEvent(ev);
			
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
		
	@Override
	public void quit() throws CartagoException {
		try {
			JsonObject req = makeJsonObjForQuit();
			ws.writeTextMessage(req.encodePrettily());
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new CartagoException(ex.getMessage());
		}
	}

	/*
	 * Explicit artifact
	 */
	public void doAction(long agentCallbackId, String id, Op op, IAlignmentTest test,
			long timeout) throws CartagoException {
		try {
			JsonObject req = makeJsonObjForAct(agentCallbackId, op, timeout);
			req.put("artifactName", id);			
			ws.writeTextMessage(req.encodePrettily());
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new CartagoException(ex.getMessage());
		}

	}

	/*
	 * Implicit artifact - op oriented.
	 * 
	 */
	public void doAction(long agentCallbackId, Op op, IAlignmentTest test,
			long timeout) throws CartagoException {
		try {
			JsonObject req = makeJsonObjForAct(agentCallbackId, op, timeout);
			ws.writeTextMessage(req.encodePrettily());
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new CartagoException(ex.getMessage());
		}
	}

	@Override
	public boolean doTryAction(long agentCallbackId, Op op, IAlignmentTest test, long timeout) throws CartagoException {
		try {
			JsonObject req = makeJsonObjForTryAct(agentCallbackId, op, timeout);
			TryActionReply replyInfo = new TryActionReply();
			this.acceptedActionsMap.put(agentCallbackId, replyInfo);
			ws.writeTextMessage(req.encodePrettily());
			replyInfo.waitForResult(20000);
			return replyInfo.isAccepted();
		} catch (Exception ex) {
			ex.printStackTrace();
			return false;
			// throw new CartagoException(ex.getMessage());
		}
	}

	
	@Override
	public WorkspaceId getWorkspaceId() throws CartagoException {
		return wspId;
	}

	@Override
	public AgentId getAgentId() throws CartagoException {
		return aid;
	}

	
	private JsonObject makeJsonObjForAct(long agentCallbackId, Op op,long timeout) {
		JsonObject req = new JsonObject();
		req.put("reqType", "doAction");		
		req.put("agentCallbackId", agentCallbackId);
		req.put("timeout", timeout);
		req.put("op", toJson(op));
		return req;
	}

	private JsonObject makeJsonObjForTryAct(long agentCallbackId, Op op,long timeout) {
		JsonObject req = new JsonObject();
		req.put("reqType", "doTryAction");		
		req.put("agentCallbackId", agentCallbackId);
		req.put("timeout", timeout);
		req.put("op", toJson(op));
		return req;
	}

	private JsonObject makeJsonObjForQuit() {
		JsonObject req = new JsonObject();
		req.put("reqType", "quit");		
		return req;
	}
	
	private void log(String msg) {
		System.out.println("[AgentBodyProxy | web infra layer]  " + msg);
	}

    
}
