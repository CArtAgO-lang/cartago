package cartago.infrastructure.web;

public class Holder<T> {

	private T value;
	
	public Holder() {
		value = null;
	}
	
	public void set(T value) {
		this.value = value;
	}
	
	public T getValue() {
		return value;
	}
	
	public boolean isPresent() {
		return value != null;
	}
}
