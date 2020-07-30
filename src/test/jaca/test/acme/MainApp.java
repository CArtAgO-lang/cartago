package acme;

public class MainApp  {	

    public static void main(String[] args) throws Exception {
    	try {
    		jason.infra.centralised.RunCentralisedMAS.main(new String[]{ "src/test/jaca/test/test-javafx.mas2j" });
    	} catch (Exception ex) {
    		ex.printStackTrace();
    	}
    }

}
