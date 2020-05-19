package pt.ist.meic.phylodb;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import pt.ist.meic.phylodb.analysis.inference.InferenceService;
import pt.ist.meic.phylodb.analysis.visualization.VisualizationService;
import pt.ist.meic.phylodb.job.JobService;
import pt.ist.meic.phylodb.phylogeny.allele.AlleleService;
import pt.ist.meic.phylodb.phylogeny.locus.LocusService;
import pt.ist.meic.phylodb.phylogeny.taxon.TaxonService;
import pt.ist.meic.phylodb.security.authentication.AuthenticationInterceptor;
import pt.ist.meic.phylodb.security.authentication.user.UserService;
import pt.ist.meic.phylodb.security.authorization.AuthorizationInterceptor;
import pt.ist.meic.phylodb.security.authorization.project.ProjectService;
import pt.ist.meic.phylodb.typing.dataset.DatasetService;
import pt.ist.meic.phylodb.typing.isolate.IsolateService;
import pt.ist.meic.phylodb.typing.profile.ProfileService;
import pt.ist.meic.phylodb.typing.schema.SchemaService;
import pt.ist.meic.phylodb.utils.MockHttp;

public abstract class ControllerTestsContext extends TestContext {

	@MockBean
	protected AuthenticationInterceptor authenticationInterceptor;
	@MockBean
	protected AuthorizationInterceptor authorizationInterceptor;
	@Autowired
	protected MockHttp http;

	@MockBean
	protected UserService userService;
	@MockBean
	protected ProjectService projectService;
	@MockBean
	protected TaxonService taxonService;
	@MockBean
	protected LocusService locusService;
	@MockBean
	protected AlleleService alleleService;
	@MockBean
	protected SchemaService schemaService;
	@MockBean
	protected DatasetService datasetService;
	@MockBean
	protected ProfileService profileService;
	@MockBean
	protected IsolateService isolateService;
	@MockBean
	protected InferenceService inferenceService;
	@MockBean
	protected VisualizationService visualizationService;
	@MockBean
	protected JobService jobService;


}
