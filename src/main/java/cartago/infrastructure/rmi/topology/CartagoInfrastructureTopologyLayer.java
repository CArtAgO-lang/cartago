


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
package cartago.infrastructure.rmi.topology;

import cartago.topology.WorkspaceTree;
import cartago.infrastructure.topology.ICartagoInfrastructureTopologyLayer;
import cartago.infrastructure.topology.ICartagoTreeRemote;
import cartago.topology.TopologyException;
import cartago.CartagoNode;
import cartago.infrastructure.rmi.ICartagoNodeRemote;
import cartago.infrastructure.CartagoInfrastructureLayerException;
import cartago.CartagoException;
import cartago.CartagoWorkspace;
import java.rmi.server.UnicastRemoteObject;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.NotBoundException;
import java.util.Collection;
import cartago.infrastructure.rmi.topology.CartagoTreeRemote;
import cartago.WorkspaceId;
import cartago.NodeId;


public class CartagoInfrastructureTopologyLayer implements ICartagoInfrastructureTopologyLayer
{

    private String centralNodeAddress; //to know where is the central node
    private CartagoTreeRemote service;

    public CartagoInfrastructureTopologyLayer(String centralNodeAddress)
    {
	this.centralNodeAddress = centralNodeAddress;
    }

    public CartagoInfrastructureTopologyLayer()
    {

    }


    //copies central tree to every node
    private void syncTrees(WorkspaceTree tCen) throws CartagoInfrastructureLayerException
    {
	Collection<String> adds = tCen.getNodesAddresses();
	for(String ad : adds)
	    {
		try
		    {
			ICartagoNodeRemote env = (ICartagoNodeRemote)Naming.lookup("rmi://"+ad+"/cartago_node");
			env.setTree(tCen);
			System.out.println("Tree updated");
		    }
		catch (RemoteException ex)
		    {
			ex.printStackTrace();
			throw new CartagoInfrastructureLayerException();
		    }
		catch (NotBoundException ex)
		    {
			ex.printStackTrace();
			throw new CartagoInfrastructureLayerException();
		    }
		catch (Exception ex)
		    {
			ex.printStackTrace();
			throw new CartagoInfrastructureLayerException();
		    }
				
	    }
    }
    
    public synchronized void mount(String wspPath) throws TopologyException, CartagoInfrastructureLayerException
    {
	//get parent path
	String pAux = wspPath;
	if(pAux.contains("/"))
	    pAux = pAux.substring(0, pAux.lastIndexOf("/"));
	//get address to locate remote node from parent		
	
	try
	    {

		ICartagoTreeRemote tr = (ICartagoTreeRemote)Naming.lookup("rmi://"+this.centralNodeAddress+"/tree");

		String rmiAddress = tr.getNodeAddressFromPath(pAux);
		
		ICartagoNodeRemote env = (ICartagoNodeRemote)Naming.lookup("rmi://"+rmiAddress+"/cartago_node");
		String newName = wspPath.substring(wspPath.lastIndexOf("/")+1);
		WorkspaceId wsp = env.createWorkspace(newName);

		//get WorkspaceTree
		tr.mount(wspPath, wsp);

		syncTrees(tr.getTree());
		

	    }
	catch (RemoteException ex)
	    {
		ex.printStackTrace();
		throw new CartagoInfrastructureLayerException();
	    }
	catch (NotBoundException ex)
	    {
		ex.printStackTrace();
		throw new CartagoInfrastructureLayerException();
	    }
	catch (Exception ex)
	    {
		ex.printStackTrace();
		throw new CartagoInfrastructureLayerException();
	    }

    }

    public void startTopologyService(String address, WorkspaceId wId, NodeId nId) throws CartagoInfrastructureLayerException
    {
	if(service != null)
	    {
		throw new CartagoInfrastructureLayerException();
	    }
	try
	    {
		WorkspaceTree tree = new WorkspaceTree();
		service = new CartagoTreeRemote(tree);
		service.installTree(address, wId, nId);
		syncTrees(tree); //add this tree to root node
	    }
	catch (Exception ex)
	    {
		ex.printStackTrace();
		throw new CartagoInfrastructureLayerException();
	    }
    }

    public boolean isTopologyServiceRunning()
    {
	return this.service != null;
    }

    public void shutdownService() 
    {
	if(this.service != null)
	    {
		this.service.shutdownService();
		this.service = null;
	    }

    }

    public String getCentralNodeAddress()
    {
	return this.centralNodeAddress;
    }

    public void setCentralNodeAddress(String centralNodeAddress)
    {
	this.centralNodeAddress = centralNodeAddress;
    }

    //the node shoud be alredy be created 
    public void mountNode(String wspPath, String address) throws TopologyException, CartagoInfrastructureLayerException
    {
	try
	    {
		ICartagoTreeRemote tr = (ICartagoTreeRemote)Naming.lookup("rmi://"+this.centralNodeAddress+"/tree");
		ICartagoNodeRemote env = (ICartagoNodeRemote)Naming.lookup("rmi://"+address+"/cartago_node");
		String newName = wspPath.substring(wspPath.lastIndexOf("/")+1);

		//workspace already created
		//CartagoWorkspace wsp = env.createWorkspace(newName);

		
		tr.mountNode(wspPath, env.getMainWorkspaceId(), env.getNodeId(), address);

		syncTrees(tr.getTree());
		


	    }
	catch (RemoteException ex)
	    {
		ex.printStackTrace();
		throw new CartagoInfrastructureLayerException();
	    }
	catch (NotBoundException ex)
	    {
		ex.printStackTrace();
		throw new CartagoInfrastructureLayerException();
	    }
	catch (Exception ex)
	    {
		ex.printStackTrace();
		throw new CartagoInfrastructureLayerException();
	    }
    }
    
}
