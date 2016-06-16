package cartago.infrastructure.rmi;

import java.io.*;
import java.rmi.RemoteException;

import cartago.*;

public class CartagoLoggerProxy implements ICartagoLogger, Serializable {

	private ICartagoLoggerRemote logger;
	
	CartagoLoggerProxy(ICartagoLoggerRemote logger){
		this.logger = logger;
	}
	
	@Override
	public void agentJoined(long when, AgentId id) throws CartagoException {
		try {
			logger.agentJoined(when, id);
		} catch (RemoteException ex) {
			throw new CartagoException(ex.getMessage());
		}
	}

	@Override
	public void agentQuit(long when, AgentId id) throws CartagoException {
		try {
			logger.agentQuit(when, id);
		} catch (RemoteException ex) {
			throw new CartagoException(ex.getMessage());
		}
		
	}

	@Override
	public void artifactCreated(long when, ArtifactId id, AgentId creator) throws CartagoException  {
		try {
			logger.artifactCreated(when, id, creator);
		} catch (RemoteException ex) {
			throw new CartagoException(ex.getMessage());
		}
	}

	@Override
	public void artifactDisposed(long when, ArtifactId id, AgentId disposer) throws CartagoException  {
		try {
			logger.artifactDisposed(when, id, disposer);
		} catch (RemoteException ex) {
			throw new CartagoException(ex.getMessage());
		}
	}

	@Override
	public void artifactFocussed(long when, AgentId who, ArtifactId id,
			IEventFilter ev) throws CartagoException  {
		try {
			logger.artifactFocussed(when, who, id, ev);
		} catch (RemoteException ex) {
			throw new CartagoException(ex.getMessage());
		}
	}

	@Override
	public void artifactNoMoreFocussed(long when, AgentId who, ArtifactId id) throws CartagoException  {
		try {
			logger.artifactNoMoreFocussed(when, who, id);
		} catch (RemoteException ex) {
			throw new CartagoException(ex.getMessage());
		}
	}

	@Override
	public void artifactsLinked(long when, AgentId id, ArtifactId linking,
			ArtifactId linked) throws CartagoException  {
		try {
			logger.artifactsLinked(when, id, linking, linked);
		} catch (RemoteException ex) {
			throw new CartagoException(ex.getMessage());
		}
	}

	@Override
	public void newPercept(long when, ArtifactId aid, Tuple signal,
			ArtifactObsProperty[] added, ArtifactObsProperty[] removed,
			ArtifactObsProperty[] changed)throws CartagoException  {
		try {
			logger.newPercept(when, aid, signal, added, removed, changed);
		} catch (RemoteException ex) {
			throw new CartagoException(ex.getMessage());
		}
	}

	@Override
	public void opCompleted(long when, OpId id, ArtifactId aid, Op op) throws CartagoException  {
		try {
			logger.opCompleted(when, id, aid, op);
		} catch (RemoteException ex) {
			throw new CartagoException(ex.getMessage());
		}
	}

	@Override
	public void opFailed(long when, OpId id, ArtifactId aid, Op op, String msg,
			Tuple descr) throws CartagoException  {
		try {
			logger.opFailed(when, id, aid, op, msg, descr);
		} catch (RemoteException ex) {
			throw new CartagoException(ex.getMessage());
		}
	}

	@Override
	public void opRequested(long when, AgentId who, ArtifactId aid, Op op) throws CartagoException {
		try {
			logger.opRequested(when, who, aid, op);
		} catch (RemoteException ex) {
			throw new CartagoException(ex.getMessage());
		}
	}

	@Override
	public void opResumed(long when, OpId id, ArtifactId aid, Op op) throws CartagoException  {
		try {
			logger.opResumed(when, id, aid, op);
		} catch (RemoteException ex) {
			throw new CartagoException(ex.getMessage());
		}
	}

	@Override
	public void opStarted(long when, OpId id, ArtifactId aid, Op op) throws CartagoException  {
		try {
			logger.opStarted(when, id, aid, op);
		} catch (RemoteException ex) {
			throw new CartagoException(ex.getMessage());
		}
	}

	@Override
	public void opSuspended(long when, OpId id, ArtifactId aid, Op op) throws CartagoException  {
		try {
			logger.opSuspended(when, id, aid, op);
		} catch (RemoteException ex) {
			throw new CartagoException(ex.getMessage());
		}
	}

}
