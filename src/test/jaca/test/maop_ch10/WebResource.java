package maop_ch10;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.SSLContext;

import org.apache.http.*;
import org.apache.http.client.*;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.*;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.*;
import org.apache.http.impl.conn.BasicHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.ssl.TrustStrategy;

import cartago.*;

public class WebResource extends Artifact {

	private CloseableHttpClient client;
	
	void init() throws Exception {
	    
		TrustStrategy acceptingTrustStrategy = (cert, authType) -> true;
	    SSLContext sslContext = SSLContexts.custom().loadTrustMaterial(null, acceptingTrustStrategy).build();
	    SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslContext, NoopHostnameVerifier.INSTANCE);
	     
	    Registry<ConnectionSocketFactory> socketFactoryRegistry = 
	      RegistryBuilder.<ConnectionSocketFactory> create()
	      .register("https", sslsf)
	      .register("http", new PlainConnectionSocketFactory())
	      .build();
	 
	    BasicHttpClientConnectionManager connectionManager = 
	      new BasicHttpClientConnectionManager(socketFactoryRegistry);

	    client = HttpClients.custom().setSSLSocketFactory(sslsf)
	      .setConnectionManager(connectionManager).build();
	 		
	}
	
	@OPERATION 
	void get(String uri, OpFeedbackParam<String> res){
		HttpGet req = new HttpGet(uri);
		CloseableHttpResponse response = null;
		try {
			response = client.execute(req);
	        if (response.getStatusLine().getStatusCode() >= 300) {
	        	failed("error");
	        } else {
		        res.set(getResponse(response.getEntity()));
	        }
		} catch (Exception ex) {
        	failed("error");
		} finally {
			if (response != null) {
				try {
					response.close();
				} catch (Exception ex) {}
			}
		}
	}

	@OPERATION 
	void post(String uri, String payload, OpFeedbackParam<String> res){
		HttpPost req = new HttpPost(uri);
		CloseableHttpResponse response = null;
		try {
		    if (payload != null) {
				StringEntity entity = new StringEntity(payload);
			    req.setEntity(entity);
			    req.setHeader("Accept", "application/json");
			    req.setHeader("Content-type", "application/json");
			 	req.setEntity(entity);
		    }
			response = client.execute(req);
	        if (response.getStatusLine().getStatusCode() >= 300) {
	        	failed("error");
	        } else {
		        res.set(getResponse(response.getEntity()));
	        }
		} catch (Exception ex) {
        	failed("error");
		} finally {
			if (response != null) {
				try {
					response.close();
				} catch (Exception ex) {}
			}
		}
	}
	
	private String getResponse(HttpEntity entity) throws Exception {
        if (entity == null) {
        	return "";
        } else {
			BufferedReader reader = new BufferedReader(new InputStreamReader(entity.getContent()));
	        StringBuffer sb = new StringBuffer();
	        while (reader.ready()) {
	        	sb.append(reader.readLine());
	        }
	        return sb.toString();
        }
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