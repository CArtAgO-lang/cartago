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
import cartago.CartagoException;
import cartago.CartagoNode;
import cartago.CartagoWorkspace;
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
	private CartagoNode mNode;
	private ConcurrentLinkedQueue<AgentBodyRemote> mRemoteCtxs;
	private GarbageBodyCollectorAgent mGarbageCollector;
	private CallHandler mCallHandler;
	private Server mServer;
	
	public CartagoNodeRemote(CartagoNode node) {
		mNode = node;
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
		CartagoWorkspace wsp = mNode.getWorkspace(wspName);
		return wsp.execInterArtifactOp(proxy, callbackId, userId, srcId, targetId, op, timeout, test);
	}

	@Override
	public String getVersion() throws CartagoException{
		return CARTAGO_VERSION.getID();
	}

	@Override
	public IAgentBodyRemote join(String wspName, AgentCredential cred,
			ICartagoCallbackRemote callback) throws CartagoException {
		
		CartagoWorkspace wsp = mNode.getWorkspace(wspName);
		ICartagoCallback proxy = new CartagoCallbackProxy(callback);
		ICartagoContext ctx = wsp.join(cred,proxy);
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
		
		CartagoWorkspace wsp = mNode.getWorkspace(wspName);
		wsp.getKernel().quitAgent(id);
		Iterator<AgentBodyRemote> it = mRemoteCtxs.iterator();
		while (it.hasNext()){
			AgentBodyRemote c = it.next();
			if (c.getAgentId().equals(id)){
				it.remove();
				break;
			}
		}
	}

	@Override
	public NodeId getNodeId() throws CartagoException {
		return mNode.getId();
	}

}
