/**
 * CArtAgO - DISI, University of Bologna
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package cartago.infrastructure.web;

import java.rmi.*;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.omg.CORBA.Request;

import cartago.*;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.http.ServerWebSocket;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.CorsHandler;


/**
 * Class representing a CArtAgO node service, serving remote requests
 *  
 * @author aricci
 *
 */
public class CartagoMiddlewareDaemon extends AbstractVerticle  {

	private int port;
		
	private Router router;
	private HttpServer server;
	
	private static int CARTAGO_DEF_PORT = 22000;
	private Logger logger = LoggerFactory.getLogger(CartagoMiddlewareDaemon.class);
	
	private static final String API_BASE_PATH = "/cartago/api";
	
	
	public CartagoMiddlewareDaemon(int port) {
		this.port = port;
	}	

	
	@Override
	public void start() {
		initWS();	
	}
	

	private void initWS() {
		
		router = Router.router(vertx);
		
		router.route().handler(CorsHandler.create("*")
				.allowedMethod(io.vertx.core.http.HttpMethod.GET)
				.allowedMethod(io.vertx.core.http.HttpMethod.POST)
				.allowedMethod(io.vertx.core.http.HttpMethod.PUT)
				.allowedMethod(io.vertx.core.http.HttpMethod.DELETE)
				.allowedMethod(io.vertx.core.http.HttpMethod.OPTIONS)
				.allowedHeader("Access-Control-Request-Method")
				.allowedHeader("Access-Control-Allow-Credentials")
				.allowedHeader("Access-Control-Allow-Origin")
				.allowedHeader("Access-Control-Allow-Headers")
				.allowedHeader("Content-Type"));

		router.route().handler(BodyHandler.create());
		router.get(API_BASE_PATH + "/version").handler(this::handleGetVersion);
		router.post(API_BASE_PATH + "/envs").handler(this::handleNewEnv);

		
		server = vertx.createHttpServer()
		.requestHandler(router)
		.listen(port, result -> {
			if (result.succeeded()) {
				log("CArtAgO Daemon Ready.");
			} else {
				log("Failed: "+result.cause());
			}
		});

	}

	private  void log(String msg) {
		System.out.println("[ CArtAgO Node Daemon ] " + msg);
	}

	public void shutdown(){
		// garbageCollector.stopActivity();		
		server.close();		
	}

	
	private void handleNewEnv(RoutingContext routingContext) {
		log("Handling New env");
		try {
			JsonObject newEnvParams = routingContext.getBodyAsJson();
	
			String envName = newEnvParams.getString("envName");
			int envPort = newEnvParams.getInteger("envPort");
			String wspRootName = newEnvParams.getString("wspRootName");
			
			JsonObject parendWspIdobj = newEnvParams.getJsonObject("parentWspId");
			String parentAddress = newEnvParams.getString("parentAddress");
				
			WorkspaceId wid = JsonUtil.toWorkspaceId(parendWspIdobj);

			new Thread(() -> {
				try {
					CartagoEnvironment.createFromRemote(envName, "localhost:"+envPort, wspRootName, wid, parentAddress);
	
					WorkspaceDescriptor des = CartagoEnvironment.getInstance().getRootWSP();

					JsonObject obj = new JsonObject();
					obj.put("envId", CartagoEnvironment.getInstance().getId().toString());
					obj.put("wspId", des.getId());

					HttpServerResponse response = routingContext.response();
					response.end(obj.encode());
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}).start();
		} catch (Exception ex) {
			ex.printStackTrace();
			routingContext.response().end();
		}
	}

	
	/* GET VERSION */

	private void handleGetVersion(RoutingContext routingContext) {
		log("Handling Get Version from "+routingContext.request().absoluteURI());
		HttpServerResponse response = routingContext.response();
		response.putHeader("content-type", "application/text").end(CARTAGO_VERSION.getID());
	}

	
	public static void main(String[] args) throws Exception {			
		Vertx vertx = Vertx.vertx();
		vertx.deployVerticle(new CartagoMiddlewareDaemon(CARTAGO_DEF_PORT));
	}
}
