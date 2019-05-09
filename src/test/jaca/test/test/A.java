package test;

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
