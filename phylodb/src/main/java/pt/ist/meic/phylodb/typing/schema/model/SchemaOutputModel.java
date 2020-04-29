package pt.ist.meic.phylodb.typing.schema.model;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import pt.ist.meic.phylodb.io.output.OutputModel;
import pt.ist.meic.phylodb.utils.service.Entity;

import java.util.Objects;

public class SchemaOutputModel implements OutputModel {

	protected String taxon_id;
	protected String id;
	protected long version;
	protected boolean deprecated;

	public SchemaOutputModel() {
	}

	public SchemaOutputModel(Schema dataset) {
		this.taxon_id = dataset.getPrimaryKey().getTaxonId();
		this.id = dataset.getPrimaryKey().getId();
		this.version = dataset.getVersion();
		this.deprecated = dataset.isDeprecated();
	}

	public SchemaOutputModel(Entity<Schema.PrimaryKey> reference) {
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
	public ResponseEntity<SchemaOutputModel> toResponseEntity() {
		return ResponseEntity.status(HttpStatus.OK).body(this);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		SchemaOutputModel that = (SchemaOutputModel) o;
		return version == that.version &&
				deprecated == that.deprecated &&
				Objects.equals(taxon_id, that.taxon_id) &&
				Objects.equals(id, that.id);
	}

}
