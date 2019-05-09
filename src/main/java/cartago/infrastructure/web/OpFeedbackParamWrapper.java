package cartago.infrastructure.web;

public class OpFeedbackParamWrapper {

	private String typeName;
	private Object content;
	
	public OpFeedbackParamWrapper() {
	}

	public OpFeedbackParamWrapper(String typeName, Object content) {
		this.typeName = typeName;
		this.content = content;
	}
	
	public String getTypeName() {
		return typeName;
	}
	
	public Object getContent() {
		return content;
	}
	
}
