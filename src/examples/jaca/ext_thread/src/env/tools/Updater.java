package tools;

public class Updater extends Thread {
	
	Counter art = null;
	
	public Updater(Counter c) {
		art = c;	
	}
	
	@Override
	public void run() {
		try {
			while (true) {
				sleep(1000);
				art.internalInc();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	

}
