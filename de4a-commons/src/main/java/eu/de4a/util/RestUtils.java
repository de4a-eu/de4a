package eu.de4a.util;


import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.impl.client.HttpClients;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
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
		RestTemplate plantilla = new RestTemplate();
		HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
		requestFactory
				.setHttpClient(HttpClients.custom().setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE).build());
		plantilla.setRequestFactory(requestFactory);
		
		return plantilla;
	}

}
