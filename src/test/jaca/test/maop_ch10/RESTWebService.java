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
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.CorsHandler;

public class RESTWebService extends Artifact {

	private Vertx vertx;
	private Router router;
	private HttpServer server;
	
	void init() throws Exception {
		vertx = Vertx.vertx();
		router = Router.router(vertx);
		
		router.route().handler(CorsHandler.create("*")
				.allowedMethod(io.vertx.core.http.HttpMethod.GET)
				.allowedMethod(io.vertx.core.http.HttpMethod.POST)
				.allowedHeader("Access-Control-Request-Method")
				.allowedHeader("Access-Control-Allow-Credentials")
				.allowedHeader("Access-Control-Allow-Origin")
				.allowedHeader("Access-Control-Allow-Headers")
				.allowedHeader("Content-Type"));

		router.route().handler(BodyHandler.create());		
	}
	
	@OPERATION
	void acceptGET(String path) {
		router.get(path).handler((res) -> {
			this.beginExtSession();
			this.signal("new_req", "get", path, res);
			this.endExtSession();
		});
	}

	@OPERATION
	void acceptPOST(String path) {
		router.post(path).handler((res) -> {
			this.beginExtSession();
			this.signal("new_req", "post", path, res);
			this.endExtSession();
		});
	}
	
	@OPERATION 
	void getParam(RoutingContext ctx, String paramName, OpFeedbackParam<String> value) {
		value.set(ctx.request().getParam(paramName));
	}

	@OPERATION 
	void getBody(RoutingContext ctx, OpFeedbackParam<String> value) {
		value.set(ctx.getBodyAsString());
	}

	@OPERATION
	void getBodyAsJson(RoutingContext ctx, OpFeedbackParam<JsonObject> value) {
		value.set(ctx.getBodyAsJson());
	}

	@OPERATION
	void getJsonDataValue(JsonObject obj, String name, OpFeedbackParam<Object> value) {
		value.set(obj.getValue(name));
	}
	
	@OPERATION 
	void sendResponse(RoutingContext ctx, String res) {
		ctx.response().end(res);
	}
	
	@OPERATION
	void start(int port) {
		server = vertx.createHttpServer()
				.requestHandler(router)
				.listen(port, result -> {
					if (result.succeeded()) {
						log("Ready.");
					} else {
						log("Failed: "+result.cause());
					}
				});
	}
	
}

