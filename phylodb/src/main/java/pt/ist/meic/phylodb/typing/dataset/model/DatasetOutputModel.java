package pt.ist.meic.phylodb.typing.dataset.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import pt.ist.meic.phylodb.io.output.OutputModel;

import java.util.Objects;

public class DatasetOutputModel implements OutputModel {

	@JsonInclude(JsonInclude.Include.NON_NULL)
	protected String project_id;
	protected String id;
	protected long version;
	protected boolean deprecated;

	public DatasetOutputModel() {
	}

	public DatasetOutputModel(Dataset dataset) {
		this.project_id = dataset.getPrimaryKey().getProjectId();
		this.id = dataset.getPrimaryKey().getId();
		this.version = dataset.getVersion();
		this.deprecated = dataset.isDeprecated();
	}

	public String getProject_id() {
		return project_id;
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
	public ResponseEntity<DatasetOutputModel> toResponseEntity() {
		return ResponseEntity.status(HttpStatus.OK).body(this);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		DatasetOutputModel that = (DatasetOutputModel) o;
		return version == that.version &&
				deprecated == that.deprecated &&
				Objects.equals(project_id, that.project_id) &&
				Objects.equals(id, that.id);
	}


	@JsonIgnoreProperties({"project_id", "deprecated"})
	public static class Resumed extends DatasetOutputModel {

		public Resumed() {
		}

		public Resumed(Dataset dataset) {
			super(dataset);
		}

	}

}
