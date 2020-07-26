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
package cartago.infrastructure.rmi;

import java.net.InetAddress;
import java.rmi.*;
import java.rmi.registry.*;
import java.rmi.server.UnicastRemoteObject;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.concurrent.*;
import cartago.*;


/**
 * Class representing a CArtAgO node service, serving remote requests
 *  
 * @author aricci
 *
 */
public class CartagoNodeRemote extends UnicastRemoteObject implements ICartagoNodeRemote {

	private String fullAddress;
	private CartagoEnvironment node;
	
	private ConcurrentLinkedQueue<AgentBodyRemote> remoteCtxs;
	private GarbageBodyCollectorAgent garbageCollector;
	
	public CartagoNodeRemote() throws Exception {
		remoteCtxs = new ConcurrentLinkedQueue<AgentBodyRemote>();	
		garbageCollector = new GarbageBodyCollectorAgent(remoteCtxs,1000,10000);
		garbageCollector.start();
	}	
		
	public String getAddress(){
		return fullAddress;
	}
	
	public void install(String address, int port) throws Exception {
		/* WARNING: the  timeout - 1000 - must be greater than the 
		   delay used by the KeepRemoteContextAliveManager
		   to keep alive the remote contexts */
		//
		LocateRegistry.createRegistry(port); 
		fullAddress = address+":"+port;
		Naming.bind("rmi://"+fullAddress+"/cartago_node", this);
		//System.out.println("CArtAgO RMI Service installed.");
	}	
		
	public void shutdownService(){
		garbageCollector.stopActivity();
		try {
			Naming.unbind("rmi://"+fullAddress+"/cartago_node");
		} catch (Exception ex){
			ex.printStackTrace();
		}
	}

	public void registerLogger(String wspName, ICartagoLogger logger) throws RemoteException, CartagoException{
		CartagoEnvironment.getInstance().registerLogger(wspName, logger);
	}
	
	/**
	 * Get a context to work inside the environment
	 * 
	 * @param cred user credential
	 * @return a context to play inside the workspace
	 *
	 */
	public ICartagoContext join(String wspName, AgentCredential cred, ICartagoCallback callback) throws cartago.security.SecurityException, RemoteException, CartagoException{
		Workspace wsp = node.resolveWSP(wspName).getWorkspace();
		//System.out.println("Remote request to join: "+wspName+" "+roleName+" "+cred+" "+callback);
		ICartagoContext ctx = wsp.joinWorkspace(cred,callback);
		AgentBodyRemote rctx = new AgentBodyRemote((AgentBody)ctx);
		remoteCtxs.add(rctx);
		AgentBodyProxy proxy = new AgentBodyProxy(rctx);
		return proxy;		
	}

	public void quit(String wspName, AgentId id) throws RemoteException, CartagoException {
		Workspace wsp = node.resolveWSP(wspName).getWorkspace();
		wsp.quitAgent(id);
		Iterator<AgentBodyRemote> it = remoteCtxs.iterator();
		while (it.hasNext()){
			AgentBodyRemote c = it.next();
			if (c.getAgentId().equals(id)){
				it.remove();
				break;
			}
		} 		
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
	 */
	public OpId execInterArtifactOp(ICartagoCallback callback, long callbackId, AgentId userId, ArtifactId srcId, ArtifactId targetId, Op op, long timeout, IAlignmentTest test) throws RemoteException, CartagoException {
		String wspName = targetId.getWorkspaceId().getName();
		Workspace wsp = node.resolveWSP(wspName).getWorkspace();
		return wsp.execInterArtifactOp(callback, callbackId, userId, srcId, targetId, op, timeout, test);
	}	 
	
	public String getVersion() throws CartagoException, RemoteException {
		return CARTAGO_VERSION.getID();
	}

	/*
	public String getNodeId() throws CartagoException, RemoteException {
		return env.();
	}
	*/
	

}
