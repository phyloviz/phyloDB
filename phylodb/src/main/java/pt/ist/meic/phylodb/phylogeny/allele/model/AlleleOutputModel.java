package pt.ist.meic.phylodb.phylogeny.allele.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import pt.ist.meic.phylodb.io.output.OutputModel;
import pt.ist.meic.phylodb.utils.service.VersionedEntity;

import java.util.Objects;

/**
 * An AlleleOutputModel is an output model for an allele
 * <p>
 * An AlleleOutputModel contains the {@link #taxon_id}, {@link #locus_id}, {@link #id}, {@link #project_id} fields which identify the allele,
 * and also contains the {@link #version}, and {@link #deprecated} fields which are the version of the allele, and the existence status respectively.
 */
public class AlleleOutputModel implements OutputModel {

	protected String taxon_id;
	protected String locus_id;
	protected String id;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	protected String project_id;
	protected long version;
	protected boolean deprecated;

	public AlleleOutputModel() {
	}

	public AlleleOutputModel(Allele allele) {
		this.taxon_id = allele.getPrimaryKey().getTaxonId();
		this.locus_id = allele.getPrimaryKey().getLocusId();
		this.id = allele.getPrimaryKey().getId();
		this.project_id = allele.getPrimaryKey().getProjectId();
		this.version = allele.getVersion();
		this.deprecated = allele.isDeprecated();
	}

	public AlleleOutputModel(VersionedEntity<Allele.PrimaryKey> reference) {
		this.taxon_id = reference.getPrimaryKey().getTaxonId();
		this.locus_id = reference.getPrimaryKey().getLocusId();
		this.id = reference.getPrimaryKey().getId();
		this.project_id = reference.getPrimaryKey().getProjectId();
		this.version = reference.getVersion();
		this.deprecated = reference.isDeprecated();
	}

	public String getTaxon_id() {
		return taxon_id;
	}

	public String getLocus_id() {
		return locus_id;
	}

	public String getId() {
		return id;
	}

	public String getProject_id() {
		return project_id;
	}

	public long getVersion() {
		return version;
	}

	public boolean isDeprecated() {
		return deprecated;
	}

	@Override
	public ResponseEntity<AlleleOutputModel> toResponseEntity() {
		return ResponseEntity.status(HttpStatus.OK).body(this);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		AlleleOutputModel that = (AlleleOutputModel) o;
		return version == that.version &&
				deprecated == that.deprecated &&
				Objects.equals(taxon_id, that.taxon_id) &&
				Objects.equals(locus_id, that.locus_id) &&
				Objects.equals(id, that.id);
	}

	/**
	 * An AlleleOutputModel.Resumed is the resumed information of an allele output model
	 * <p>
	 * An AlleleOutputModel.Resumed is constituted by the {@link #id} field which is the id of the allele,
	 * and by the {@link #version} field which is the version of the allele.
	 */
	@JsonIgnoreProperties({"taxon_id", "locus_id", "deprecated"})
	public static class Resumed extends AlleleOutputModel {

		public Resumed() {
		}

		public Resumed(Allele allele) {
			super(allele);
		}

		public Resumed(VersionedEntity<Allele.PrimaryKey> allele) {
			super(allele);
		}

	}

}
