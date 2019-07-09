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

import java.net.*;
import java.util.UUID;
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
import io.vertx.ext.web.client.HttpResponse;
import io.vertx.ext.web.client.WebClient;


/**
 * CArtAgO RMI Infrastructure Service - enables remote interaction exploiting RMI transport protocol.
 *  
 * @author aricci
 *
 */
public class CartagoInfrastructureLayer implements ICartagoInfrastructureLayer {
	
	static public final int DEFAULT_PORT = 20100; 
	static public final int DAEMON_PORT = 10000; 
	private CartagoEnvironmentService service;
	private Vertx vertx;
	private boolean error = false;
	
	public CartagoInfrastructureLayer(){
		vertx = Vertx.vertx();
	}
		
	public void shutdownLayer() throws CartagoException {
		this.shutdownService();
	}

	public ICartagoContext joinRemoteWorkspace(String envName, String address, String wspFullNameRemote, AgentCredential cred, ICartagoCallback eventListener, String wspNameLocal) throws CartagoInfrastructureLayerException, CartagoException {
		
		try {
			
			String host = getHost(address);
			int port = getPort(address);
			if (port == -1) {
				port = DEFAULT_PORT;
			}

			HttpClientOptions options = new HttpClientOptions().setDefaultHost(host).setDefaultPort(port);
			HttpClient client = vertx.createHttpClient(options);
			
			AgentBodyProxy proxy = new AgentBodyProxy(vertx, port);
			
			Semaphore ev = new Semaphore(0);
			error = false;
			
			client.websocket("/cartago/api/join", (WebSocket ws) -> {
				  
				  JsonObject params = new JsonObject();
				  params.put("wspFullName", wspFullNameRemote);
				  JsonObject ac = new JsonObject();
				  ac.put("userName", cred.getId());
				  ac.put("roleName", cred.getRoleName());				  
				  params.put("agent-cred", ac);
				  
				  ws.handler((Buffer b) -> {
					  try {
						JsonObject msg = b.toJsonObject();  
						String wspUUID = msg.getString("wspUUID");
						WorkspaceId wspId = new WorkspaceId(wspNameLocal, UUID.fromString(wspUUID));						
						proxy.init(address, ws, wspId, eventListener);
					  } catch (Exception ex) { 
						ex.printStackTrace();  
					  } finally {
					  	ev.release();
					  }
				  });
				   
				  ws.writeTextMessage(params.encode());
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
	
	/**
	 * remote path: <host>{:<port>}/<masName>/<wspPath>
	 */
	public WorkspaceDescriptor resolveRemoteWSP(String remotePath) throws WorkspaceNotFoundException {
		int index1 = remotePath.indexOf("//");
		String address = null;
		String fullName = null;
		int index2;
		if (index1 == -1) {
			index2 = remotePath.indexOf('/');
			address = remotePath.substring(0, index2);
			fullName = remotePath.substring(index2 + 1);
		} else {
			String withoutPref = remotePath.substring(index1 + 2);
			String pref = remotePath.substring(0, index1 + 2);
			index2 = withoutPref.indexOf('/');
			address = withoutPref.substring(0,index2);
			fullName = withoutPref.substring(index2 + 1);
		}
		int index3 = fullName.indexOf('/');
		String envName = fullName.substring(0, index3);
		String wspPath = fullName.substring(index3);
		return this.resolveRemoteWSP(wspPath, address, envName);
	}	
	
	public WorkspaceDescriptor createRemoteWorkspace(String fullWspName, String address, String envName) throws CartagoException {
		try {
			
			String addr = address;
			if (addr.startsWith("http://")) {
				addr = addr.substring(7);
			}
			
			String host = getHost(addr);
			int port = getPort(addr);
			if (port == -1) {
				port = DEFAULT_PORT;
			}

			Semaphore ev = new Semaphore(0);
			
			Holder<WorkspaceDescriptor> result = new Holder<WorkspaceDescriptor>();
			
			WebClient client = WebClient.create(vertx);
			// String uri = "/" + envName + fullPath;
			String uri = "/cartago/api/envs"; 
			
			int index = fullWspName.lastIndexOf("/");
			String rootWspName = fullWspName.substring(index + 1);
			
			JsonObject msg = new JsonObject();
			msg.put("wspRootName", rootWspName);
			msg.put("envName", CartagoEnvironment.getInstance().getName());
			msg.put("envPort", port);
			
			Buffer buffer = msg.toBuffer();

			client
			  .post(22000, host, uri)
			  .sendBuffer(buffer, ar -> {
			    try {
					if (ar.succeeded()) {
				      HttpResponse<Buffer> response = ar.result();
				      // System.out.println("Received response with status code" + response.statusCode());
				      JsonObject ws = response.bodyAsJsonObject();
				      // GlobalWorkspaceInfo info = JsonUtil.toGlobalWorkspaceInfo(ws);
				      // String envName = ws.getString("envName");
				      String envId = ws.getString("envId");
				      UUID uuid = UUID.fromString(envId);
				      JsonObject id = ws.getJsonObject("wspId");
				      WorkspaceId wid = JsonUtil.toWorkspaceId(id);
				      WorkspaceDescriptor des = new WorkspaceDescriptor(envName, uuid, wid, fullWspName, address, "web");
				      result.set(des);	
				    } else {
				      System.out.println("Something went wrong " + ar.cause().getMessage());
				    }
			    } catch (Exception ex) {
			    	ex.printStackTrace();
			    } finally {
				    ev.release();
			    }
			  });
			
			try {
				ev.acquire();
			} catch(Exception ex) {
				ex.printStackTrace();
			}
			
			if (result.isPresent()) {
				return result.getValue();
			} else {
				throw new WorkspaceNotFoundException();
			}
		} catch (Exception ex) {
			throw new WorkspaceNotFoundException();
		}		
	}

	public WorkspaceDescriptor resolveRemoteWSP(String fullPath, String address, String masName) throws WorkspaceNotFoundException {
		try {
			
			String host = getHost(address);
			int port = getPort(address);
			if (port == -1) {
				port = DEFAULT_PORT;
			}

			Semaphore ev = new Semaphore(0);
			
			Holder<WorkspaceDescriptor> result = new Holder<WorkspaceDescriptor>();
			
			WebClient client = WebClient.create(vertx);
			// String uri = "/" + envName + fullPath;
			String uri = "/cartago/api/" + masName; 
			
			client
			  .get(port, host, uri)
			  .addQueryParam("wsp", fullPath)
			  .send(ar -> {
			    try {
					if (ar.succeeded()) {
				      HttpResponse<Buffer> response = ar.result();
				      // System.out.println("Received response with status code" + response.statusCode());
				      JsonObject ws = response.bodyAsJsonObject();
				      // GlobalWorkspaceInfo info = JsonUtil.toGlobalWorkspaceInfo(ws);
				      String envName = ws.getString("envName");
				      String envId = ws.getString("envId");
				      UUID uuid = UUID.fromString(envId);
				      JsonObject id = ws.getJsonObject("id");
				      WorkspaceDescriptor des = null;
				      if (id != null) {
				    	  /* local for the remote => remote for this node */
				    	  WorkspaceId wid = JsonUtil.toWorkspaceId(id);
				    	  des = new WorkspaceDescriptor(envName, uuid, wid, fullPath, address, "web");
				      } else {
				    	  String remotePath = ws.getString("remotePath");
				    	  String addr = ws.getString("address");
				    	  String protocol = ws.getString("protocol");
				    	  des = new WorkspaceDescriptor(envName, uuid, null, remotePath, addr, protocol);
				      }
			    	  result.set(des);	
				    } else {
				      System.out.println("Something went wrong " + ar.cause().getMessage());
				    }
			    } catch (Exception ex) {
			    	ex.printStackTrace();
			    } finally {
				    ev.release();
			    }
			  });
			
			try {
				ev.acquire();
			} catch(Exception ex) {
				ex.printStackTrace();
			}
			
			if (result.isPresent()) {
				return result.getValue();
			} else {
				throw new WorkspaceNotFoundException();
			}
		} catch (Exception ex) {
			throw new WorkspaceNotFoundException();
		}
	}

	
	public void spawnNode(String address, String masName, UUID envId, String rootWspName) {
		try {
			
			String host = getHost(address);
			int port = getPort(address);
			if (port == -1) {
				port = DEFAULT_PORT;
			}
			
			WebClient client = WebClient.create(vertx);
			String uri = "/cartago/api/envs"; 
			
			JsonObject msg = new JsonObject();
			msg.put("envName", masName);
			msg.put("envPort", port);
			msg.put("wspRootName", rootWspName);
			Buffer buffer = msg.toBuffer(); 
				
			Semaphore done = new Semaphore(0);
			
			client
			  .post(DAEMON_PORT, host, uri)
			  .sendBuffer(buffer, ar -> {
			    try {
					if (ar.succeeded()) {
				      HttpResponse<Buffer> response = ar.result();
				      System.out.println("Spawn succeeded: " + masName + " at " + address);
				    } else {
				      System.out.println("Something went wrong " + ar.cause().getMessage());
				    }
			    } catch (Exception ex) {
			    	ex.printStackTrace();
			    } finally {
			    	done.release();
			    }
			  });
			
			  done.acquire();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
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
	
	public void startService(String address) throws CartagoInfrastructureLayerException {
		
		if (service != null){
			throw new CartagoInfrastructureLayerException();
		}
		try {
			int port = DEFAULT_PORT;
			service = new CartagoEnvironmentService();
			if (address == null || address.equals("")){
				address = InetAddress.getLocalHost().getHostAddress();
			} else {
				int port1 = getPort(address);
				if (port1 != -1){
					port = port1;
					address = address.substring(0, address.indexOf(':'));
				}
			}
			service.install(address, port);
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
	
	private static String getHost(String address){
		int index = address.indexOf(":");
		if (index != -1){
			return address.substring(0,index);
		} else {
			return address;
		}
		
	}

}
