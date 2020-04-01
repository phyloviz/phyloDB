package pt.ist.meic.phylodb.utils.service;

public class Reference<T> extends Entity {

	private T id;

	public Reference(T id, int version, boolean deprecated) {
		super(version, deprecated);
		this.id = id;
	}

	public T getId() {
		return id;
	}

}
