package pt.ist.meic.phylodb.utils.service;

import java.util.Objects;

/**
 * An Entity is an domain object that can be identified an {@link K id}, and that can be signaled as deprecated
 *
 * @param <K> entity id
 */
public class Entity<K> {

	protected K id;
	protected boolean deprecated;

	public Entity(K id, boolean deprecated) {
		this.id = id;
		this.deprecated = deprecated;
	}

	public K getPrimaryKey() {
		return id;
	}

	public boolean isDeprecated() {
		return deprecated;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Entity<?> entity = (Entity<?>) o;
		return deprecated == entity.deprecated &&
				Objects.equals(id, entity.id);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, deprecated);
	}

}
