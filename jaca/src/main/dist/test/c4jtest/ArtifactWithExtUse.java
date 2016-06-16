package c4jtest;

import cartago.*;

class ExtThread extends Thread {
	
	private ArtifactWithExtUse art;

	public ExtThread(ArtifactWithExtUse art){
		this.art = art;
	}
	
	public void run(){
		while (true){
			try {
				Thread.sleep(1000);
				art.beginExternalSession();
				art.externalInc();
				art.endExternalSession(true);
			} catch (Exception ex){
				art.endExternalSession(false);
			}
		}
	}
	
}

public class ArtifactWithExtUse extends Artifact {

	void init(){
		defineObsProperty("a",0);
		new ExtThread(this).start();
	}

	/* ext API */
	
	public void externalInc(){
		ObsProperty prop = this.getObsProperty("a");
		prop.updateValue(prop.intValue()+1);
	}
	
	@OPERATION void reset(){
		ObsProperty prop = this.getObsProperty("a");
		prop.updateValue(0);
	}
	
}
