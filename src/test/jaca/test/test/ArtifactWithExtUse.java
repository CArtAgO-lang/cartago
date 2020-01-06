package test;

import cartago.*;


public class ArtifactWithExtUse extends Artifact {

	void init(){
		defineObsProperty("a",0);
		new ExtThread(this).start();
	}

	@OPERATION void reset(){
		ObsProperty prop = this.getObsProperty("a");
		prop.updateValue(0);
	}

	void externalInc(){
		ObsProperty prop = this.getObsProperty("a");
		prop.updateValue(prop.intValue()+1);
	}
	
	class ExtThread extends Thread {
		
		private ArtifactWithExtUse art;

		public ExtThread(ArtifactWithExtUse art){
			this.art = art;
		}
		
		public void run(){
			while (true){
				try {
					Thread.sleep(1000);
					art.beginExtSession();
					art.externalInc();
					art.endExtSession();
				} catch (Exception ex){
				}
			}
		}
		
	}	
}
