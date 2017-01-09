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
package cartago.infrastructure.rmi;

import java.rmi.*;
import java.net.*;
import java.util.concurrent.ConcurrentLinkedQueue;

import cartago.*;
import cartago.infrastructure.*;
import cartago.security.*;
import java.util.Enumeration;

/**
 * CArtAgO RMI Infrastructure Service - enables remote interaction exploiting RMI transport protocol.
 *  
 * @author aricci
 *
 */
public class CartagoInfrastructureLayer implements ICartagoInfrastructureLayer {
	
	private KeepRemoteBodyAliveManagerAgent keepAliveAgent;
	private ConcurrentLinkedQueue<AgentBodyProxy> remoteCtxs;
	static public final int DEFAULT_PORT = 20100; 
	private CartagoNodeRemote service;
	
	public CartagoInfrastructureLayer(){
		remoteCtxs = new ConcurrentLinkedQueue<AgentBodyProxy>();
		keepAliveAgent = new KeepRemoteBodyAliveManagerAgent(remoteCtxs,5000);
		keepAliveAgent.start();
	}
		
	public void shutdownLayer() throws CartagoException {
		keepAliveAgent.shutdown();
	}

	public ICartagoContext joinRemoteWorkspace(String wspName, String address, AgentCredential cred, ICartagoCallback eventListener) throws CartagoInfrastructureLayerException, CartagoException {
		try {
			String fullAddress = address;
			if (getPort(address)==-1){
				fullAddress = address+":"+DEFAULT_PORT;
			}
			ICartagoNodeRemote env = (ICartagoNodeRemote)Naming.lookup("rmi://"+fullAddress+"/cartago_node");
			CartagoCallbackRemote srv = new CartagoCallbackRemote(eventListener);
			CartagoCallbackProxy proxy = new CartagoCallbackProxy(srv);
			System.out.println("Looking for "+"rmi://"+address+"/cartago_node");
			ICartagoContext ctx = env.join(wspName, cred, proxy);
			remoteCtxs.add((AgentBodyProxy)ctx);
			return ctx;		
		} catch (RemoteException ex) {
			ex.printStackTrace();
			throw new CartagoInfrastructureLayerException();
		} catch (NotBoundException ex) {
			ex.printStackTrace();
			throw new CartagoInfrastructureLayerException();
		} catch (MalformedURLException ex){
			ex.printStackTrace();
			throw new CartagoInfrastructureLayerException();
		}
	}

	public OpId execRemoteInterArtifactOp(ICartagoCallback callback, long callbackId,
			AgentId userId, ArtifactId srcId, ArtifactId targetId, String address, Op op,
			long timeout, IAlignmentTest test)
			throws CartagoInfrastructureLayerException, CartagoException {
		try {
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
		}
	}


    public WorkspaceId getMainWorkspace(String address) throws CartagoInfrastructureLayerException, CartagoException
    {
	try {
	    String fullAddress = address;
	    if (getPort(address)==-1){
		fullAddress = address+":"+DEFAULT_PORT;
	    }
	    ICartagoNodeRemote env = (ICartagoNodeRemote)Naming.lookup("rmi://"+fullAddress+"/cartago_node");
	    return env.getMainWorkspaceId();		
	} catch (RemoteException ex) {
	    ex.printStackTrace();
	    throw new CartagoInfrastructureLayerException();
	} catch (NotBoundException ex) {
	    ex.printStackTrace();
	    throw new CartagoInfrastructureLayerException();
	} catch (MalformedURLException ex){
	    ex.printStackTrace();
	    throw new CartagoInfrastructureLayerException();
	}		
    }
								    
    
	public NodeId getNodeAt(String address) throws CartagoInfrastructureLayerException, CartagoException {
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
		}		
	}

	//
	
	public void registerLoggerToRemoteWsp(String wspName, String address, ICartagoLogger logger) throws CartagoException {
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
		}
		
	}
	
	//
	
	public void startService(CartagoNode node, String address) throws CartagoInfrastructureLayerException {
		if (service != null){
			throw new CartagoInfrastructureLayerException();
		}
		try {
			int port = DEFAULT_PORT;
			service = new CartagoNodeRemote(node);
			if (address == null || address.equals("")){
			    address = getLocalIP();
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

    @Override
    public void quitWorkspace(String address, String wspName, AgentId id) throws CartagoException
    {
	try
	    {
		ICartagoNodeRemote env = (ICartagoNodeRemote)Naming.lookup("rmi://"+address+"/cartago_node");
		env.quit(wspName, id);
	    }
	catch (RemoteException ex)
	    {
		ex.printStackTrace();
		throw new CartagoException("Remote problems in quitWorkspace");
	    }
	catch (NotBoundException ex)
	    {
		ex.printStackTrace();
		throw new CartagoInfrastructureLayerException();
	    }
	catch (MalformedURLException ex)
	    {
		ex.printStackTrace();
		throw new CartagoInfrastructureLayerException();
	    }
    }

        public static String getLocalIP()
    {
	try
	    {

		Enumeration e = NetworkInterface.getNetworkInterfaces();
		while(e.hasMoreElements())
		    {
		    	
		        NetworkInterface n = (NetworkInterface) e.nextElement();	       
		        Enumeration ee = n.getInetAddresses();
		        while (ee.hasMoreElements())
			    {
				InetAddress i = (InetAddress) ee.nextElement();
				if(i.isSiteLocalAddress())
				    {
					//System.out.println(i.getHostAddress());
					return (i.getHostAddress());		            
				    }
			    }
		    }
	    }
	catch(Exception e){e.printStackTrace();}
	return("localhost");
    }

}
