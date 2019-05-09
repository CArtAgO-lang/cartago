package cartago.infrastructure.web;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;

import cartago.AgentId;
import cartago.ArtifactId;
import cartago.ArtifactObsProperty;
import cartago.NodeId;
import cartago.Op;
import cartago.OpFeedbackParam;
import cartago.Tuple;
import cartago.WorkspaceId;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

public class JsonUtil {

    static private ObjectMapper objectMapper;
    static private SimpleModule module;
	static {
        objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        objectMapper.configure(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES, false);
        objectMapper.configure(DeserializationFeature.USE_JAVA_ARRAY_FOR_JSON_ARRAY, true);
        module = 
      		  new SimpleModule("OpFeedbackParam", new Version(1, 0, 0, null, null, null));
        module.addSerializer(OpFeedbackParam.class, new OpFeedbackParamSerializer());
        // module.addDeserializer(OpFeedbackParam.class, new OpFeedbackParamDeserializer());
        objectMapper.registerModule(module);
		
	}
	
	// ---

	static public JsonObject toJson(Op op) {
		JsonArray params = new JsonArray();
		for (Object p: op.getParamValues()) {
			JsonObject param = new JsonObject();
			param.put("paramClass", p.getClass().getName());
			if (!p.getClass().getName().equals("cartago.OpFeedbackParam")) {
				try {
					String ser = objectMapper.writeValueAsString(p);
					param.put("paramValue", ser);
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			} else {
				OpFeedbackParam opp = (OpFeedbackParam) p;
				Object val = opp.get();
				if (val != null) {
					JsonObject varx = new JsonObject();
					varx.put("class", val.getClass().getName());
					try {
						String ser = objectMapper.writeValueAsString(val);
						varx.put("value", ser);
					} catch (Exception ex) {
						ex.printStackTrace();
					}
					param.put("paramValue", varx);
				}
			}
			params.add(param);
		}
		
		JsonObject opInfo = new JsonObject();
		opInfo.put("name", op.getName());
		opInfo.put("params", params);
		return opInfo;
	}
	
	static public Op toOp(JsonObject obj) {
		String opName = obj.getString("name");
		JsonArray params = obj.getJsonArray("params");
		Object[] par = new Object[params.size()];
		for (int i = 0; i < params.size(); i++) {
			JsonObject param = params.getJsonObject(i);
			String paramClassName = param.getString("paramClass");
				if (!paramClassName.equals("cartago.OpFeedbackParam")) {
					String value = param.getString("paramValue");	      			  
					try {
						if (value != null) {
							par[i] = objectMapper.readValue(value, Class.forName(paramClassName));
						} 
					} catch (Exception ex) {
						ex.printStackTrace();
					}
				} else {
					JsonObject pvalue = param.getJsonObject("paramValue");	      			  
					OpFeedbackParam opp = new OpFeedbackParam();
					try {
						if (pvalue != null) {
							Object val = objectMapper.readValue(pvalue.getString("value"), Class.forName(pvalue.getString("class")));
							opp.set(val);
						}
					} catch (Exception ex) {
						ex.printStackTrace();
					}
					par[i] = opp;
				}
		}			    			
		return new Op(opName, par);
	}

	// ---
	
	static public  JsonObject toJson(AgentId id) {
		JsonObject obj = new JsonObject();
		obj.put("localId", id.getLocalId());
		obj.put("agentName", id.getAgentName());
		obj.put("agentRole", id.getAgentRole());
		JsonObject wid = toJson(id.getWorkspaceId());
		obj.put("workspaceId", wid);
		obj.put("globalId", id.getGlobalId());		
		return obj;
	}
	
	static public AgentId toAgentId(JsonObject obj) {
		String agentName = obj.getString("agentName");
		String agentRole = obj.getString("agentRole");
		int localId = obj.getInteger("localId");
		String globalId = obj.getString("globalId");
		WorkspaceId wid = toWorkspaceId(obj.getJsonObject("workspaceId"));
		return new AgentId(agentName, globalId, localId, agentRole, wid);
	}
	
	// --
	
	static public JsonObject toJson(WorkspaceId id) {
		JsonObject obj = new JsonObject();
		obj.put("name", id.getName());
		obj.put("nodeId", id.getNodeId().getId().toString());
		return obj;
	}

	static public WorkspaceId toWorkspaceId(JsonObject obj) {		
		String name = obj.getString("name");
		String nodeId = obj.getString("nodeId");
		NodeId nodeIdObj  = new NodeId(nodeId);
		return new WorkspaceId(name, nodeIdObj);
	}
	
	// --
	
	static public  JsonObject toJson(ArtifactId id) {
		JsonObject obj = new JsonObject();
		obj.put("id", id.getId());
		obj.put("name", id.getName());
		obj.put("artifactType", id.getArtifactType());
		JsonObject wid = toJson(id.getWorkspaceId());
		obj.put("workspaceId", wid);
		JsonObject cid = toJson(id.getCreatorId());
		obj.put("creatorId", cid);		
		return obj;
	}
	
	static public ArtifactId toArtifactId(JsonObject obj) {
		if (obj != null) {
			String name = obj.getString("name");
			int id = obj.getInteger("id");
			String artifactType = obj.getString("artifactType");
			WorkspaceId workspaceId = toWorkspaceId(obj.getJsonObject("workspaceId"));
			AgentId creatorId = toAgentId(obj.getJsonObject("creatorId"));
			return new ArtifactId(name, id, artifactType, workspaceId, creatorId);
		} else {
			return null;
		}
	}
	
	// --

	static public  JsonObject toJson(Tuple t) {
		JsonObject obj = new JsonObject();
		obj.put("name", t.getLabel());
		JsonArray params = new JsonArray();
		for (Object p: t.getContents()) {
			JsonObject param = new JsonObject();
			param.put("paramClass", p.getClass().getName());
			try {
				String ser = objectMapper.writeValueAsString(p);
				param.put("paramValue", ser);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			params.add(param);
		}
		obj.put("params", params);
		return obj;
	}
	
	
	static public Tuple toTuple(JsonObject obj) {
		if (obj != null) {
			String name = obj.getString("name");
			JsonArray params = obj.getJsonArray("params");
			Object[] par = new Object[params.size()];
			for (int i = 0; i < params.size(); i++) {
				JsonObject param = params.getJsonObject(i);
				String paramClassName = param.getString("paramClass");
				String value = param.getString("paramValue");	      			  
				try {
					if (value != null) {
						par[i] = objectMapper.readValue(value, Class.forName(paramClassName));
					} 
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			} 			    			
			return new Tuple(name, par);
		} else {
			return null;
		}
	}
	
	// --
	
	static public  JsonObject toJson(ArtifactObsProperty prop) {
		JsonObject obj = new JsonObject();
		obj.put("name", prop.getName());
		obj.put("id", prop.getId());
		obj.put("fullId", prop.getFullId());		
		JsonArray params = new JsonArray();
		for (Object p: prop.getValues()) {
			JsonObject param = new JsonObject();
			param.put("paramClass", p.getClass().getName());
			try {
				String ser = objectMapper.writeValueAsString(p);
				param.put("paramValue", ser);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			params.add(param);
		}
		obj.put("params", params);
		return obj;
	}
	
	static public ArtifactObsProperty toArtifactObsProperty(JsonObject obj) {
		String name = obj.getString("name");
		long id = obj.getLong("id");
		String fullId = obj.getString("fullId");
		JsonArray params = obj.getJsonArray("params");
		Object[] par = new Object[params.size()];
		for (int i = 0; i < params.size(); i++) {
			JsonObject param = params.getJsonObject(i);
			String paramClassName = param.getString("paramClass");
			String value = param.getString("paramValue");	      			  
			try {
				if (value != null) {
					par[i] = objectMapper.readValue(value, Class.forName(paramClassName));
				} 
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		} 		
		return new ArtifactObsProperty(fullId, id, name, par);
	}

	// --
	
	static public  JsonArray toJson(ArtifactObsProperty[] props) {
		JsonArray obj = new JsonArray();
		for (ArtifactObsProperty prop: props) {
			obj.add(toJson(prop));
		}
		return obj;
	}
		
	static public ArtifactObsProperty[] toArtifactObsPropertyArray(JsonArray obj) {
		if (obj != null) {
			ArtifactObsProperty[] elems = new ArtifactObsProperty[obj.size()];
			for (int i = 0; i < elems.length; i++) {
				elems[i] = toArtifactObsProperty(obj.getJsonObject(i));
			}
			return elems;
		} else {
			return null;
		}
	}
	
	// 
	
	static public  JsonArray toJson(Collection<ArtifactObsProperty> props) {
		JsonArray obj = new JsonArray();
		for (ArtifactObsProperty prop: props) {
			obj.add(toJson(prop));
		}
		return obj;
	}

	static public List<ArtifactObsProperty> toArtifactObsPropertyList(JsonArray obj) {
		if (obj != null) {
			List<ArtifactObsProperty> elems = new ArrayList<ArtifactObsProperty>();
			for (int i = 0; i < obj.size(); i++) {
				elems.add(toArtifactObsProperty(obj.getJsonObject(i)));
			}
			return elems;
		} else {
			return null;
		}
	}

}
