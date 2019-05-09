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

import java.rmi.*;
import java.net.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Semaphore;

import cartago.*;
import cartago.infrastructure.*;
import cartago.security.*;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpClientOptions;
import io.vertx.core.http.HttpClientRequest;
import io.vertx.core.http.ServerWebSocket;
import io.vertx.core.http.WebSocket;
import io.vertx.core.json.JsonObject;


/**
 * CArtAgO RMI Infrastructure Service - enables remote interaction exploiting RMI transport protocol.
 *  
 * @author aricci
 *
 */
public class CartagoInfrastructureLayer implements ICartagoInfrastructureLayer {
	
	// private KeepRemoteBodyAliveManagerAgent keepAliveAgent;
	// private ConcurrentLinkedQueue<AgentBodyProxy> remoteCtxs;
	static public final int DEFAULT_PORT = 20100; 
	private CartagoNodeService service;
	private Vertx vertx;
	private boolean error = false;
	
	public CartagoInfrastructureLayer(){
		/*
		remoteCtxs = new ConcurrentLinkedQueue<AgentBodyProxy>();
		keepAliveAgent = new KeepRemoteBodyAliveManagerAgent(remoteCtxs,5000);
		keepAliveAgent.start();
		*/
		// throw new RuntimeException("not implemented");
		vertx = Vertx.vertx();
	}
		
	public void shutdownLayer() throws CartagoException {
		// keepAliveAgent.shutdown();
		this.shutdownService();
	}

	public ICartagoContext joinRemoteWorkspace(String wspName, String address, AgentCredential cred, ICartagoCallback eventListener) throws CartagoInfrastructureLayerException, CartagoException {
		
		try {
			
			int port = getPort(address);
			if (port == -1) {
				port = DEFAULT_PORT;
			}

			HttpClientOptions options = new HttpClientOptions().setDefaultHost(address).setDefaultPort(port);
			HttpClient client = vertx.createHttpClient(options);
			
			AgentBodyProxy proxy = new AgentBodyProxy();
			
			Semaphore ev = new Semaphore(0);
			error = false;
			
			client.websocket("/cartago/api/join", (WebSocket ws) -> {
				  
				  System.out.println("Connected!");

				  // sending
				  
				  JsonObject params = new JsonObject();
				  params.put("wspName", wspName);
				  
				  JsonObject ac = new JsonObject();
				  ac.put("userName", cred.getId());
				  ac.put("roleName", cred.getRoleName());
				  
				  params.put("agent-cred", ac);
				  
				  ws.handler((Buffer b) -> {
					JsonObject jwspId = b.toJsonObject();  
					String nodeUUID = jwspId.getString("nodeUUID");
					NodeId nodeId = new NodeId(nodeUUID);
					WorkspaceId wspId = new WorkspaceId(wspName, nodeId);
					proxy.init(ws, wspId, eventListener);
				  	ev.release();
				  });
				   
				  ws.writeTextMessage(params.encode());
				  
				  /*
					ICartagoNodeRemote env = (ICartagoNodeRemote)Naming.lookup("rmi://"+fullAddress+"/cartago_node");
					CartagoCallbackRemote srv = new CartagoCallbackRemote(eventListener);
					CartagoCallbackProxy proxy = new CartagoCallbackProxy(srv);
					System.out.println("Looking for "+"rmi://"+address+"/cartago_node");
					ICartagoContext ctx = env.join(wspName, cred, proxy);
					remoteCtxs.add((AgentBodyProxy)ctx);
					*/
			}, err -> {
				  System.out.println("Error!");
				  error = true;
				  ev.release();
			
			});

			ev.acquire();
			if (!error) {
				return proxy;
			} else {
				throw new CartagoInfrastructureLayerException();
			}
			
		} catch (Exception ex){
			ex.printStackTrace();
			throw new CartagoInfrastructureLayerException();
		}
		
		// throw new RuntimeException("not implemented");

	}

	public OpId execRemoteInterArtifactOp(ICartagoCallback callback, long callbackId,
			AgentId userId, ArtifactId srcId, ArtifactId targetId, String address, Op op,
			long timeout, IAlignmentTest test)
			throws CartagoInfrastructureLayerException, CartagoException {
		/* try {
			CartagoCallbackRemote srv = new CartagoCallbackRemote(callback);
			CartagoCallbackProxy proxy = new CartagoCallbackProxy(srv);
			String fullAddress = address;
			if (getPort(address)==-1){
				fullAddress = address+":"+DEFAULT_PORT;
			}
			ICartagoNodeRemote env = (ICartagoNodeRemote)Naming.lookup("rmi://"+fullAddress+"/cartago_node");
			return env.execInterArtifactOp(proxy, callbackId, userId, srcId, targetId, op, timeout, test);
		} catch (Exception ex){
			ex.printStackTrace();
			throw new CartagoException("Inter-artifact op failed: "+ex.getLocalizedMessage());
		}*/
		throw new RuntimeException("not implemented");
	}


	public NodeId getNodeAt(String address) throws CartagoInfrastructureLayerException, CartagoException {
		/*
		try {
			String fullAddress = address;
			if (getPort(address)==-1){
				fullAddress = address+":"+DEFAULT_PORT;
			}
			ICartagoNodeRemote env = (ICartagoNodeRemote)Naming.lookup("rmi://"+fullAddress+"/cartago_node");
			return env.getNodeId();		
		} catch (RemoteException ex) {
			ex.printStackTrace();
			throw new CartagoInfrastructureLayerException();
		} catch (NotBoundException ex) {
			ex.printStackTrace();
			throw new CartagoInfrastructureLayerException();
		} catch (MalformedURLException ex){
			ex.printStackTrace();
			throw new CartagoInfrastructureLayerException();
		}	*/
		throw new RuntimeException("not implemented");

	}

	//
	
	public void registerLoggerToRemoteWsp(String wspName, String address, ICartagoLogger logger) throws CartagoException {
		/*
		try {
			String fullAddress = address;
			if (getPort(address)==-1){
				fullAddress = address+":"+DEFAULT_PORT;
			}
			ICartagoNodeRemote env = (ICartagoNodeRemote)Naming.lookup("rmi://"+fullAddress+"/cartago_node");
			CartagoLoggerRemote srv = new CartagoLoggerRemote(logger);
			CartagoLoggerProxy proxy = new CartagoLoggerProxy(srv);
			env.registerLogger(wspName, proxy);
		} catch (RemoteException ex) {
			ex.printStackTrace();
			throw new CartagoInfrastructureLayerException();
		} catch (NotBoundException ex) {
			ex.printStackTrace();
			throw new CartagoInfrastructureLayerException();
		} catch (MalformedURLException ex){
			ex.printStackTrace();
			throw new CartagoInfrastructureLayerException();
		}*/
		throw new RuntimeException("not implemented");
		
	}
	
	//
	
	public void startService(CartagoNode node, String address) throws CartagoInfrastructureLayerException {
		
		if (service != null){
			throw new CartagoInfrastructureLayerException();
		}
		try {
			int port = DEFAULT_PORT;
			service = new CartagoNodeService(node);
			if (address == null || address.equals("")){
				address = InetAddress.getLocalHost().getHostAddress();
			} else {
				int port1 = getPort(address);
				if (port1 != -1){
					port = port1;
					address = address.substring(0, address.indexOf(':'));
				}
			}
			service.install(address,port);
		} catch (Exception ex){
			ex.printStackTrace();
			throw new CartagoInfrastructureLayerException();
		}
	}

	public void shutdownService() throws CartagoException {
		
		if (service != null){
			service.shutdownService();
			service = null;
		}
	}

	public boolean isServiceRunning() {
		return service != null;
	}

	
	private static int getPort(String address){
		int index = address.indexOf(":");
		if (index != -1){
			String snum = address.substring(index+1);
			return Integer.parseInt(snum);
		} else {
			return -1;
		}
		
	}
	

}
