package pt.ist.meic.phylodb.unit.security;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.HandlerMapping;
import pt.ist.meic.phylodb.analysis.inference.InferenceController;
import pt.ist.meic.phylodb.phylogeny.allele.AlleleController;
import pt.ist.meic.phylodb.phylogeny.allele.model.AlleleInputModel;
import pt.ist.meic.phylodb.phylogeny.taxon.TaxonController;
import pt.ist.meic.phylodb.phylogeny.taxon.model.TaxonInputModel;
import pt.ist.meic.phylodb.security.SecurityInterceptor;
import pt.ist.meic.phylodb.security.authorization.AuthorizationInterceptor;
import pt.ist.meic.phylodb.security.authorization.Role;
import pt.ist.meic.phylodb.security.authorization.Visibility;
import pt.ist.meic.phylodb.security.project.ProjectService;
import pt.ist.meic.phylodb.security.project.model.Project;
import pt.ist.meic.phylodb.security.user.model.User;
import pt.ist.meic.phylodb.unit.Context;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Collections;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static pt.ist.meic.phylodb.security.SecurityInterceptor.PROVIDER;
import static pt.ist.meic.phylodb.security.SecurityInterceptor.ROLE;

public class AuthorizationInterceptorTests extends Context {

	private static final String ID = "4f809af7-2c99-43f7-b674-4843c77384c7";
	@Autowired
	private AuthorizationInterceptor interceptor;
	@MockBean
	private ProjectService service;

	private static Stream<Arguments> preHandle_params() throws NoSuchMethodException {
		MockHttpServletResponse response = new MockHttpServletResponse();
		User.PrimaryKey userKey = new User.PrimaryKey("id", "provider"), otherKey = new User.PrimaryKey("id2", "provider2");
		Project userProject = new Project(ID, "t", Visibility.PRIVATE, "t", new User.PrimaryKey[]{userKey});
		Project userPublicProject = new Project(ID, "t", Visibility.PUBLIC, "t", new User.PrimaryKey[]{userKey});
		Project otherPublicProject = new Project(ID, "t", Visibility.PUBLIC, "t", new User.PrimaryKey[]{otherKey});
		Project otherProject = new Project(ID, "t", Visibility.PRIVATE, "t", new User.PrimaryKey[]{otherKey});
		HandlerMethod handler1 = new HandlerMethod(new TaxonController(null), TaxonController.class.getMethod("saveTaxon", String.class, TaxonInputModel.class));
		HandlerMethod handler2 = new HandlerMethod(new AlleleController(null), AlleleController.class.getMethod("saveAllele", String.class, String.class, String.class, String.class, AlleleInputModel.class));
		HandlerMethod handler3 = new HandlerMethod(new AlleleController(null), AlleleController.class.getMethod("getAlleles", String.class, String.class, String.class, int.class));
		HandlerMethod handler4 = new HandlerMethod(new TaxonController(null), TaxonController.class.getMethod("getTaxa", int.class));
		HandlerMethod handler5 = new HandlerMethod(new InferenceController(null), InferenceController.class.getMethod("postInference", String.class, String.class, String.class, String.class, MultipartFile.class));
		HandlerMethod handler6 = new HandlerMethod(new InferenceController(null), InferenceController.class.getMethod("getInferences", String.class, String.class, int.class));
		return Stream.of(Arguments.of(request(userKey, Role.ADMIN, null), response, handler1, null, true),
				Arguments.of(request(userKey, Role.ADMIN, null), response, handler2, null, true),
				Arguments.of(request(userKey, Role.USER, null), response, handler1, null, false),
				Arguments.of(request(userKey, Role.USER, ID), response, handler3, userProject, true),
				Arguments.of(request(userKey, Role.USER, ID), response, handler3, userPublicProject, true),
				Arguments.of(request(userKey, Role.USER, ID), response, handler3, otherProject, false),
				Arguments.of(request(userKey, Role.USER, ID), response, handler3, otherPublicProject, true),
				Arguments.of(request(userKey, Role.USER, ID), response, handler2, userProject, true),
				Arguments.of(request(userKey, Role.USER, ID), response, handler2, userPublicProject, true),
				Arguments.of(request(userKey, Role.USER, ID), response, handler2, otherProject, false),
				Arguments.of(request(userKey, Role.USER, ID), response, handler2, otherPublicProject, false),
				Arguments.of(request(userKey, Role.USER, null), response, handler4, null, true),
				Arguments.of(request(userKey, Role.ADMIN, null), response, handler4, null, true),
				Arguments.of(request(userKey, Role.ADMIN, null), response, handler2, null, true),
				Arguments.of(request(userKey, Role.USER, null), response, handler2, null, false),
				Arguments.of(request(userKey, Role.USER, ID), response, handler6, userProject, true),
				Arguments.of(request(userKey, Role.USER, ID), response, handler6, userPublicProject, true),
				Arguments.of(request(userKey, Role.USER, ID), response, handler6, otherProject, false),
				Arguments.of(request(userKey, Role.USER, ID), response, handler6, otherPublicProject, true),
				Arguments.of(request(userKey, Role.USER, ID), response, handler5, userProject, true),
				Arguments.of(request(userKey, Role.USER, ID), response, handler5, userPublicProject, true),
				Arguments.of(request(userKey, Role.USER, ID), response, handler5, otherProject, false),
				Arguments.of(request(userKey, Role.USER, ID), response, handler5, otherPublicProject, true));
	}

	private static MockHttpServletRequest request(User.PrimaryKey key, Role userRole, String projectId) {
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.setAttribute(SecurityInterceptor.ID, key.getId());
		request.setAttribute(PROVIDER, key.getProvider());
		request.setAttribute(ROLE, userRole);
		request.setAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE, Collections.emptyMap());
		if (projectId != null)
			request.setParameter(AuthorizationInterceptor.PROJECT, projectId);
		return request;
	}

	@ParameterizedTest
	@MethodSource("preHandle_params")
	public void preHandle(HttpServletRequest req, HttpServletResponse res, HandlerMethod hm, Project project, boolean expected) {
		MockitoAnnotations.initMocks(this);
		Mockito.when(service.getProject(ID, -1)).thenReturn(Optional.ofNullable(project));
		boolean result = interceptor.handle(req, res, hm);
		assertEquals(expected, result);
	}

}
