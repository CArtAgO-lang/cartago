
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
    private HashMap<NodeId, String> mapNodes; //to know which address corresponds to a cartago node

    


    public void mountRoot(NodeId nId, WorkspaceId wsid, String rootWspAddress) throws TopologyException
    {
	if(this.root != null)
	    throw new TopologyException("cannot mount root, already present");
	
	mapIds = new  HashMap<WorkspaceId, TreeNode>();
	this.root = new TreeNode(wsid, nId, rootWspAddress);
	mapIds.put(wsid, this.root);
	mapNodes.put(nId, rootWspAddress);
	    
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

	String[] parts = path.split(Utils.getSeparationTokenEscaped());
	TreeNode current = this.root;
	for(int i = 1; i < parts.length; i++) //first part is root
	    {
		String n = parts[i];
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
		res += aux.getName() + Utils.getSeparationToken() + res;
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

     public WorkspaceId getPathId(String path) throws TopologyException
    {
	TreeNode node = getNodeFromPath(path);
	
	if(node == null)
	    throw new TopologyException("Invalid Path");
	return node.getWspId();
    }

    
    
    

    //returns the parent's path
    private String pathToParent(String path) throws TopologyException
    {
	String paPa = Utils.parentPath(path);
	if(paPa.equals("") && !this.root.getName().equals(path))
	    throw new TopologyException("Invalid path");
	
	return paPa;
    }


    //mounts the main workspace of a node
    public void mountNode(String path, WorkspaceId wsid, NodeId nId, String address)  throws TopologyException
    {

	String parentPath = pathToParent(path);


	
	if(root != null && parentPath.equals(""))
	    throw new TopologyException("Cannot mount on root path");
	
	    
	if(parentPath.equals(""))
	    {	   
		throw new TopologyException("Cannot mount on root");
	    }
	else
	    {
		
		TreeNode parent = getNodeFromPath(parentPath);

		if(parent == null)
		    throw new TopologyException("Invalid mount path");
		
		String newName = Utils.createSimpleName(path);

		TreeNode newNode = new TreeNode(wsid, nId, address); //

		parent.addChild(newNode);

		mapIds.put(wsid, newNode);

		mapNodes.put(nId, address);
	    }
	    
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
		String newName = Utils.createSimpleName(path);

		TreeNode newNode = new TreeNode(wsid, parent.getNodeId(), parent.getAddress()); 

		parent.addChild(newNode);

		mapIds.put(wsid, newNode);

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

    private void printTree(TreeNode current, int spaces)
    {
	for(int i = 0; i < spaces*3; i++)
	    {
		System.out.print(" ");
	    }
	System.out.println("| " + current.getName());
	for(TreeNode aux : current.getChildren().values())
	    {
		printTree(aux, spaces+1);
	    }
    }

    public void printTree()
    {
	printTree(this.root, 0);
    }
    
}
