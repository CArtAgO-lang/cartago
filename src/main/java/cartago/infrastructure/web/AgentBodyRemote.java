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
package cartago.infrastructure.web;

import cartago.*;
import cartago.events.*;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.ServerWebSocket;
import io.vertx.core.json.JsonObject;

import static cartago.infrastructure.web.JsonUtil.*;

public class AgentBodyRemote  implements ICartagoCallback {
    
	private ServerWebSocket websocket;
    private  AgentBody ctx;  
	private long lastPingFromMind;

    
    public AgentBodyRemote()   {
        lastPingFromMind = System.currentTimeMillis();
    }

    public void init(AgentBody ctx, ServerWebSocket websocket) {
    	this.ctx = ctx;
        this.websocket = websocket;
        websocket.handler(this::handleData);
    }
    
    
    private void handleData(Buffer data) {
    	synchronized (this) {	
    		JsonObject req = data.toJsonObject();
    		String reqType = req.getString("reqType");
    		
    		if (reqType.equals("doAction")) { 
    			long agentCallbackId = req.getLong("agentCallbackId");
    			long timeout = req.getLong("timeout");    			
    			try {	    				    			
	    			Op op = toOp(req.getJsonObject("op"));	    			
    				String artifactName = req.getString("artifactName");
	    			if (artifactName != null) {
	        			ctx.doAction(agentCallbackId, artifactName, op, null, timeout);
	    			} else {
	        			ctx.doAction(agentCallbackId, op, null, timeout);
	    			}
    			} catch (Exception ex) {
    				ex.printStackTrace();
    			}
    		} else if (reqType.equals("ping")) {
    			ping();
    		} else if (reqType.equals("getAgentId")) {
    			
    		} else if (reqType.equals("getWorkspaceId")) {
    			
    		}
    	}
    }


	private void writeEventInfo(JsonObject obj, CartagoEvent ev) {
		obj.put("id", ev.getId());
		obj.put("timestamp", ev.getTimestamp());
	}

	private void writeActionEventInfo(JsonObject obj, CartagoActionEvent ev) {
		writeEventInfo(obj,ev);
		obj.put("actionId", ev.getActionId());
		obj.put("timestamp", ev.getTimestamp());
		obj.put("op", toJson(ev.getOp()));
	}

	
	@Override
	public void notifyCartagoEvent(CartagoEvent ev) {
		synchronized (this) {
			try {
				if (websocket != null) {
					JsonObject evo = new JsonObject();
					if (ev instanceof ActionSucceededEvent) {
						evo.put("evType", "actionSucceeded");
						ActionSucceededEvent evAct = (ActionSucceededEvent) ev;
						writeActionEventInfo(evo, evAct);		
						if (evAct.getArtifactId() != null) {
							evo.put("artifactId", toJson(evAct.getArtifactId()));
						}
					} else if (ev instanceof ActionFailedEvent) {
						evo.put("evType", "actionFailed");
						ActionFailedEvent evAct = (ActionFailedEvent) ev;
						writeActionEventInfo(evo, evAct);
						evo.put("failureMsg", evAct.getFailureMsg());
						evo.put("failureReason", toJson(evAct.getFailureDescr()));
					} else if (ev instanceof ArtifactObsEvent) {
						evo.put("evType", "artifactObs");
						writeEventInfo(evo, ev);
						ArtifactObsEvent evObs = (ArtifactObsEvent) ev;
						evo.put("src", toJson(evObs.getArtifactId()));
						if (evObs.getSignal() != null) {
							evo.put("signal", toJson(evObs.getSignal()));
						}
						if (evObs.getChangedProperties() != null) {
							evo.put("propsChanged", toJson(evObs.getChangedProperties()));
						}
						if (evObs.getAddedProperties() != null) {
							evo.put("propsAdded", toJson(evObs.getAddedProperties()));
						}
						if (evObs.getRemovedProperties() != null) {
							evo.put("propsRemoved", toJson(evObs.getRemovedProperties()));
						}
					} else if (ev instanceof FocusSucceededEvent) {
						evo.put("evType", "focusSucceeded");
						FocusSucceededEvent evFoc = (FocusSucceededEvent) ev;
						writeActionEventInfo(evo, evFoc);						
						evo.put("targetArtifactId", toJson(evFoc.getArtifactId()));
						evo.put("artifactId", toJson(evFoc.getTargetArtifact()));
						evo.put("props", toJson(evFoc.getObsProperties()));
					} else if (ev instanceof StopFocusSucceededEvent) {
						evo.put("evType", "stopFocusSucceeded");
						StopFocusSucceededEvent evFoc = (StopFocusSucceededEvent) ev;
						writeActionEventInfo(evo, evFoc);						
						evo.put("targetArtifactId", toJson(evFoc.getArtifactId()));
						evo.put("artifactId", toJson(evFoc.getTargetArtifact()));
						evo.put("props", toJson(evFoc.getObsProperties()));
					} else if (ev instanceof FocussedArtifactDisposedEvent) {
						evo.put("evType", "focussedArtifactDisposed");
						FocussedArtifactDisposedEvent evFoc = (FocussedArtifactDisposedEvent) ev;
						writeEventInfo(evo, ev);
						evo.put("src", toJson(evFoc.getArtifactId()));
						if (evFoc.getSignal() != null) {
							evo.put("signal", toJson(evFoc.getSignal()));
						}
						if (evFoc.getChangedProperties() != null) {
							evo.put("propsChanged", toJson(evFoc.getChangedProperties()));
						}
						if (evFoc.getAddedProperties() != null) {
							evo.put("propsAdded", toJson(evFoc.getAddedProperties()));
						}
						if (evFoc.getRemovedProperties() != null) {
							evo.put("propsRemoved", toJson(evFoc.getRemovedProperties()));
						}
						evo.put("props", toJson(evFoc.getObsProperties()));
					}

					websocket.writeTextMessage(evo.encode());
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}

	private synchronized void  ping()  {
		lastPingFromMind = System.currentTimeMillis();
	}
	
	synchronized long getLastPing(){
		return lastPingFromMind;
	}	
	
	AgentBody getContext(){
		return ctx;
	}


	

}
