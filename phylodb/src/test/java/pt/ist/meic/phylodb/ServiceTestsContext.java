package pt.ist.meic.phylodb;

import org.mockito.InjectMocks;
import org.springframework.boot.test.mock.mockito.MockBean;
import pt.ist.meic.phylodb.phylogeny.allele.AlleleRepository;
import pt.ist.meic.phylodb.phylogeny.allele.AlleleService;
import pt.ist.meic.phylodb.phylogeny.locus.LocusRepository;
import pt.ist.meic.phylodb.phylogeny.locus.LocusService;
import pt.ist.meic.phylodb.phylogeny.taxon.TaxonRepository;
import pt.ist.meic.phylodb.phylogeny.taxon.TaxonService;
import pt.ist.meic.phylodb.security.authentication.user.UserRepository;
import pt.ist.meic.phylodb.security.authentication.user.UserService;
import pt.ist.meic.phylodb.security.authorization.project.ProjectRepository;
import pt.ist.meic.phylodb.security.authorization.project.ProjectService;
import pt.ist.meic.phylodb.typing.dataset.DatasetRepository;
import pt.ist.meic.phylodb.typing.dataset.DatasetService;
import pt.ist.meic.phylodb.typing.isolate.IsolateRepository;
import pt.ist.meic.phylodb.typing.isolate.IsolateService;
import pt.ist.meic.phylodb.typing.profile.ProfileRepository;
import pt.ist.meic.phylodb.typing.profile.ProfileService;
import pt.ist.meic.phylodb.typing.schema.SchemaRepository;
import pt.ist.meic.phylodb.typing.schema.SchemaService;

public abstract class ServiceTestsContext extends TestContext {

	@MockBean
	protected UserRepository userRepository;
	@MockBean
	protected ProjectRepository projectRepository;
	@MockBean
	protected TaxonRepository taxonRepository;
	@MockBean
	protected LocusRepository locusRepository;
	@MockBean
	protected AlleleRepository alleleRepository;
	@MockBean
	protected SchemaRepository schemaRepository;
	@MockBean
	protected DatasetRepository datasetRepository;
	@MockBean
	protected ProfileRepository profileRepository;
	@MockBean
	protected IsolateRepository isolateRepository;

	@InjectMocks
	protected UserService userService;
	@InjectMocks
	protected ProjectService projectService;
	@InjectMocks
	protected TaxonService taxonService;
	@InjectMocks
	protected LocusService locusService;
	@InjectMocks
	protected AlleleService alleleService;
	@InjectMocks
	protected SchemaService schemaService;
	@InjectMocks
	protected DatasetService datasetService;
	@InjectMocks
	protected ProfileService profileService;
	@InjectMocks
	protected IsolateService isolateService;
}