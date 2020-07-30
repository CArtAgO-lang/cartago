package acme;

import cartago.*;

public class MainWindowArtifact extends Artifact {

    public void init() throws Exception {
    	defineObsProperty("button","not_pressed");

    	/* setup JavaFX */
    	initJFX(this);
    }

    @INTERNAL_OPERATION
    void notifyButtonPressed() {
    	getObsProperty("button").updateValue("pressed");
    }

    @INTERNAL_OPERATION
    void notifyButtonReleased() {
    	getObsProperty("button").updateValue("released");
    }
    
    private void initJFX(MainWindowArtifact art) {
    	new Thread(() -> {
    		new MainWindow().initJFX(art);        
    	}).start();
    }
}
