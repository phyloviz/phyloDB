package pt.ist.meic.phylodb.phylogeny.locus.model;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import pt.ist.meic.phylodb.io.output.OutputModel;
import pt.ist.meic.phylodb.utils.service.Reference;

import java.util.Objects;

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

	public LocusOutputModel(String taxonId, Reference<String> r) {
		this.taxon_id =  taxonId;
		this.id = r.getPrimaryKey();
		this.version = r.getVersion();
		this.deprecated = r.isDeprecated();
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

}
