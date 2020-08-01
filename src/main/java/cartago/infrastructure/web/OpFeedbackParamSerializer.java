package cartago.infrastructure.web;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import cartago.*;
import io.vertx.core.json.JsonObject;

public class OpFeedbackParamSerializer extends StdSerializer<OpFeedbackParam> {
    
    public OpFeedbackParamSerializer() {
        this(null);
    }
 
    public OpFeedbackParamSerializer(Class<OpFeedbackParam> op) {
        super(op);
    }
 
    @Override
    public void serialize(OpFeedbackParam op, JsonGenerator jsonGenerator, SerializerProvider serializer) {
		try {
			jsonGenerator.writeStartObject();
            
            if (op.get() != null) {
	            jsonGenerator.writeObjectField("op_feedback_param", op.get());
    		} else {
                jsonGenerator.writeStringField("op_feedback_param",  "");
    		}
            jsonGenerator.writeEndObject(); 
    	}  catch (Exception ex) {
			ex.printStackTrace();
		}
        /* jsonGenerator.writeStartObject();
        jsonGenerator.writeStringField("car_brand", car.getType());
        jsonGenerator.writeEndObject(); */
    }
}