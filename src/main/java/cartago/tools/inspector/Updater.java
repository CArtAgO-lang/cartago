package cartago.tools.inspector;

import java.util.*;

public class Updater extends Thread {

	Inspector insp;
	WorkspaceScene scene;
	HashMap<Class<? extends LoggerEvent>,Command> commands;
	
	public Updater(Inspector insp){
		commands = new HashMap<Class<? extends LoggerEvent>,Command>();
		this.insp = insp;
		scene = insp.getWorkspaceScene();
		
		commands.put(AgentJoined.class, new Command(){
			public void process(LoggerEvent event){
				AgentJoined ev = (AgentJoined) event;
				scene.addAgent(ev.getAgentId());
			}
		});
		
		commands.put(AgentQuit.class, new Command(){
			public void process(LoggerEvent event){
				AgentQuit ev = (AgentQuit) event;
				scene.removeAgent(ev.getAgentId());
			}
		});
		
		commands.put(ArtifactCreated.class, new Command(){
			public void process(LoggerEvent event){
				ArtifactCreated ev = (ArtifactCreated) event;
				scene.addArtifact(ev.getArtifactId(),ev.getCreatorId());
			}
		});

		commands.put(ArtifactDisposed.class, new Command(){
			public void process(LoggerEvent event){
				ArtifactDisposed ev = (ArtifactDisposed) event;
				scene.removeArtifact(ev.getArtifactId());
			}
		});

		commands.put(OpRequested.class, new Command(){
			public void process(LoggerEvent event){
				OpRequested ev = (OpRequested) event;
				//scene.addOngoingOp(ev.getAgentId(),ev.getArtifactId(),ev.getOp());
   			}
		});
		
		commands.put(OpStarted.class, new Command(){
			public void process(LoggerEvent event){
				OpStarted ev = (OpStarted) event;
				//scene.changeOpStateToStarted(ev.getOpId());
				scene.addOngoingOp(ev.getOpId().getAgentBodyId(),ev.getArtifactId(),ev.getOp(),ev.getOpId());
			}
		});
		
		commands.put(OpCompleted.class, new Command(){
			public void process(LoggerEvent event){
				/*
				OpCompleted ev = (OpCompleted) event;
				scene.changeOpStateToCompleted(ev.getOpId());
				*/
			}
		});

		commands.put(OpFailed.class, new Command(){
			public void process(LoggerEvent event){
				/*
				OpFailed ev = (OpFailed) event;
				scene.changeOpStateToFailed(ev.getOpId());
				*/
			}
		});
	
	}

	public void run(){
		while (true){
			LoggerEvent event = insp.fetchEvent();
			Command cmd = commands.get(event.getClass());
			if (cmd != null){
				log("new event: "+event);
				cmd.process(event);
			} else {
				log("unrecognized event: "+event);
			}
		}
	}
	
	private void log(String msg){
		System.out.println("[UPDATER] "+msg);
	}
	interface Command {
		void process(LoggerEvent ev);
	}
	
}
