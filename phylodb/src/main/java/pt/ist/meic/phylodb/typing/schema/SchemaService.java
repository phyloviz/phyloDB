package pt.ist.meic.phylodb.typing.schema;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pt.ist.meic.phylodb.phylogeny.locus.LocusRepository;
import pt.ist.meic.phylodb.typing.Method;
import pt.ist.meic.phylodb.typing.schema.model.Schema;
import pt.ist.meic.phylodb.utils.db.EntityRepository;
import pt.ist.meic.phylodb.utils.service.Reference;

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
	public Optional<List<Schema>> getSchemas(String taxonId, int page, int limit) {
		return schemaRepository.findAll(page, limit, taxonId);
	}

	@Transactional(readOnly = true)
	public Optional<Schema> getSchema(String taxonId, String schemaId, int version) {
		return schemaRepository.find(new Schema.PrimaryKey(taxonId, schemaId), version);
	}

	@Transactional
	public boolean saveSchema(Schema schema) {
		String[] lociIds = schema.getLociIds().stream()
				.map(Reference::getPrimaryKey)
				.toArray(String[]::new);
		Optional<Schema> dbSchema = schemaRepository.find(schema.getPrimaryKey().getTaxonId(), lociIds, EntityRepository.CURRENT_VERSION_VALUE);
		if (!Method.exists(schema.getType()) || locusRepository.anyMissing(schema.getPrimaryKey().getTaxonId(), lociIds) ||
				(dbSchema.isPresent() && !dbSchema.get().getPrimaryKey().equals(schema.getPrimaryKey())))
			return false;
		return schemaRepository.save(schema);
	}

	@Transactional
	public boolean deleteSchema(String taxonId, String schemaId) {
		return schemaRepository.remove(new Schema.PrimaryKey(taxonId, schemaId));
	}

}
