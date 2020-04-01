package pt.ist.meic.phylodb.typing.schema;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pt.ist.meic.phylodb.phylogeny.locus.LocusRepository;
import pt.ist.meic.phylodb.typing.schema.model.Schema;
import pt.ist.meic.phylodb.utils.db.Status;
import pt.ist.meic.phylodb.utils.service.Reference;

import java.util.List;
import java.util.Optional;

import static pt.ist.meic.phylodb.utils.db.Status.UNCHANGED;

@Service
public class SchemaService {

	private LocusRepository locusRepository;
	private SchemaRepository schemaRepository;

	public SchemaService(LocusRepository locusRepository, SchemaRepository schemaRepository) {
		this.locusRepository = locusRepository;
		this.schemaRepository = schemaRepository;
	}

	@Transactional(readOnly = true)
	public Optional<List<Schema>> getSchemas(String taxonId, int page, int limit) {
		return Optional.ofNullable(schemaRepository.findAll(page, limit, taxonId));
	}

	@Transactional(readOnly = true)
	public Optional<Schema> getSchema(String taxonId, String schemaId, int version) {
		return Optional.ofNullable(schemaRepository.find(new Schema.PrimaryKey(taxonId, schemaId), version));
	}

	@Transactional
	public Status saveSchema(Schema schema) {
		String[] lociIds = schema.getLociIds().stream()
				.map(Reference::getId)
				.toArray(String[]::new);
		Schema dbSchema = schemaRepository.find(schema.getTaxonId(), lociIds);
		if (locusRepository.anyMissing(schema.getTaxonId(), lociIds) ||
				(dbSchema != null && !dbSchema.getId().equals(schema.getId())))
			return UNCHANGED;
		return schemaRepository.save(schema);
	}

	@Transactional
	public Status deleteSchema(String taxonId, String schemaId) {
		return schemaRepository.remove(new Schema.PrimaryKey(taxonId, schemaId));
	}

}
