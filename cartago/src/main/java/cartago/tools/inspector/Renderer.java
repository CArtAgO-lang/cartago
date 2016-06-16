package cartago.tools.inspector;

import java.util.*;

public class Renderer extends Thread {

	InspectorView view;
	WorkspaceScene scene;
	
	public Renderer(WorkspaceScene scene, InspectorView view){
		this.view = view;
		this.scene = scene;
	}

	public void run(){
		while (true){
			try {
				Thread.sleep(10);
				scene.update();
				view.repaint();
			} catch (Exception ex){
				ex.printStackTrace();
			}
		}
	}

	private void log(String msg){
		System.out.println("[RENDERER] "+msg);
	}
	
}
