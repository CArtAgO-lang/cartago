package cartago.infrastructure.rmi;

import java.rmi.*;
import java.rmi.server.UnicastRemoteObject;
import cartago.*;
import cartago.security.SecurityException;

public class CartagoLoggerRemote extends UnicastRemoteObject implements ICartagoLoggerRemote {

	private ICartagoLogger logger;
    /** Creates a new instance of CartagoRemoteContext */
    public CartagoLoggerRemote(ICartagoLogger logger) throws SecurityException, RemoteException  {
        super();  
        this.logger = logger;
    }

    @Override
	public void agentJoined(long when, AgentId id) throws RemoteException,
			CartagoException {
    	logger.agentJoined(when, id);
	}

	@Override
	public void agentQuit(long when, AgentId id) throws RemoteException,
			CartagoException {
		logger.agentQuit(when, id);
	}

	@Override
	public void artifactCreated(long when, ArtifactId id, AgentId creator)
			throws RemoteException, CartagoException {
		logger.artifactCreated(when, id, creator);
	}

	@Override
	public void artifactDisposed(long when, ArtifactId id, AgentId disposer)
			throws RemoteException, CartagoException {
		logger.artifactDisposed(when, id, disposer);
	}

	@Override
	public void artifactFocussed(long when, AgentId who, ArtifactId id,
			IEventFilter ev) throws RemoteException, CartagoException {
		logger.artifactFocussed(when, who, id, ev);
	}

	@Override
	public void artifactNoMoreFocussed(long when, AgentId who, ArtifactId id)
			throws RemoteException, CartagoException {

		logger.artifactNoMoreFocussed(when, who, id);
	}

	@Override
	public void artifactsLinked(long when, AgentId id, ArtifactId linking,
			ArtifactId linked) throws RemoteException, CartagoException {
		logger.artifactsLinked(when, id, linking, linked);
	}

	@Override
	public void newPercept(long when, ArtifactId aid, Tuple signal,
			ArtifactObsProperty[] added, ArtifactObsProperty[] removed,
			ArtifactObsProperty[] changed) throws RemoteException,
			CartagoException {

		logger.newPercept(when, aid, signal, added, removed, changed);
	}

	@Override
	public void opCompleted(long when, OpId id, ArtifactId aid, Op op)
			throws RemoteException, CartagoException {
		logger.opCompleted(when, id, aid, op);
		
	}

	@Override
	public void opFailed(long when, OpId id, ArtifactId aid, Op op, String msg,
			Tuple descr) throws RemoteException, CartagoException {
		logger.opFailed(when, id, aid, op, msg, descr);
		
	}

	@Override
	public void opRequested(long when, AgentId who, ArtifactId aid, Op op)
			throws RemoteException, CartagoException {
		// TODO Auto-generated method stub
		logger.opRequested(when, who, aid, op);
	}

	@Override
	public void opResumed(long when, OpId id, ArtifactId aid, Op op)
			throws RemoteException, CartagoException {
		// TODO Auto-generated method stub
		logger.opResumed(when, id, aid, op);
	}

	@Override
	public void opStarted(long when, OpId id, ArtifactId aid, Op op)
			throws RemoteException, CartagoException {
		logger.opStarted(when, id, aid, op);
		
	}

	@Override
	public void opSuspended(long when, OpId id, ArtifactId aid, Op op)
			throws RemoteException, CartagoException {
		// TODO Auto-generated method stub
		logger.opSuspended(when, id, aid, op);
	}

}
