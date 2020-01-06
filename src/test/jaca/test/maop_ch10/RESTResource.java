package maop_ch10;

import cartago.*;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.ext.web.client.HttpResponse;
import io.vertx.ext.web.client.WebClient;

public class RESTResource extends Artifact {

	private Vertx vertx;
	private WebClient client;
	
	void init() {
		vertx = Vertx.vertx();
		client = WebClient.create(vertx);
	}
	
	@OPERATION 
	void get(String host, int port, String uri, OpFeedbackParam<String> res){
		client
		.get(port, host, uri)
		.send(ar -> {
			if (ar.succeeded()) {
			      // Obtain response
					HttpResponse<Buffer> response = ar.result();

			      System.out.println("Received response with status code" + response.statusCode());
			    } else {
			      System.out.println("Something went wrong " + ar.cause().getMessage());
			    }		
		});
	}
	
	@OPERATION 
	void post(String uri, String payload, OpFeedbackParam<String> res){
	}
	
}


/*
public class RESTResource extends Artifact {

	Socket socket;
	DataInputStream input;
	DataOutputStream output;
	ReadCmd cmd;
	boolean stopped;
	
	@OPERATION void init(String addr, int port) throws Exception {
		socket = new Socket(addr,port);
		cmd = new ReadCmd();
		stopped = false;
		input = new DataInputStream(socket.getInputStream());
		output = new DataOutputStream(socket.getOutputStream());
	}
	
	@OPERATION void get(String path, String String payload){
		try {
			output.writeByte(v);
		} catch (Exception ex){
			this.failed(ex.toString());
		}
	}
	
	@OPERATION void readByte(OpFeedbackParam<Integer> value){
		await(cmd);		
		if (cmd.isLastExecSucceeded()){
			value.set(cmd.getLastFetched());
		} else {
			this.failed("read_failure");
		}
	}


	@OPERATION void startReadStream(){
		stopped = false;
		execInternalOp("genReadStream");
	}
	
	@OPERATION void stopReadStream(){
		stopped = true;
	}	
	
	@INTERNAL_OPERATION void genReadStream(){
		while (!stopped){
			await(cmd);		
			if (cmd.isLastExecSucceeded()){
				signal("value",cmd.getLastFetched());
			} else {
				break;
			}
		}
	}
	
	class ReadCmd implements IBlockingCmd {
		
		private int fetched;
		private boolean succeeded;
	
		public ReadCmd(){
			succeeded = false;
		}
		
		public void exec(){
			try {
				fetched = input.readByte();
				succeeded = true;
			} catch (Exception ex){
				succeeded = false;
			}
		}
		
		public int getLastFetched(){
			return fetched;
		}
		
		public boolean isLastExecSucceeded(){
			return succeeded;
		}
	}
}

*/