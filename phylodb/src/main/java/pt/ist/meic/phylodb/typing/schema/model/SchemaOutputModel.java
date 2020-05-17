package pt.ist.meic.phylodb.typing.schema.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import pt.ist.meic.phylodb.io.output.OutputModel;
import pt.ist.meic.phylodb.utils.service.Entity;

import java.util.Objects;

public class SchemaOutputModel implements OutputModel {

	@JsonInclude(JsonInclude.Include.NON_NULL)
	protected String taxon_id;
	protected String id;
	protected long version;
	protected boolean deprecated;

	public SchemaOutputModel() {
	}

	public SchemaOutputModel(Schema schema) {
		this.taxon_id = schema.getPrimaryKey().getTaxonId();
		this.id = schema.getPrimaryKey().getId();
		this.version = schema.getVersion();
		this.deprecated = schema.isDeprecated();
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

	@JsonIgnoreProperties({"taxon_id"})
	public static class Resumed extends SchemaOutputModel {

		public Resumed() {
		}

		public Resumed(Schema schema) {
			super(schema);
		}

	}

}
