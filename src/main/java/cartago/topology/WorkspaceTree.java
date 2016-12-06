
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
import cartago.CartagoWorkspace;
import cartago.CartagoException;

public class WorkspaceTree implements java.io.Serializable
{
    private TreeNode root;
    private HashMap<WorkspaceId, TreeNode> mapIds; //to easly retrive TreeNodes from workspaceIds

    public WorkspaceTree(WorkspaceId wspId, String address)
    {
	this.root = new TreeNode(wspId, address);
	mapIds = new  HashMap<WorkspaceId, TreeNode>();
	mapIds.put(wspId, this.root);
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
    
    public void mount(String path, CartagoNode context) throws TopologyException
    {
	String parentPath = pathToParent(path);
	if(root != null && parentPath.equals(""))
	    throw new TopologyException("Cannot mount on root path");

	try{
	
	if(parentPath.equals("")) //create root, temporal imp
	    {	   
		CartagoWorkspace wsp = context.createWorkspace("main");
		this.root = new TreeNode(wsp.getId(), "localhost");
		mapIds.put(wsp.getId(), this.root);
	    }
	else
	    {
		TreeNode parent = getNodeFromPath(parentPath);
		String newName = path.substring(path.lastIndexOf("/")+1);

		CartagoWorkspace wsp = context.createWorkspace(newName);
		TreeNode newNode = new TreeNode(wsp.getId(), parent.getAddress());

		parent.addChild(newNode);
		mapIds.put(wsp.getId(), newNode);
	    }
	}
	catch(CartagoException ex)
	    {
		ex.printStackTrace();
	    }
    }

    public String getIdAddress(WorkspaceId id)
    {
	TreeNode node = this.mapIds.get(id);
	return node.getAddress();
    }


}
