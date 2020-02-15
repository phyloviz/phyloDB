package pt.ist.meic.phylodb.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.io.UnsupportedEncodingException;

@Component
public class MockHttp {

	@Autowired
	private MockMvc mvc;

	@Autowired
	private ObjectMapper objectMapper;

	public <T> T parseResult(Class<T> _class,  MockHttpServletResponse response) throws JsonProcessingException, UnsupportedEncodingException {
		return objectMapper.readValue(response.getContentAsString(), _class);
	}

	public MockHttpServletResponse get(String uri) throws Exception {
		return executeRequest(() -> MockMvcRequestBuilders.get(uri));
	}

	public <T> MockHttpServletResponse post(String uri, T data) throws Exception {
		return executeRequest(() -> MockMvcRequestBuilders.post(uri), data);
	}

	public <T> MockHttpServletResponse put(String uri, T data) throws Exception {
		return executeRequest(() -> MockMvcRequestBuilders.put(uri), data);

	}

	public <T> MockHttpServletResponse delete(String uri) throws Exception {
		return executeRequest(() -> MockMvcRequestBuilders.delete(uri));
	}

	private MockHttpServletResponse executeRequest(Supplier<MockHttpServletRequestBuilder> action) throws Exception {
		return mvc.perform(action.get()).andReturn().getResponse();
	}

	private <T> MockHttpServletResponse executeRequest(Supplier<MockHttpServletRequestBuilder> action, T data) throws Exception {
		return mvc.perform(action.get().contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(data))).andReturn().getResponse();
	}

}
