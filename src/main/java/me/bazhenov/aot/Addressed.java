package me.bazhenov.aot;

final class Addressed<T> {

	private int address = 0;
	private final T ref;

	Addressed(int address) {
		this.address = address;
		ref = null;
	}

	Addressed(T ref) {
		this.ref = ref;
	}

	int getAddress() {
		return address;
	}

	void setAddress(int address) {
		this.address = address;
	}

	T getRef() {
		return ref;
	}
}
