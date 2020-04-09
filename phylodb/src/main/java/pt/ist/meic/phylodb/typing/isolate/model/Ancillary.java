package pt.ist.meic.phylodb.typing.isolate.model;

import java.util.Objects;

public class Ancillary {

	private final String key;
	private final String value;

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
	public String toString() {
		return String.format("%s: %s", key, value);
	}

	@Override
	public boolean equals(Object o) {
		if (o == null || getClass() != o.getClass()) return false;
		Ancillary ancillary = (Ancillary) o;
		return Objects.equals(key, ancillary.key);
	}

}
