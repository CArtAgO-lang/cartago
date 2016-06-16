package cartago.tools.inspector;

import cartago.*;

import java.util.*;
import java.util.concurrent.locks.*;

public class WorkspaceScene {

	ArrayList<AgentMarker> agents;
	ArrayList<ArtifactMarker> artifacts;
	HashMap<String,OpInfo> ongoingOp;
	LinkedList<FocussedArtifactInfo> focussedArt;
	Random rand; 
    Lock lock;
	
	int currentIndex = 0;
	P2d[] pos = new P2d[]{ new P2d(0.25,0), new P2d(0.25,0.25), new P2d(0,0.25), new P2d(-0.25,0.25), new P2d(-0.25,0),
			               new P2d(-0.25,-0.25), new P2d(0,-0.25), new P2d(0.25,-0.25)};
	P2d currentPos;

	public WorkspaceScene(){
		rand = new Random();
		agents = new ArrayList<AgentMarker>();
		artifacts = new ArrayList<ArtifactMarker>();
		ongoingOp = new HashMap<String,OpInfo>();
		focussedArt = new 	LinkedList<FocussedArtifactInfo>();
		lock = new ReentrantLock();
		currentPos = new P2d(0,0);
	}
	
	public void addAgent(AgentId id){
		try {
			lock.lock();
			P2d pos = nextPos();
			agents.add(new AgentMarker(id, pos));
		} finally {
			lock.unlock();
		}
	}
	
	public void removeAgent(AgentId id){
		try {
			lock.lock();
			agents.remove(id);
		} finally {
			lock.unlock();
		}
	}
	
	private P2d nextPos(){
		int index = currentIndex;
		currentIndex = (currentIndex+1) % pos.length;
		P2d p = pos[index].copy();
		p.scale(1.5);
		double x = rand.nextDouble()*0.1-0.05;
		double y = rand.nextDouble()*0.1-0.05;
		p.sum(new V2d(x,y));
		return p;
	}

	private P2d nextPosNear(P2d p0){
		/*
		int index = currentIndex;
		currentIndex = (currentIndex+1) % pos.length;
		P2d p = pos[index].copy();
		p.scale(0.25);
		*/
		P2d p = p0.copy();
		double x = rand.nextDouble()*0.25-0.125;
		double y = rand.nextDouble()*0.25-0.125;
		p.sum(new V2d(x,y));		
		return p;
	}
	
	
	public void addArtifact(ArtifactId id, AgentId creator){
		try {
			P2d pos = null;
			lock.lock();
			if (creator!=null){
				P2d posOwner = this.getAgentPos(creator);
				if (posOwner != null){
			       pos = nextPosNear(posOwner);		
				}
			}
			if (pos == null){
			  pos = nextPos();
			}
			artifacts.add(new ArtifactMarker(id,pos));
		} finally {
			lock.unlock();
		}
	}

	public void removeArtifact(ArtifactId id){
		try {
			lock.lock();
			artifacts.remove(id);
		} finally {
			lock.unlock();
		}
	}
	
	public ArrayList<AgentMarker> getAgents(){
		return agents;
	}

	public ArrayList<ArtifactMarker> getArtifacts(){
		return artifacts;
	}

	public Collection<OpInfo> getOngoingOps(){
		return ongoingOp.values();
	}
	
	public Collection<FocussedArtifactInfo> getFocussedArtifacts(){
		return focussedArt;
	}
	
	public void addOngoingOp(AgentId who, ArtifactId aid, Op op, OpId oid){
		try {
			lock.lock();
			String key = oid.getArtifactId().getName()+oid.getId();
			OpInfo info = new OpInfo(who,aid,op);
			info.changeStateToStarted(oid);
			ongoingOp.put(key, info);
		} finally {
			lock.unlock();
		}
	}
	
	public void addFocus(AgentId who, ArtifactId aid){
		try {
			lock.lock();
			FocussedArtifactInfo info = new FocussedArtifactInfo(who,aid);
			this.focussedArt.add(info);
		} finally {
			lock.unlock();
		}
	}
	
	
	/*
	public void changeOpStateToStarted(OpId id){
		try {
			lock.lock();
			OpInfo opInfo = requestedOp.remove(id.getOpName());
			if (opInfo == null){
				log("ERROR - no request for "+id.getOpName());
			} else {
				opInfo.changeStateToStarted(id);
				ongoingOp.put(id, opInfo);
			}
		} finally {
			lock.unlock();
		}
	}
	*/
	
	public void changeOpStateToCompleted(OpId id){
		try {
			lock.lock();
			String key = id.getArtifactId().getName()+id.getId();
			OpInfo opInfo = ongoingOp.get(key);
			opInfo.changeStateToCompleted(id);
		} finally {
			lock.unlock();
		}
	}
	
	public  void changeOpStateToFailed(OpId id){
		try {
			lock.lock();
			String key = id.getArtifactId().getName()+id.getId();
			OpInfo opInfo = ongoingOp.get(key);
			opInfo.changeStateToFailed(id);
		} finally {
			lock.unlock();
		}
	}
	
	
	void update(){
		try {
			lock.lock();
			for (AgentMarker m0: agents){
				for (AgentMarker m1: agents){
					if (m0 != m1){
						V2d vect = new V2d(m0.getPos(),m1.getPos()); 
						applyForce(vect,m0.getPos());
					}
				}
				for (ArtifactMarker m2: artifacts){
					V2d vect = new V2d(m0.getPos(),m2.getPos()); 
					applyForce(vect,m0.getPos());
				}
			}
	
			for (ArtifactMarker m0: artifacts){
				
				for (AgentMarker m1: agents){
					//force.sum(new V2d(m0.getPos(),m1.getPos()));
					V2d vect = new V2d(m0.getPos(),m1.getPos()); 
					applyForce(vect,m0.getPos());
				}
				for (ArtifactMarker m2: artifacts){
					if (m0 != m2){
						//force.sum(new V2d(m0.getPos(),m2.getPos()));
						V2d vect = new V2d(m0.getPos(),m2.getPos()); 
						applyForce(vect,m0.getPos());
					}
				}
				//applyForce(force,m0.getPos());
			}

			Iterator<OpInfo> it = ongoingOp.values().iterator();
			while (it.hasNext()){
				OpInfo info = it.next();
				long time = System.currentTimeMillis();
				if ((info.hasCompleted() || info.hasFailed()) && ((time - info.getTimestamp()) > 50)) {
					//log("removing op "+info.getOpId()+" "+info.getTargetId());
					it.remove();
				} else {
					/*
					P2d m0 = this.getAgentPos(info.getAgentId());
					P2d m1 = this.getArtifactPos(info.getTargetId());
					if (m0 != null && m1 != null){
						V2d vect = new V2d(m1,m0);
						applyForce2(vect,m0);
					}*/
				}
			}
			
			
			
		} finally {
			lock.unlock();
		}
	}
	
	private void applyForce(V2d force, P2d pos){
		double alfa = 0.0001;
		double dist = force.abs();
	    if (dist==0){
			double x = rand.nextDouble()*0.2-0.1;
			double y = rand.nextDouble()*0.2-0.1;
			pos.sum(new V2d(x,y));
	    } else { 
		    double forceMod = alfa/(dist*dist*dist);
			if (forceMod > 0.0005){//0.0005/*0.0025*/){
				force.normalize();
				if (forceMod > 0.25){
					forceMod = 0.25;
				}
				force.mul(forceMod);
				pos.sum(force);
			}
	    }
	}
	
	private void applyForce2(V2d force, P2d pos){
		double alfa = 0.0005;
		double dist = force.abs();
	    if (dist==0){
			double x = rand.nextDouble()*0.1-0.05;
			double y = rand.nextDouble()*0.1-0.05;
			pos.sum(new V2d(x,y));
	    } else { 
		    double forceMod = alfa*dist;
			if (forceMod > 0.0005){//0.0005/*0.0025*/){
				force.normalize();
				if (forceMod > 0.25){
					forceMod = 0.25;
				}
				force.mul(forceMod);
				pos.sum(force);
			}
	    }
	}

	private void log(String msg){
		System.out.println("[SCENE] "+msg);
	}

	public void lock(){
		lock.lock();
	}

	public void unlock(){
		lock.unlock();
	}


	private P2d getAgentPos(AgentId id){
		for (AgentMarker marker: agents){
			if (marker.getAgentId().equals(id)){
				return marker.getPos();
			}
		}
		return null;
	}

	private P2d getArtifactPos(ArtifactId id){
		for (ArtifactMarker marker: artifacts){
			if (marker.getArtifactId().equals(id)){
				return marker.getPos();
			}
		}
		return null;
	}

}
