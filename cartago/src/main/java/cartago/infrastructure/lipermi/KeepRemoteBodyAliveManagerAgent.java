package cartago.infrastructure.lipermi;

import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * 
 * @author mguidi
 *
 */
public class KeepRemoteBodyAliveManagerAgent extends Thread {
	
	private ConcurrentLinkedQueue<AgentBodyProxy> proxies;
	private boolean stopped;
	private long delay;
	
	public KeepRemoteBodyAliveManagerAgent(ConcurrentLinkedQueue<AgentBodyProxy> proxies, long delay){
		setName("KeepRemoteBodyAliveManagerAgentLipeRMI");
		stopped = false;
		this.proxies = proxies;
		this.delay = delay;
	}
	
	public void run(){
		while (!stopped){
			try {
				sleep(delay);
				Iterator<AgentBodyProxy> it = proxies.iterator();
				while (it.hasNext()){
					AgentBodyProxy ctx = it.next();
					try {
						ctx.ping();
					} catch (Exception ex){
						ex.printStackTrace();
						it.remove();
					}
				}
			} catch (InterruptedException e) {
				
			} catch (Exception ex){
				log("Error: "+ex);
			}
		}
	}
	
	public void shutdown(){
		interrupt();
		stopped = true;
		
//		Iterator<AgentBodyProxy> it = proxies.iterator();
//		while (it.hasNext()){
//			AgentBodyProxy ctx = it.next();
//			try {
//				ctx.close();
//			} catch (CartagoException e) {
//				e.printStackTrace();
//			}
//		}
	}
	
	private void log(String msg){
		System.err.println("[KEEP-ALIVE-MANAGER] "+msg);
	}
}
