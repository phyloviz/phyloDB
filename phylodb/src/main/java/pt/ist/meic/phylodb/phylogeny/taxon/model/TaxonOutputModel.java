package pt.ist.meic.phylodb.phylogeny.taxon.model;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import pt.ist.meic.phylodb.io.output.OutputModel;

import java.util.Objects;

public class TaxonOutputModel implements OutputModel {

	protected String id;
	protected long version;
	protected boolean deprecated;

	public TaxonOutputModel() {
	}

	public TaxonOutputModel(Taxon taxon) {
		this.id = taxon.getPrimaryKey();
		this.version = taxon.getVersion();
		this.deprecated = taxon.isDeprecated();
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
	public ResponseEntity<TaxonOutputModel> toResponseEntity() {
		return ResponseEntity.status(HttpStatus.OK).body(this);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		TaxonOutputModel that = (TaxonOutputModel) o;
		return version == that.version &&
				deprecated == that.deprecated &&
				Objects.equals(id, that.id);
	}

}
