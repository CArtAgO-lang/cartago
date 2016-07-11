package cartago.infrastructure.rmi;

import java.rmi.RemoteException;

import cartago.*;
import java.util.*;

public class CartagoControllerProxy implements ICartagoController, java.io.Serializable {

	private ICartagoControllerRemote ctx;

	CartagoControllerProxy(ICartagoControllerRemote ctx) throws CartagoException {
		this.ctx = ctx;
	}
	
	public ArtifactId[] getCurrentArtifacts() throws CartagoException {
		try {
			return ctx.getCurrentArtifacts();
		} catch (RemoteException ex) {
			throw new CartagoException(ex.getMessage());
		}
	}

	public AgentId[] getCurrentAgents() throws CartagoException {
		try {
			return ctx.getCurrentAgents();
		} catch (RemoteException ex) {
			throw new CartagoException(ex.getMessage());
		}
	}

	public boolean removeArtifact(String artifactName) throws CartagoException {
		try {
			 return ctx.removeArtifact(artifactName);
		} catch (RemoteException ex) {
			throw new CartagoException(ex.getMessage());
		}
	}

	public boolean removeAgent(String id) throws CartagoException {
		try {
			return ctx.removeAgent(id);
		} catch (RemoteException ex) {
			throw new CartagoException(ex.getMessage());
		}
	}

	public ArtifactInfo getArtifactInfo(String name) throws CartagoException {
		try {
			return ctx.getArtifactInfo(name);
		} catch (RemoteException ex) {
			throw new CartagoException(ex.getMessage());
		}
	}
}
