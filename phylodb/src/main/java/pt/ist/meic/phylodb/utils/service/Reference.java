package pt.ist.meic.phylodb.utils.service;

public class Reference<T> extends Entity<T> {

	public Reference(T id, Long version, boolean deprecated) {
		super(id, version, deprecated);
		this.id = id;
	}

}
