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

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import cartago.*;
import java.util.*;

public class CartagoControllerRemote extends UnicastRemoteObject implements ICartagoControllerRemote {

	private ICartagoController contr;
	
	public CartagoControllerRemote() throws RemoteException {
	}

	public CartagoControllerRemote(ICartagoController c) throws RemoteException {
		contr = c;
	}
	
	public ArtifactId[] getCurrentArtifacts() throws CartagoException,
			RemoteException {
		return contr.getCurrentArtifacts();
	}

	public AgentId[] getCurrentAgents() throws CartagoException, RemoteException {
		return contr.getCurrentAgents();
	}

	public boolean removeArtifact(String artifactName) throws CartagoException,
			RemoteException {
		return contr.removeArtifact(artifactName);
	}

	public boolean removeAgent(String userName) throws CartagoException,
			RemoteException {
		return contr.removeAgent(userName);
	}

	public ArtifactInfo getArtifactInfo(String name) throws CartagoException {
		return contr.getArtifactInfo(name);
	}
		
}
