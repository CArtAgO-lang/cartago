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
package cartago.infrastructure.web;

import java.rmi.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import cartago.*;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.http.ServerWebSocket;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.CorsHandler;


/**
 * Class representing a CArtAgO node service, serving remote requests
 *  
 * @author aricci
 *
 */
public class CartagoEnvironmentService extends AbstractVerticle  {

	private String fullAddress;
	private int port;
		
	private Router router;
	private HttpServer server;
	
	private String envName;
	
	private Logger logger = LoggerFactory.getLogger(CartagoEnvironmentService.class);
	
	private static final String API_BASE_PATH = "/cartago/api";

	private ConcurrentLinkedQueue<AgentBodyRemote> remoteCtxs;
	// private GarbageBodyCollectorAgent garbageCollector;
	private ConcurrentHashMap<String, AgentBody> pendingBodies;
	
	
	public CartagoEnvironmentService() throws Exception {
		remoteCtxs = new ConcurrentLinkedQueue<AgentBodyRemote>();	
		// garbageCollector = new GarbageBodyCollectorAgent(remoteCtxs,1000,10000);
		// garbageCollector.start();
		pendingBodies = new  ConcurrentHashMap<String, AgentBody>();
	}	
		
	public void install(String address, int port) throws Exception {
		/* WARNING: the  timeout - 1000 - must be greater than the 
		   delay used by the KeepRemoteContextAliveManager
		   to keep alive the remote contexts */
		//
		this.port = port;
		fullAddress = address+":"+port;

		Vertx vertx = Vertx.vertx();
		vertx.deployVerticle(this);
	}	

	
	@Override
	public void start() {
		initWS();	
	}
	

	private void initWS() {
		
		router = Router.router(vertx);
		
		router.route().handler(CorsHandler.create("*")
				.allowedMethod(io.vertx.core.http.HttpMethod.GET)
				.allowedMethod(io.vertx.core.http.HttpMethod.POST)
				.allowedMethod(io.vertx.core.http.HttpMethod.PUT)
				.allowedMethod(io.vertx.core.http.HttpMethod.DELETE)
				.allowedMethod(io.vertx.core.http.HttpMethod.OPTIONS)
				.allowedHeader("Access-Control-Request-Method")
				.allowedHeader("Access-Control-Allow-Credentials")
				.allowedHeader("Access-Control-Allow-Origin")
				.allowedHeader("Access-Control-Allow-Headers")
				.allowedHeader("Content-Type"));

		router.route().handler(BodyHandler.create());
		
		router.get(API_BASE_PATH + "/version").handler(this::handleGetVersion);
		// router.get(API_BASE_PATH + "/node-id").handler(this::handleGetNodeId);
		// router.post(API_BASE_PATH + "/connect").handler(this::handleConnect);
		// router.post(API_BASE_PATH + "/quit-wsp").handler(this::handleQuitWSP);
		// router.post(API_BASE_PATH + "/exec-ia-op").handler(this::handleExecIAOP);
		// router.post(API_BASE_PATH + "/do-action").handler(this::handleJoinWSP);

		router.get(API_BASE_PATH + "/:masName").handler(this::handleResolveWSP);
		 
		server = vertx.createHttpServer()
		.requestHandler(router)
		.websocketHandler(ws -> {
			 if (ws.path().equals(API_BASE_PATH + "/join")) {
				 this.handleJoinWSP(ws);
			 } else {
				 ws.reject();
			 }
		})
		.listen(port, result -> {
			if (result.succeeded()) {
				log("Ready.");
			} else {
				log("Failed: "+result.cause());
			}
		});

	}

	private  void log(String msg) {
		System.out.println("[ CArtAgO Web Service Layer ] " + msg);
	}

	public void shutdownService(){
		// garbageCollector.stopActivity();		
		server.close();		
	}

	public void registerLogger(String wspName, ICartagoLogger logger) throws RemoteException, CartagoException{
		CartagoEnvironment.getInstance().registerLogger(wspName, logger);
	}
	

	/* JOIN */
	
	private void handleJoinWSP(ServerWebSocket ws) {
		log("Handling Join WSP from "+ws.remoteAddress() + " - " + ws.path());
		CartagoEnvironmentService service = this;
		
		ws.handler(buffer -> {
				JsonObject joinParams = buffer.toJsonObject();
		
				String wspName = joinParams.getString("wspFullName");
				JsonObject agentCred = joinParams.getJsonObject("agent-cred");
				
				String userName = agentCred.getString("userName");
				String roleName = agentCred.getString("roleName");
		
				AgentCredential cred = 	new AgentIdCredential(userName, roleName);
						
				try {
					Workspace wsp = CartagoEnvironment.getInstance().resolveWSP(wspName).getWorkspace();			
					System.out.println("Remote request to join: " + wspName + " " + roleName + " " + cred);
						
				    AgentBodyRemote rbody = new AgentBodyRemote();			
					ICartagoContext ctx = wsp.joinWorkspace(cred, rbody);
					remoteCtxs.add(rbody);
		
					rbody.init((AgentBody) ctx, ws, service);	

					JsonObject reply = new JsonObject()
							.put("wspUUID", wsp.getId().getUUID().toString());
					ws.writeTextMessage(reply.encode());
										
				} catch (Exception ex) {
					ex.printStackTrace();
					ws.reject();
				}
			});
	}

	public void registerNewJoin(String bodyId, AgentBody body) {
		this.pendingBodies.put(bodyId, body);
	}
	
	/* QUIT */
	
	private void handleQuitWSP(RoutingContext routingContext) {
		log("Handling Quit WSP from "+routingContext.request().absoluteURI());
		HttpServerResponse response = routingContext.response();
		response.putHeader("content-type", "application/text").end("Not implemented.");
	}
	
	/*
	public void quit(String wspName, AgentId id) throws RemoteException, CartagoException {
		CartagoWorkspace wsp = node.getWorkspace(wspName);		
		wsp.quitAgent(id);
		Iterator<AgentBodyRemote> it = remoteCtxs.iterator();
		while (it.hasNext()){
			AgentBodyRemote c = it.next();
			if (c.getAgentId().equals(id)){
				it.remove();
				break;
			}
		} 
	}*/
	
	
	/* exec Inter-artifact  */
	
	private void handleExecIAOP(RoutingContext routingContext) {
		log("Handling Exec Inter artifact OP from "+routingContext.request().absoluteURI());
		HttpServerResponse response = routingContext.response();
		response.putHeader("content-type", "application/text").end("Not implemented.");
	}
	/**
	 * Exec an inter-artifact operation call.
	 * 
	 * @param callback
	 * @param callbackId
	 * @param userId
	 * @param srcId
	 * @param targetId
	 * @param op
	 * @param timeout
	 * @param test
	 * @return
	 * @throws RemoteException
	 * @throws CartagoException
	 *//*
	public OpId execInterArtifactOp(ICartagoCallback callback, long callbackId, AgentId userId, ArtifactId srcId, ArtifactId targetId, Op op, long timeout, IAlignmentTest test) throws RemoteException, CartagoException {
		String wspName = targetId.getWorkspaceId().getName();
		CartagoWorkspace wsp = (CartagoWorkspace) node.getWorkspace(wspName);
		return wsp.execInterArtifactOp(callback, callbackId, userId, srcId, targetId, op, timeout, test);
	}*/	 
	

	/* GET VERSION */

	private void handleGetVersion(RoutingContext routingContext) {
		log("Handling Get Version from "+routingContext.request().absoluteURI());
		HttpServerResponse response = routingContext.response();
		response.putHeader("content-type", "application/text").end(CARTAGO_VERSION.getID());
	}

	/* GET WORKSPACE INFO */

	private void handleResolveWSP(RoutingContext routingContext) {
		log("Handling ResolveWSP from "+routingContext.request().absoluteURI());
		String envName = routingContext.request().getParam("masName");
		String fullPath = routingContext.request().getParam("wsp");
		JsonObject obj = new JsonObject();
		try {
			WorkspaceDescriptor des = CartagoEnvironment.getInstance().resolveWSP(fullPath);
			obj.put("envName", des.getEnvName());
			obj.put("envId", des.getEnvId().toString());
			if (des.isLocal()) {
				obj.put("id", JsonUtil.toJson(des.getId()));
			} else {
				obj.put("remotePath", des.getRemotePath());
				obj.put("address", des.getAddress());
				obj.put("protocol", des.getProtocol());
			}
			routingContext.response().putHeader("content-type", "application/text").end(obj.encode());
		} catch (Exception ex) {
			HttpServerResponse response = routingContext.response();
			response.setStatusCode(404).end();
		}
	}


}
