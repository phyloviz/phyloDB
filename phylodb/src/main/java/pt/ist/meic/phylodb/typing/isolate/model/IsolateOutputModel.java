package pt.ist.meic.phylodb.typing.isolate.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import pt.ist.meic.phylodb.io.output.OutputModel;
import pt.ist.meic.phylodb.utils.service.VersionedEntity;

import java.util.Objects;

/**
 * An IsolateOutputModel is an output model for an isolate
 * <p>
 * An IsolateOutputModel contains the {@link #project_id}, {@link #dataset_id}, {@link #id} fields which identify the isolate,
 * and also contains the {@link #version}, and {@link #deprecated} fields which are the version of the allele, and the existence status respectively.
 */
public class IsolateOutputModel implements OutputModel {

	@JsonInclude(JsonInclude.Include.NON_NULL)
	protected String project_id;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	protected String dataset_id;
	protected String id;
	protected long version;
	protected boolean deprecated;

	public IsolateOutputModel() {
	}

	public IsolateOutputModel(Isolate isolate) {
		this.project_id = isolate.getPrimaryKey().getProjectId();
		this.dataset_id = isolate.getPrimaryKey().getDatasetId();
		this.id = isolate.getPrimaryKey().getId();
		this.version = isolate.getVersion();
		this.deprecated = isolate.isDeprecated();
	}

	public IsolateOutputModel(VersionedEntity<Isolate.PrimaryKey> isolate) {
		this.project_id = isolate.getPrimaryKey().getProjectId();
		this.dataset_id = isolate.getPrimaryKey().getDatasetId();
		this.id = isolate.getPrimaryKey().getId();
		this.version = isolate.getVersion();
		this.deprecated = isolate.isDeprecated();
	}

	public String getProject_id() {
		return project_id;
	}

	public String getDataset_id() {
		return dataset_id;
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
	public ResponseEntity<IsolateOutputModel> toResponseEntity() {
		return ResponseEntity.status(HttpStatus.OK).body(this);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		IsolateOutputModel that = (IsolateOutputModel) o;
		return version == that.version &&
				deprecated == that.deprecated &&
				Objects.equals(project_id, that.project_id) &&
				Objects.equals(dataset_id, that.dataset_id) &&
				Objects.equals(id, that.id);
	}

	/**
	 * An IsolateOutputModel.Resumed is the resumed information of an isolate output model
	 * <p>
	 * An IsolateOutputModel.Resumed is constituted by the {@link #id} field which is the id of the isolate,
	 * and by the {@link #version} field which is the version of the isolate.
	 */
	@JsonIgnoreProperties({"project_id", "dataset_id", "deprecated"})
	public static class Resumed extends IsolateOutputModel {

		public Resumed() {
		}

		public Resumed(VersionedEntity<Isolate.PrimaryKey> isolate) {
			super(isolate);
		}

	}

}
