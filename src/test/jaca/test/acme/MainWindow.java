package acme;

import javafx.application.Application;
import javafx.event.*;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class MainWindow extends Application {
	
	static private MainWindowArtifact art;
	
    public void start(Stage stage) throws Exception {
    	stage.setTitle("Hello World!");
        Button btn = new Button("Say 'Hello World'");
        
        btn.pressedProperty().addListener((observable, wasPressed, pressed) -> {
        	try {
	        	art.beginExtSession();
	            if (pressed) {
	            	art.notifyButtonPressed();
	            } else {
	            	art.notifyButtonReleased();
	            }
        	} finally {
	        	art.endExtSession();
        	}
        });
        
        StackPane root = new StackPane();
        root.getChildren().add(btn);
        stage.setScene(new Scene(root, 300, 250));
        stage.show();    	
    }
        
    public void initJFX(MainWindowArtifact art) {
    	MainWindow.art = art;
    	launch(new String[] {});
    }
} 