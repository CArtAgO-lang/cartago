package cartago.infrastructure.web;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import cartago.*;

public class OpFeedbackParamDeserializer extends StdDeserializer<OpFeedbackParam> {
    
	ObjectMapper mapper;
	
    public OpFeedbackParamDeserializer() {
        this(null);
    }
 
    public OpFeedbackParamDeserializer(Class<?> vc) {
        super(vc);
    }

    public void setMapper(ObjectMapper mapper) {
    	this.mapper = mapper;
    }
    
    @Override
    public OpFeedbackParam deserialize(JsonParser parser, DeserializationContext deserializer) {
    	// Car car = new Car();
    	/*
    	ObjectCodec codec = parser.getCodec();
        TreeNode node = codec.readTree(parser);
         
        // try catch block
        TreeNode value = node.get("op_feedback_param");
        OpFeedbackParam res = new OpFeedbackParam();
        value.to
        res.set(t);
        */
    	return new OpFeedbackParam<>();
    }
}