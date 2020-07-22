package pt.ist.meic.phylodb.phylogeny.locus.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import pt.ist.meic.phylodb.io.output.OutputModel;
import pt.ist.meic.phylodb.utils.service.VersionedEntity;

import java.util.Objects;

/**
 * A LocusOutputModel is an output model for a locus
 * <p>
 * A LocusOutputModel contains the {@link #taxon_id}, and {@link #id} fields which identify the locus,
 * and also contains the {@link #version}, and {@link #deprecated} fields which are the version of the locus, and the existence status respectively.
 */
public class LocusOutputModel implements OutputModel {

	protected String taxon_id;
	protected String id;
	protected long version;
	protected boolean deprecated;

	public LocusOutputModel() {
	}

	public LocusOutputModel(Locus locus) {
		this.taxon_id = locus.getPrimaryKey().getTaxonId();
		this.id = locus.getPrimaryKey().getId();
		this.version = locus.getVersion();
		this.deprecated = locus.isDeprecated();
	}

	public LocusOutputModel(VersionedEntity<Locus.PrimaryKey> reference) {
		this.taxon_id = reference.getPrimaryKey().getTaxonId();
		this.id = reference.getPrimaryKey().getId();
		this.version = reference.getVersion();
		this.deprecated = reference.isDeprecated();
	}

	public String getTaxon_id() {
		return taxon_id;
	}

	public String getId() {
		return id;
	}

	public long getVersion() {
		return version;
	}

	public boolean isDeprecated() {
		return deprecated;
	}

	@Override
	public ResponseEntity<LocusOutputModel> toResponseEntity() {
		return ResponseEntity.status(HttpStatus.OK).body(this);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		LocusOutputModel that = (LocusOutputModel) o;
		return version == that.version &&
				deprecated == that.deprecated &&
				Objects.equals(taxon_id, that.taxon_id) &&
				Objects.equals(id, that.id);
	}

	/**
	 * A LocusOutputModel.Resumed is the resumed information of a locus output model
	 * <p>
	 * A LocusOutputModel.Resumed is constituted by the {@link #id} field which is the id of the locus,
	 * and by the {@link #version} field which is the version of the locus.
	 */
	@JsonIgnoreProperties({"taxon_id", "deprecated"})
	public static class Resumed extends LocusOutputModel {

		public Resumed() {
		}

		public Resumed(VersionedEntity<Locus.PrimaryKey> locus) {
			super(locus);
		}

	}

}
