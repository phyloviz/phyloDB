package pt.ist.meic.phylodb.utils.service;

public abstract class Entity<K> {

	protected K id;
	protected int version;
	protected boolean deprecated;

	public Entity(K id, int version, boolean deprecated) {
		this.id = id;
		this.version = version;
		this.deprecated = deprecated;
	}

	public K getPrimaryKey() {
		return id;
	}

	public int getVersion() {
		return version;
	}

	public boolean isDeprecated() {
		return deprecated;
	}

}
