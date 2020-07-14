package pt.ist.meic.phylodb.unit;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import pt.ist.meic.phylodb.analysis.inference.InferenceService;
import pt.ist.meic.phylodb.analysis.visualization.VisualizationService;
import pt.ist.meic.phylodb.job.JobService;
import pt.ist.meic.phylodb.phylogeny.allele.AlleleService;
import pt.ist.meic.phylodb.phylogeny.locus.LocusService;
import pt.ist.meic.phylodb.phylogeny.taxon.TaxonService;
import pt.ist.meic.phylodb.security.authentication.AuthenticationInterceptor;
import pt.ist.meic.phylodb.security.authorization.AuthorizationInterceptor;
import pt.ist.meic.phylodb.security.project.ProjectService;
import pt.ist.meic.phylodb.security.user.UserService;
import pt.ist.meic.phylodb.typing.dataset.DatasetService;
import pt.ist.meic.phylodb.typing.isolate.IsolateService;
import pt.ist.meic.phylodb.typing.profile.ProfileService;
import pt.ist.meic.phylodb.typing.schema.SchemaService;

import java.io.UnsupportedEncodingException;

public abstract class ControllerTestsContext extends Context {

	@Autowired
	private MockMvc mvc;
	@Autowired
	private ObjectMapper objectMapper;

	protected <T> T parseResult(Class<T> _class, MockHttpServletResponse response) throws JsonProcessingException, UnsupportedEncodingException {
		return objectMapper.readValue(response.getContentAsString(), _class);
	}

	protected MockHttpServletResponse executeRequest(MockHttpServletRequestBuilder action, MediaType mediatype) throws Exception {
		return mvc.perform(action.accept(mediatype)).andReturn().getResponse();
	}

	protected <T> MockHttpServletResponse executeRequest(MockHttpServletRequestBuilder action, T data) throws Exception {
		return mvc.perform(action.contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(data))).andReturn().getResponse();
	}

	protected MockHttpServletResponse executeFileRequest(MockHttpServletRequestBuilder action) throws Exception {
		return mvc.perform(action).andReturn().getResponse();
	}

	@MockBean
	protected AuthenticationInterceptor authenticationInterceptor;
	@MockBean
	protected AuthorizationInterceptor authorizationInterceptor;
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
