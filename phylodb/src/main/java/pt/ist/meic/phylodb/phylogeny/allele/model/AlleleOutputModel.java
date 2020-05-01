package pt.ist.meic.phylodb.phylogeny.allele.model;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import pt.ist.meic.phylodb.io.output.OutputModel;
import pt.ist.meic.phylodb.utils.service.Entity;

import java.util.Objects;

public class AlleleOutputModel implements OutputModel {

	protected String taxon_id;
	protected String locus_id;
	protected String id;
	protected long version;
	protected boolean deprecated;

	public AlleleOutputModel() {
	}

	public AlleleOutputModel(Allele allele) {
		this.taxon_id = allele.getPrimaryKey().getTaxonId();
		this.locus_id = allele.getPrimaryKey().getLocusId();
		this.id = allele.getPrimaryKey().getId();
		this.version = allele.getVersion();
		this.deprecated = allele.isDeprecated();
	}

	public AlleleOutputModel(Entity<Allele.PrimaryKey> reference) {
		this.taxon_id = reference.getPrimaryKey().getTaxonId();
		this.locus_id = reference.getPrimaryKey().getLocusId();
		this.id = reference.getPrimaryKey().getId();
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

}