package cartago.infrastructure.lipermi;

import java.io.IOException;
import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedQueue;

import lipermi.exception.LipeRMIException;
import lipermi.handler.CallHandler;
import lipermi.net.Server;

import cartago.AgentBody;
import cartago.AgentCredential;
import cartago.AgentId;
import cartago.ArtifactId;
import cartago.CARTAGO_VERSION;
import cartago.CartagoEnvironment;
import cartago.CartagoException;
import cartago.Workspace;
import cartago.IAlignmentTest;
import cartago.ICartagoCallback;
import cartago.ICartagoContext;
import cartago.NodeId;
import cartago.Op;
import cartago.OpId;

/**
 * Class representing a CArtAgO node service, serving remote requests
 * 
 * @author mguidi
 *
 */
public class CartagoNodeRemote implements ICartagoNodeRemote {
	
	private int mPort;
	private ConcurrentLinkedQueue<AgentBodyRemote> mRemoteCtxs;
	private GarbageBodyCollectorAgent mGarbageCollector;
	private CallHandler mCallHandler;
	private Server mServer;
	
	public CartagoNodeRemote() {
		mRemoteCtxs = new ConcurrentLinkedQueue<AgentBodyRemote>();
		mGarbageCollector = new GarbageBodyCollectorAgent(mRemoteCtxs,500,10000);
		mGarbageCollector.start();
	}
	
	public void install(int port) throws Exception {
		/* WARNING: the  timeout - 1000 - must be greater than the 
		   delay used by the KeepRemoteContextAliveManager
		   to keep alive the remote contexts */
		//
		mPort = port;
		mCallHandler = new CallHandler();
		mServer = new Server();
		
		try {
			mCallHandler.registerGlobal(ICartagoNodeRemote.class, this);
		} catch (LipeRMIException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {
			mServer.bind(mPort, mCallHandler);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println("CArtAgO LipeRMI Service installed on port: "+mPort);
	}
	
	public void shutdownService(){
		mGarbageCollector.stopActivity();
		mServer.close();
	}
	
	public int getPort(){
		return mPort;
	}
	
	@Override
	public OpId execInterArtifactOp(ICartagoCallbackRemote callback, long callbackId,
			AgentId userId, ArtifactId srcId, ArtifactId targetId, Op op,
			long timeout, IAlignmentTest test) throws CartagoException {
		
		ICartagoCallback proxy = new CartagoCallbackProxy(callback);
		String wspName = targetId.getWorkspaceId().getName();
		Workspace wsp = CartagoEnvironment.getInstance().resolveWSP(wspName).getWorkspace();
		return wsp.execInterArtifactOp(proxy, callbackId, userId, srcId, targetId, op, timeout, test);
	}

	@Override
	public String getVersion() throws CartagoException{
		return CARTAGO_VERSION.getID();
	}

	@Override
	public IAgentBodyRemote join(String wspName, AgentCredential cred,
			ICartagoCallbackRemote callback) throws CartagoException {
		
		Workspace wsp = CartagoEnvironment.getInstance().resolveWSP(wspName).getWorkspace();
		ICartagoCallback proxy = new CartagoCallbackProxy(callback);
		ICartagoContext ctx = wsp.joinWorkspace(cred,proxy);
		try {
			IAgentBodyRemote rctx = new AgentBodyRemote((AgentBody)ctx, mCallHandler);
			mRemoteCtxs.add((AgentBodyRemote) rctx);
			return rctx;
		} catch (LipeRMIException e) {
			e.printStackTrace();
			throw new CartagoException(e.getLocalizedMessage());
		}
	}

	@Override
	public void quit(String wspName, AgentId id) throws CartagoException {
		
		Workspace wsp = CartagoEnvironment.getInstance().resolveWSP(wspName).getWorkspace();
		wsp.quitAgent(id);
		Iterator<AgentBodyRemote> it = mRemoteCtxs.iterator();
		while (it.hasNext()){
			AgentBodyRemote c = it.next();
			if (c.getAgentId().equals(id)){
				it.remove();
				break;
			}
		}
	}

	/*
	public NodeId getNodeId() throws CartagoException {
		return mNode.getFullName();
	}*/

}
