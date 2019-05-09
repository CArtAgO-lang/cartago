package c4jtest;

class A<T>{
	T value;
	
	A(){
	}
	
	void set(T v) {
		value = v;
	}
	
	T get() {
		return value;
	}
}
