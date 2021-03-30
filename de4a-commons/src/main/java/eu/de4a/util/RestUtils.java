package eu.de4a.util;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.impl.client.HttpClients;
import org.springframework.http.MediaType;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.converter.xml.MappingJackson2XmlHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

public class RestUtils {
	
	RestUtils() {
		//empty constructor
	}
	
	/**
	 * 
	 * Obtain generic rest client template spring
	 * 
	 * @return template
	 *		rest template
	 */
	public static RestTemplate getRestTemplate() {
		HttpComponentsClientHttpRequestFactory httpComponentsClientHttpRequestFactory = new HttpComponentsClientHttpRequestFactory();
		httpComponentsClientHttpRequestFactory
		.setHttpClient(HttpClients.custom().setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE).build());
		RestTemplate template = new RestTemplate(httpComponentsClientHttpRequestFactory);
		List<HttpMessageConverter<?>> messageConverters = new ArrayList<>();
		MappingJackson2XmlHttpMessageConverter converter = new MappingJackson2XmlHttpMessageConverter();
		converter.setSupportedMediaTypes(Collections.singletonList(MediaType.ALL));
		messageConverters.add(converter);
		template.setMessageConverters(messageConverters);
		return new RestTemplate(httpComponentsClientHttpRequestFactory);
	}

}
