package pt.ist.meic.phylodb.typing.dataset.model;

import pt.ist.meic.phylodb.io.input.InputModel;

import java.util.Optional;
import java.util.UUID;

/**
 * A DatasetInputModel is the input model for a dataset
 * <p>
 * A DatasetInputModel is constituted by the {@link #id} field to identify the dataset,
 * the {@link #description} that is the dataset and the {@link #taxon_id}, and {@link #schema_id}
 * fields which identifies the schema the dataset follows.
 */
public class DatasetInputModel implements InputModel<Dataset> {

	private String id;
	private String description;
	private String taxon_id;
	private String schema_id;

	public DatasetInputModel() {
	}

	public DatasetInputModel(String id, String description, String taxonId, String schemaId) {
		this.id = id;
		this.description = description;
		this.taxon_id = taxonId;
		this.schema_id = schemaId;
	}

	public String getId() {
		return id;
	}

	public String getDescription() {
		return description;
	}

	public String getTaxon_id() {
		return taxon_id;
	}

	public String getSchema_id() {
		return schema_id;
	}

	@Override
	public Optional<Dataset> toDomainEntity(String... params) {
		UUID id = params.length == 1 ? UUID.randomUUID() : UUID.fromString(params[1]);
		return (params.length != 1 && !params[1].equals(this.id)) || taxon_id == null || schema_id == null ? Optional.empty() :
				Optional.of(new Dataset(params[0], id.toString(), description, taxon_id, schema_id));
	}

}
