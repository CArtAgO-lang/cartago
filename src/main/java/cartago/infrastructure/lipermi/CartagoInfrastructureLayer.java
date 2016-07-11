package cartago.infrastructure.lipermi;

import java.io.IOException;
import java.util.concurrent.ConcurrentLinkedQueue;

import lipermi.exception.LipeRMIException;
import lipermi.handler.CallHandler;
import lipermi.net.Client;

import cartago.AgentId;
import cartago.ArtifactId;
import cartago.CartagoException;
import cartago.CartagoNode;
import cartago.IAlignmentTest;
import cartago.ICartagoCallback;
import cartago.ICartagoContext;
import cartago.ICartagoLogger;
import cartago.NodeId;
import cartago.Op;
import cartago.OpId;
import cartago.infrastructure.CartagoInfrastructureLayerException;
import cartago.infrastructure.ICartagoInfrastructureLayer;
import cartago.security.AgentCredential;

/**
 * 
 * @author mguidi
 *
 */
public class CartagoInfrastructureLayer implements ICartagoInfrastructureLayer {

	static public final int DEFAULT_PORT = 20101; 
	private KeepRemoteBodyAliveManagerAgent mKeepAliveAgent;
	private ConcurrentLinkedQueue<AgentBodyProxy> mRemoteCtxs;
	private CartagoNodeRemote mService;
			
	public CartagoInfrastructureLayer(){
		mRemoteCtxs = new ConcurrentLinkedQueue<AgentBodyProxy>();
		mKeepAliveAgent = new KeepRemoteBodyAliveManagerAgent(mRemoteCtxs,500);
		mKeepAliveAgent.start();
	}
	
	@Override
	public OpId execRemoteInterArtifactOp(ICartagoCallback callback,
			long callbackId, AgentId userId, ArtifactId srcId,
			ArtifactId targetId, String fullAddress, Op op, long timeout,
			IAlignmentTest test) throws CartagoInfrastructureLayerException,
			CartagoException {
		
		try {
			CallHandler callHandler = new CallHandler();
			ICartagoCallbackRemote srv = new CartagoCallbackRemote(callback, callHandler);
			
			String address = getAddress(fullAddress);
			int port = DEFAULT_PORT;
			int port1 = getPort(fullAddress);
			if (port1 != -1){
				port = port1;
			}
			Client client = new Client(address, port, callHandler);
			ICartagoNodeRemote env = (ICartagoNodeRemote) client.getGlobal(ICartagoNodeRemote.class);
			return env.execInterArtifactOp(srv, callbackId, userId, srcId, targetId, op, timeout, test);
			
		} catch (LipeRMIException e) {
			e.printStackTrace();
			throw new CartagoInfrastructureLayerException();
		} catch (IOException e) {
			e.printStackTrace();
			throw new CartagoInfrastructureLayerException();
		}
	}

	@Override
	public NodeId getNodeAt(String fullAddress)
			throws CartagoInfrastructureLayerException, CartagoException {
		
		try {
			CallHandler callHandler = new CallHandler();
			String address = getAddress(fullAddress);
			int port = DEFAULT_PORT;
			int port1 = getPort(fullAddress);
			if (port1 != -1){
				port = port1;
			}
			Client client = new Client(address, port, callHandler);
			ICartagoNodeRemote env = (ICartagoNodeRemote) client.getGlobal(ICartagoNodeRemote.class);
			NodeId nodeId = env.getNodeId();
			client.close();
			return nodeId;		
		} catch (IOException ex) {
			ex.printStackTrace();
			throw new CartagoInfrastructureLayerException();
		} 
	}

	@Override
	public boolean isServiceRunning() {
		return mService != null;
	}

	@Override
	public ICartagoContext joinRemoteWorkspace(String wspName, String fullAddress,
			AgentCredential cred, ICartagoCallback eventListener)
			throws CartagoInfrastructureLayerException, CartagoException {
		
		try {
			CallHandler callHandler = new CallHandler();
			ICartagoCallbackRemote srv = new CartagoCallbackRemote(eventListener, callHandler);
			
			String address = getAddress(fullAddress);
			int port = DEFAULT_PORT;
			int port1 = getPort(fullAddress);
			if (port1 != -1){
				port = port1;
			}
			Client client = new Client(address, port, callHandler);
			ICartagoNodeRemote env = (ICartagoNodeRemote) client.getGlobal(ICartagoNodeRemote.class);
			IAgentBodyRemote rctx = env.join(wspName, cred, srv);
			ICartagoContext ctx = new  AgentBodyProxy(rctx, client);
			mRemoteCtxs.add((AgentBodyProxy)ctx);
			return ctx;
		} catch (IOException ex) {
			//ex.printStackTrace();
			throw new CartagoInfrastructureLayerException();
		} catch (LipeRMIException e) {
			//e.printStackTrace();
			throw new CartagoInfrastructureLayerException();
		} 
	}

	@Override
	public void shutdownLayer() throws CartagoException {
		mKeepAliveAgent.shutdown();
	}

	@Override
	public void shutdownService() throws CartagoException {
		if (mService != null){
			mService.shutdownService();
			mService = null;
		}
	}

	@Override
	public void startService(CartagoNode node, String address)
			throws CartagoInfrastructureLayerException {
		
		if (mService != null){
			throw new CartagoInfrastructureLayerException();
		}
		try {
			int port = DEFAULT_PORT;
			mService = new CartagoNodeRemote(node);
			if (address!=null && !address.equals("")) {
				port = Integer.parseInt(address);
			}
			
			mService.install(port);
		} catch (Exception ex){
			ex.printStackTrace();
			throw new CartagoInfrastructureLayerException();
		}
	}
	
	public void registerLoggerToRemoteWsp(String wspName, String address, ICartagoLogger logger) throws CartagoException {
		throw new CartagoException("Feature not supported (TODO)");
	}
	
	private static String getAddress(String address) {
		int index = address.indexOf(":");
		if (index == -1) {
			return address;
		} else {
			return address.substring(0, index);
		}
	}
	
	private static int getPort(String address){
		int index = address.indexOf(":");
		if (index != -1){
			return Integer.parseInt(address.substring(index+1));
		} else {
			return -1;
		}
	}

}
