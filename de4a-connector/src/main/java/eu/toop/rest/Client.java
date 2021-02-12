package eu.toop.rest;
 

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

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
import org.springframework.util.StringUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import com.helger.commons.collection.iterate.IterableIterator;
import com.helger.xsds.bdxr.smp1.ServiceMetadataType;
import com.helger.xsds.bdxr.smp1.SignedServiceMetadataType;

import eu.de4a.conn.api.rest.Ack;
import eu.de4a.conn.api.smp.NodeInfo;
import eu.toop.as4.client.ResponseWrapper;
import eu.toop.rest.model.EvidenceService;
import eu.toop.rest.model.IssuingAuthority;
 
 

@Component
public class Client { 
	@Autowired
	private RestTemplate restTemplate; 
	@Value("${smp.endpoint}")
	private String smpEndpoint;
	@Value("${idk.endpoint}")
	private String idkEndpoint;
	private static final Logger logger =  LoggerFactory.getLogger (Client.class);	
	
	public NodeInfo getNodeInfo (String service) {
		 logger.debug("Consulta SMP {}", service);
		 final String separator = ":";
		 final String doubleSeparator = "::";
		 
		 //Consultamos al SMP el ServiceMetadata a traves del participantId y documentTypeId
		 //ej.: iso6523-actorid-upis:service::9921:ESS2833002E:BirthCertificate:1.0
		 List<String> serviceParams = Arrays.asList(service.split(":service::"));
		 String scheme = serviceParams.get(0);
		 List<String> docParams = Arrays.asList(serviceParams.get(1).split(separator));
		 String participantId = docParams.get(0) + separator + docParams.get(1);
		 String docId = docParams.get(2) + (docParams.size() > 3 ? separator + docParams.get(3) : "");
		 
		 
		 StringBuilder uri = new StringBuilder(smpEndpoint);
		 uri.append(scheme).append(doubleSeparator).append(participantId).append("/services/").append(scheme)
		 	.append(doubleSeparator).append(docId);
		 		 
		 NodeInfo nodeInfo = new NodeInfo();
		 try {
			 SignedServiceMetadataType signedServiceMetadata = restTemplate.getForObject(uri.toString(), SignedServiceMetadataType.class);
			 ServiceMetadataType serviceMetadata = signedServiceMetadata.getServiceMetadata();		 
		 
			 nodeInfo.setParticipantIdentifier(serviceMetadata.getServiceInformation().getParticipantIdentifier().getValue());
			 nodeInfo.setDocumentIdentifier(serviceMetadata.getServiceInformation().getDocumentIdentifier().getValue());
			 nodeInfo.setEndpointURI(serviceMetadata.getServiceInformation().getProcessList().getProcessAtIndex(0)
					 .getServiceEndpointList().getEndpointAtIndex(0).getEndpointURI());
			 nodeInfo.setCertificate(serviceMetadata.getServiceInformation().getProcessList().getProcessAtIndex(0)
					 .getServiceEndpointList().getEndpointAtIndex(0).getCertificate());
		 } catch (NullPointerException nPe) {
			 logger.warn("Se ha producido un error en el parseo de la respuesta SMP", nPe);
			 return new NodeInfo();
		 } catch (HttpClientErrorException | HttpServerErrorException httpClientOrServerExc) {
			 logger.error("No se ha encontrado información del servicio en el servidor SMP");
			 return new NodeInfo();
		 }
		 
		 return nodeInfo;
	}
	
	public IssuingAuthority getIssuingAuthority(String canonicalEvidenceType, String countryCode) {
		
		StringBuilder uri = new StringBuilder(idkEndpoint);
		uri.append(canonicalEvidenceType);
		uri.append("/").append(countryCode);
		
		return restTemplate.getForObject(uri.toString(), IssuingAuthority.class);
	}
	
	public EvidenceService getEvidenceService(String canonicalEvidenceType, String countryCode, String ...args) {
		
		StringBuilder uri = new StringBuilder(idkEndpoint);
		uri.append(canonicalEvidenceType);
		uri.append("/").append(countryCode);
		if(!StringUtils.isEmpty(args)) {
			Iterator<String> it = new IterableIterator<>(args);
			while(it.hasNext()) {
				String nameParam = it.next();
				if(it.hasNext()) {
					String valueParam = it.next();
					uri.append("?").append(nameParam);
					uri.append("=").append(valueParam);
				}
			}
		}
		
		return restTemplate.getForObject(uri.toString(), EvidenceService.class);
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
