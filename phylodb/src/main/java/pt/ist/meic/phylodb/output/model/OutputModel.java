package pt.ist.meic.phylodb.output.model;

public abstract class OutputModel {

	private boolean deprecated;
	private int version;

	public OutputModel(boolean deprecated, int version) {
		this.deprecated = deprecated;
		this.version = version;
	}

	public boolean isDeprecated() {
		return deprecated;
	}

	public int getVersion() {
		return version;
	}

}
