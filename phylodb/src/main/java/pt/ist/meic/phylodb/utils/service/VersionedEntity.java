package pt.ist.meic.phylodb.utils.service;

import java.util.Objects;

/**
 * An VersionedEntity is an {@link Entity} that is versioned
 *
 * @param <K> entity id
 */
public class VersionedEntity<K> extends Entity<K> {

	protected long version;

	public VersionedEntity(K id, long version, boolean deprecated) {
		super(id, deprecated);
		this.version = version;
	}

	public long getVersion() {
		return version;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		if (!super.equals(o)) return false;
		VersionedEntity<?> that = (VersionedEntity<?>) o;
		return version == that.version;
	}

	@Override
	public int hashCode() {
		return Objects.hash(super.hashCode(), version);
	}

}
