

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
package cartago.infrastructure.topology;

import cartago.topology.WorkspaceTree;
import cartago.topology.TopologyException;
import cartago.infrastructure.CartagoInfrastructureLayerException;
import cartago.WorkspaceId;
import cartago.NodeId;
import java.rmi.Remote;
import java.rmi.RemoteException;

/**
Interface to implement a central remote workspace tree

@autor xavier
 */



public interface ICartagoTreeRemote extends Remote
{
    public void mount(String wspPath, WorkspaceId wsId) throws TopologyException, RemoteException;
    public void mountNode(String wspPath, WorkspaceId wsId, NodeId nId, String address) throws TopologyException, RemoteException;
}
