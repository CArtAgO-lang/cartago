package cartago.tools;

import cartago.*;
import java.net.*;
import java.io.*;

/**
 * To be completed.
 * 
 * @author aricci
 *
 */
public class CommunicatorArtifact extends Artifact {

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

	/*
	@OPERATION void tell(String agent, String what){		
		this.signal(target, type, objs)
	}
	*/
	
	/*
	@OPERATION void writeByte(int v){
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
	*/
	
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

