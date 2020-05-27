package pt.ist.meic.phylodb.typing.schema;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pt.ist.meic.phylodb.phylogeny.locus.LocusRepository;
import pt.ist.meic.phylodb.typing.schema.model.Schema;
import pt.ist.meic.phylodb.utils.service.VersionedEntity;

import java.util.List;
import java.util.Optional;

@Service
public class SchemaService {

	private LocusRepository locusRepository;
	private SchemaRepository schemaRepository;

	public SchemaService(LocusRepository locusRepository, SchemaRepository schemaRepository) {
		this.locusRepository = locusRepository;
		this.schemaRepository = schemaRepository;
	}

	@Transactional(readOnly = true)
	public Optional<List<VersionedEntity<Schema.PrimaryKey>>> getSchemas(String taxonId, int page, int limit) {
		return schemaRepository.findAllEntities(page, limit, taxonId);
	}

	@Transactional(readOnly = true)
	public Optional<Schema> getSchema(String taxonId, String schemaId, long version) {
		return schemaRepository.find(new Schema.PrimaryKey(taxonId, schemaId), version);
	}

	@Transactional
	public boolean saveSchema(Schema schema) {
		if (schema == null)
			return false;
		String[] lociIds = schema.getLociIds().toArray(new String[0]);
		Optional<Schema> dbSchema = schemaRepository.find(schema.getPrimaryKey().getTaxonId(), schema.getType(), lociIds);
		if (locusRepository.anyMissing(schema.getLociReferences()) ||
				(dbSchema.isPresent() && !dbSchema.get().getPrimaryKey().equals(schema.getPrimaryKey())))
			return false;
		return schemaRepository.save(schema);
	}

	@Transactional
	public boolean deleteSchema(String taxonId, String schemaId) {
		return schemaRepository.remove(new Schema.PrimaryKey(taxonId, schemaId));
	}

}
