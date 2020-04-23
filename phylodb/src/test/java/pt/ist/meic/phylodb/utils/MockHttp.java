package pt.ist.meic.phylodb.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.io.UnsupportedEncodingException;

@Component
public class MockHttp {

	@Autowired
	private MockMvc mvc;

	@Autowired
	private ObjectMapper objectMapper;

	public <T> T parseResult(Class<T> _class, MockHttpServletResponse response) throws JsonProcessingException, UnsupportedEncodingException {
		return objectMapper.readValue(response.getContentAsString(), _class);
	}

	public MockHttpServletResponse executeRequest(MockHttpServletRequestBuilder action, MediaType mediatype) throws Exception {
		return mvc.perform(action.accept(mediatype)).andReturn().getResponse();
	}

	public <T> MockHttpServletResponse executeRequest(MockHttpServletRequestBuilder action, MediaType mediatype, T data) throws Exception {
		return mvc.perform(action.contentType(mediatype).content(objectMapper.writeValueAsString(data))).andReturn().getResponse();
	}

}
