package pt.ist.meic.phylodb.unit;

import org.neo4j.ogm.model.Result;
import org.neo4j.ogm.session.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import pt.ist.meic.phylodb.analysis.inference.InferenceRepository;
import pt.ist.meic.phylodb.analysis.visualization.VisualizationRepository;
import pt.ist.meic.phylodb.job.JobRepository;
import pt.ist.meic.phylodb.phylogeny.allele.AlleleRepository;
import pt.ist.meic.phylodb.phylogeny.locus.LocusRepository;
import pt.ist.meic.phylodb.phylogeny.taxon.TaxonRepository;
import pt.ist.meic.phylodb.security.user.UserRepository;
import pt.ist.meic.phylodb.security.project.ProjectRepository;
import pt.ist.meic.phylodb.typing.dataset.DatasetRepository;
import pt.ist.meic.phylodb.typing.isolate.IsolateRepository;
import pt.ist.meic.phylodb.typing.profile.ProfileRepository;
import pt.ist.meic.phylodb.typing.schema.SchemaRepository;
import pt.ist.meic.phylodb.utils.db.Query;

import java.util.HashMap;

@Transactional
public abstract class RepositoryTestsContext extends Context {

	@Autowired
	protected Session session;

	@Autowired
	protected UserRepository userRepository;
	@Autowired
	protected TaxonRepository taxonRepository;
	@Autowired
	protected LocusRepository locusRepository;
	@Autowired
	protected AlleleRepository alleleRepository;
	@Autowired
	protected SchemaRepository schemaRepository;
	@Autowired
	protected ProjectRepository projectRepository;
	@Autowired
	protected DatasetRepository datasetRepository;
	@Autowired
	protected ProfileRepository profileRepository;
	@Autowired
	protected IsolateRepository isolateRepository;

	@Autowired
	protected InferenceRepository inferenceRepository;
	@Autowired
	protected VisualizationRepository visualizationRepository;

	@Autowired
	protected JobRepository jobRepository;

	protected Result query(Query query) {
		return session.query(query.getExpression(), query.getParameters());
	}

	protected final <T> T query(Class<T> _class, Query query) {
		return session.queryForObject(_class, query.getExpression(), query.getParameters());
	}

	protected void execute(Query query) {
		session.query(query.getExpression(), query.getParameters());
		session.clear();
	}

	protected int countNodes() {
		Integer count = session.queryForObject(Integer.class, "MATCH (n) RETURN COUNT(n)", new HashMap<>());
		return count == null ? 0 : count;
	}

	protected int countRelationships() {
		Integer count = session.queryForObject(Integer.class, "MATCH (n)-[r]->() RETURN COUNT(r)", new HashMap<>());
		return count == null ? 0 : count;
	}

}
