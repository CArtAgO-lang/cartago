
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
Class that represents a node for the tree topology

*/

import java.util.HashMap;
import cartago.WorkspaceId;
import cartago.NodeId;

public class TreeNode implements java.io.Serializable
{
    private String name;
    private WorkspaceId wspId;
    private TreeNode parent;
    private HashMap<String, TreeNode> children;
    private String address;
    private NodeId nId;

    public TreeNode(WorkspaceId wspId, NodeId nId,  String address)
    {
	this.name = wspId.getName();
	this.address = address;
	this.wspId = wspId;
	this.nId = nId;
	this.parent = null;
	this.children = new HashMap<String, TreeNode>();
    }

    public String getAddress()
    {
	return this.address;
    }
    
    public String getName()
    {
	return this.name;
    }

    public WorkspaceId getWspId()
    {
	return this.wspId;
    }
    
    public TreeNode getParent()
    {
	return this.parent;
    }

    public NodeId getNodeId()
    {
	return this.nId;
    }

    public HashMap<String, TreeNode> getChildren()
    {
	return this.children;
    }

    public void addChild(TreeNode node)
    {
	this.children.put(node.getName(), node);
	node.setParent(this);
    }

    public void removeChild(String name)
    {
	this.children.remove(name);
    }

    public void setParent(TreeNode parent)
    {
	this.parent = parent;
    }

    public void setAddress(String address)
    {
	this.address = address;
    }
	
}
