
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
package cartago.topology;

/**
   Class that represents a tree that maintains the workspace topology

*/

import java.util.HashMap;
import cartago.WorkspaceId;
import cartago.CartagoNode;
import cartago.NodeId;
import cartago.CartagoWorkspace;
import cartago.CartagoException;
import java.util.Collection;
import java.util.ArrayList;

public class WorkspaceTree implements java.io.Serializable
{
    private TreeNode root;
    private HashMap<WorkspaceId, TreeNode> mapIds; //to easly retrive TreeNodes from workspaceIds   
    private HashMap<NodeId, String> mapNodes; //to know which address corresponds to a node

    
    //remote version
    public void mountRoot(CartagoNode context, String rootWspAddress) throws TopologyException
    {
	if(this.root != null)
	    throw new TopologyException("cannot mount root, already present");
	
	try
	    {
		mapIds = new  HashMap<WorkspaceId, TreeNode>();
		
		CartagoWorkspace wsp = context.createWorkspace("main");
		this.root = new TreeNode(wsp.getId(), context.getId(), rootWspAddress);
		mapIds.put(wsp.getId(), this.root);
	    }
	catch(CartagoException ex)
	    {
		ex.printStackTrace();
	    }
    }

    //local version
    public void mountRoot(WorkspaceId wsid, String rootWspAddress) throws TopologyException
    {
	if(this.root != null)
	    throw new TopologyException("cannot mount root, already present");
	
	mapIds = new  HashMap<WorkspaceId, TreeNode>();
	this.root = new TreeNode(wsid, null, "localhost");
	mapIds.put(wsid, this.root);
	    
    }

    public WorkspaceTree()
    {
	this.root = null;
	mapIds =  new  HashMap<WorkspaceId, TreeNode>();
	mapNodes = new  HashMap<NodeId, String>();
    }
    
    //checks if two workspaces are on the same CartagoNode
    public boolean inSameCartagoNode(String path1, String path2) throws TopologyException
    {
	TreeNode n1 = getNodeFromPath(path1);
	TreeNode n2 = getNodeFromPath(path2);
	if(n1 == null || n2 == null)
	    throw new TopologyException("Invalid path");

	return n1.getWspId().getNodeId().equals(n2.getWspId().getNodeId());
    }

    //returns address to locate node from a tree path
    public String getNodeAddressFromPath(String path) throws TopologyException
    {
	TreeNode tn = getNodeFromPath(path);
	if(tn == null)
	    throw new TopologyException("Invalid path");
	NodeId nId = tn.getNodeId();
	return mapNodes.get(nId);
	
    }
    //returns null if wrong path
    private TreeNode getNodeFromPath(String path)
    {
	String[] parts = path.split("/");
	TreeNode current = this.root;
	for(String n : parts)
	    {
		current = current.getChildren().get(n);
		if(current == null)
		    return null;
	    }
	return current;
    }

    //traverses parents to yield path
    private String retrievePath(TreeNode node)
    {
	String res = node.getName();
	TreeNode aux = node;
	while(aux.getParent() != null)
	    {		
		aux = aux.getParent();
		res += aux.getName() + "/" + res;
	    }
	return res;
    }

    public String getIdPath(WorkspaceId id) throws TopologyException
    {
	TreeNode node = this.mapIds.get(id);
	if(node == null)
	    throw new TopologyException("Invalid WorkspaceId");
	return retrievePath(node);
    }

    //returns the parent's path
    private String pathToParent(String path) throws TopologyException
    {
	if(!path.contains("/")) //is root or invalid
	    {
		if(this.root.getName().equals(path))
		    return ""; //no parent
		throw new TopologyException("Invalid path");
	    }
	return path.substring(0, path.lastIndexOf("/"));
    }



    
    public void mount(String path, WorkspaceId wsid) throws TopologyException
    {
	String parentPath = pathToParent(path);
	if(root != null && parentPath.equals(""))
	    throw new TopologyException("Cannot mount on root path");

	    
	if(parentPath.equals("")) //crea
	    {	   
		throw new TopologyException("Cannot mount on root");
	    }
	else
	    {
		TreeNode parent = getNodeFromPath(parentPath);
		String newName = path.substring(path.lastIndexOf("/")+1);

		TreeNode newNode = new TreeNode(wsid, parent.getNodeId(), "localhost"); //

		//more code to create a conection with the node corresponding to address so the workspace is created there

		parent.addChild(newNode);

	    }
    }

	public String getIdAddress(WorkspaceId id)
	{
	    TreeNode node = this.mapIds.get(id);
	    return node.getAddress();
	}

	public void setAddressRoot(String address)
	{
	    this.root.setAddress(address);
	}

    public Collection<String> getNodesAddresses()
    {
	ArrayList<String> res = new ArrayList<String>();

	for(NodeId aux : mapNodes.keySet())
	    {
		res.add(mapNodes.get(aux));
	    }
	return res;
    }
}
