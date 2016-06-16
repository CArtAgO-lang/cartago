package cartago.tools.inspector;

import cartago.*;
import java.util.concurrent.*;

public class Inspector {

	private LinkedBlockingQueue<LoggerEvent> eventQueue;
    private InspectorLogger logger;
    private WorkspaceScene scene;
    private Updater updater;
    private Renderer renderer;
    private InspectorView view;
    
	public Inspector(){
		eventQueue = new LinkedBlockingQueue<LoggerEvent>();
		scene = new WorkspaceScene();
		logger = new InspectorLogger(this);
	    updater = new Updater(this);	
	    view = new InspectorView(scene);
	    view.setVisible(true);
	    renderer = new Renderer(scene,view);
	}

	public void start(){
	    updater.start();
	    renderer.start();
	}
	
	public ICartagoLogger getLogger(){
		return logger;
	}
	
	public WorkspaceScene getWorkspaceScene(){
		return scene;
	}

	public void notifyEvent(LoggerEvent ev){
		eventQueue.add(ev);
	}
	
	public LoggerEvent fetchEvent(){
		try {
			return eventQueue.poll(1000000, TimeUnit.MILLISECONDS);
		} catch (Exception ex){
			return null;
		}
	}
	
}
