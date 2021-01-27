package eu.toop.rest;
 

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContexts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import eu.de4a.conn.api.rest.Ack;
import eu.de4a.conn.api.smp.NodeInfo;
import eu.de4a.util.DE4AConstants;
import eu.toop.as4.client.ResponseWrapper;
 
 

@Component
public class Client { 
	@Autowired
	private RestTemplate restTemplate; 
	@Value("${smp.endpoint}")
	private String smpEndpoint;
	private static final Logger logger =  LoggerFactory.getLogger (Client.class);
	public NodeInfo  getNodeInfo ( String dataOwnerdI,String evidenceServiceUri){
		 logger.debug("Gimme node info AS4 {}",dataOwnerdI);
		 String uri =String.format(smpEndpoint, dataOwnerdI,evidenceServiceUri); //"http://localhost:8382/smp/whois?dataOwnerId="+dataOwnerdI+"&serviceURI="+evidenceServiceUri; 
		 //String uri = "https://des-de4a.redsara.es/de4a-smp/whois?dataOwnerId="+dataOwnerdI+"&serviceURI="+evidenceServiceUri; 
		 return restTemplate.getForObject(uri, NodeInfo.class);
	}
	public void pushEvidence(String endpoint,ResponseWrapper response) { 
		logger.debug("Sending response {}",endpoint);
		// String uri = "http://localhost:8083/de4a-connector/request?urlReturn=http://localhost:8682/de4a-evaluator/ReturnPage&evaluatorId="+dataOwnerdI+"&@evaluatorId="+evidenceServiceUri+"&requestId=777"; 
		 RestTemplate plantilla = new RestTemplate();
		HttpComponentsClientHttpRequestFactory requestFactory =
		                new HttpComponentsClientHttpRequestFactory();
		requestFactory.setHttpClient(HttpClients.custom().setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE).build());
		plantilla.setRequestFactory(requestFactory);
		MultiValueMap<String, Object> body
		  = new LinkedMultiValueMap<>();
		response.getAttacheds().forEach(p->{
//			body.add("file", p); 
			body.add("file", new FileSystemResource(convert(p)));
		});   
 		HttpHeaders headers = new HttpHeaders();
 		headers.setContentType(MediaType.MULTIPART_FORM_DATA);
// 	 List<HttpMessageConverter<?>> messageConverters = new ArrayList<>();
// 	 MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
// 	 converter.setSupportedMediaTypes(Collections.singletonList(MediaType.APPLICATION_XML  ));
// 	 messageConverters.add(converter);
// 	 plantilla.setMessageConverters(messageConverters);
 		HttpEntity<MultiValueMap<String, Object>> requestEntity = new    HttpEntity<MultiValueMap<String, Object>>( 	body, headers); 
		plantilla.postForEntity(endpoint,requestEntity, Ack.class);  
		
	 
	}  
	  public static File convert(MultipartFile file)
	  {    
	    File convFile = new File(file.getOriginalFilename());
	    try {
	        convFile.createNewFile();
	          FileOutputStream fos = new FileOutputStream(convFile); 
	            fos.write(file.getBytes());
	            fos.close(); 
	    } catch (IOException e) {
	        // TODO Auto-generated catch block
	        e.printStackTrace();
	    } 

	    return convFile;
	 }
//	public void  pushEvidence( String endpoint,Object o) throws RestClientException{
//		 logger.debug("Pushing object to evaluator {}",endpoint);   
//		 HttpHeaders headers = new HttpHeaders();
//		 headers.setContentType(MediaType.APPLICATION_JSON);
//		 HttpEntity<Document> request = new HttpEntity<Document>((Document)o, headers);
//		 final RestTemplate restTemplate = new RestTemplate();
////		 List<HttpMessageConverter<?>> messageConverters = new ArrayList<>();
////		 MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
////		 converter.setSupportedMediaTypes(Collections.singletonList(MediaType.APPLICATION_XML  ));
////		 messageConverters.add(converter);
////		 restTemplate.setMessageConverters(messageConverters);
//		 restTemplate.postForLocation( endpoint, request );
//		 
//		 
//		 Document reponse = (Document)o; 
//		 HttpEntity<Document> requestEntity = new HttpEntity<>(reponse, headers); 
//		 URI uri = restTemplate.postForLocation(endpoint, requestEntity );
//	} 
	
	public void pushEvidence2(String endpoint,org.apache.http.HttpEntity multipart) {
		//CloseableHttpClient client = HttpClients.createDefault();
		SSLConnectionSocketFactory scsf=null;
		try {
			  scsf =new SSLConnectionSocketFactory(
				     SSLContexts.custom().loadTrustMaterial(null, new TrustSelfSignedStrategy()).build(), 
				        NoopHostnameVerifier.INSTANCE); 
			  
		} catch (KeyManagementException | NoSuchAlgorithmException | KeyStoreException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		CloseableHttpClient client =HttpClients.custom().setSSLSocketFactory(scsf).build();
	    HttpPost httpPost = new HttpPost(endpoint); 
	    httpPost.setEntity(multipart);
	    CloseableHttpResponse response=null;
	    try {
		  response = client.execute(httpPost);
		} catch (IOException e) {
			logger.error("Error pushing evidence to evaluator",e);
		} finally {
			 try {
				 client.close();
				 response.close();
			} catch (IOException e) {  
			}
		}
	   
	
	}
}
