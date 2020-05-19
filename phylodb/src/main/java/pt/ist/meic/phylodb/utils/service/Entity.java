package pt.ist.meic.phylodb.utils.service;

import java.util.Objects;

public class Entity<K> {

	protected K id;
	protected long version;
	protected boolean deprecated;

	public Entity(K id, long version, boolean deprecated) {
		this.id = id;
		this.version = version;
		this.deprecated = deprecated;
	}

	public K getPrimaryKey() {
		return id;
	}

	public long getVersion() {
		return version;
	}

	public boolean isDeprecated() {
		return deprecated;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Entity<?> entity = (Entity<?>) o;
		return version == entity.version &&
				deprecated == entity.deprecated &&
				Objects.equals(id, entity.id);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, version, deprecated);
	}

}
