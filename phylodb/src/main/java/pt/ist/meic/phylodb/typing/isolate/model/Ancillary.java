package pt.ist.meic.phylodb.typing.isolate.model;

import java.util.Objects;

public class Ancillary {

	private String key;
	private String value;

	public Ancillary() {
	}

	public Ancillary(String key, String value) {
		this.key = key;
		this.value = value;
	}

	public String getKey() {
		return key;
	}

	public String getValue() {
		return value;
	}

	@Override
	public boolean equals(Object o) {
		if (o == null || getClass() != o.getClass()) return false;
		Ancillary ancillary = (Ancillary) o;
		return Objects.equals(key, ancillary.key);
	}

}
