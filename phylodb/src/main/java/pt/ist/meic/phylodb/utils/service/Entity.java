package pt.ist.meic.phylodb.utils.service;


public abstract class Entity {

	protected int version;
	protected boolean deprecated;

	public Entity(int version, boolean deprecated) {
		this.version = version;
		this.deprecated = deprecated;
	}

	public int getVersion() {
		return version;
	}

	public boolean isDeprecated() {
		return deprecated;
	}

}
