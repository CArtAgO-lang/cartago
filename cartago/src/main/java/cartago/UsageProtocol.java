package cartago;
import cartago.manual.syntax.Literal;
import cartago.manual.syntax.UsageProtBody;

public class UsageProtocol {

	private String signature;
    private Object pre;
    private Literal function;
    private UsageProtBody body;

	public UsageProtocol(String signature, Literal function){
		this.signature = signature;
		this.function = function;
	}
	
	public void setPrecondition(Object pre){
		this.pre = pre;
	}
	
	public void setBody(UsageProtBody body){
		this.body = body;
	}
	
	public String getSignature(){
		return signature;
	}
	
	public Literal getFunction(){
		return function;
	}
	
	public UsageProtBody getBody(){
		return body;
	}
	
	public Object getPrecondition(){
		return pre;
	}
}
