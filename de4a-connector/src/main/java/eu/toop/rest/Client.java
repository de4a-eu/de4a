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
import org.springframework.util.CollectionUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import com.helger.commons.collection.iterate.IterableIterator;
import com.helger.xsds.bdxr.smp1.ProcessType;
import com.helger.xsds.bdxr.smp1.ServiceMetadataType;
import com.helger.xsds.bdxr.smp1.SignedServiceMetadataType;

import eu.de4a.conn.api.requestor.EvidenceServiceType;
import eu.de4a.conn.api.requestor.IssuingAuthorityType;
import eu.de4a.conn.api.requestor.ResponseTransferEvidence;
import eu.de4a.conn.api.rest.Ack;
import eu.de4a.conn.api.smp.NodeInfo;
import eu.toop.as4.client.ResponseWrapper;
 
 

@Component
public class Client { 
	@Autowired
	private RestTemplate restTemplate; 
	@Value("#{'${smp.endpoint.jvm:${smp.endpoint:}}'}")
	private String smpEndpoint;
	@Value("#{'${idk.endpoint.jvm:${idk.endpoint:}}'}")
	private String idkEndpoint;
	private static final Logger logger =  LoggerFactory.getLogger (Client.class);	
	private static final String DOUBLE_SEPARATOR = "::";
	private static final String SEPARATOR = ":";
	private static final String SERVICE = ":service::";
	private static final String SERVICES_PATH = "/services/";
	
	public String getSmpReturnUri(String senderId, String service) {
		final String docId = ResponseTransferEvidence.class.getSimpleName();
		final StringBuilder uri = new StringBuilder(smpEndpoint);
		
		List<String> serviceParams = Arrays.asList(service.split(SERVICE));
		String scheme = serviceParams.get(0);
		
		uri.append(scheme).append(DOUBLE_SEPARATOR).append(senderId).append(SERVICES_PATH).append(scheme)
				.append(DOUBLE_SEPARATOR).append(docId);
		return uri.toString();
	}
	
	public String getSmpUri(String service) {
		List<String> serviceParams = Arrays.asList(service.split(SERVICE));
		String scheme = serviceParams.get(0);
		List<String> docParams = Arrays.asList(serviceParams.get(1).split(SEPARATOR));
		String participantId = docParams.get(0) + SEPARATOR + docParams.get(1);
		String docId = docParams.get(2) + (docParams.size() > 3 ? SEPARATOR + docParams.get(3) : "");

		StringBuilder uri = new StringBuilder(smpEndpoint);
		uri.append(scheme).append(DOUBLE_SEPARATOR).append(participantId).append(SERVICES_PATH).append(scheme)
				.append(DOUBLE_SEPARATOR).append(docId);
		return uri.toString();
	}

	public String getSmpUri(String service, String requestor) {
		List<String> serviceParams = Arrays.asList(service.split(SERVICE));
		String scheme = serviceParams.get(0);
		List<String> docParams = Arrays.asList(serviceParams.get(1).split(SEPARATOR));
		String docId = "";
		for (int i = 2; i < docParams.size(); i++)
			docId += (i != 2 ? SEPARATOR : "") + docParams.get(i);
		
		StringBuilder uri = new StringBuilder(smpEndpoint);
		uri.append(scheme).append(DOUBLE_SEPARATOR).append(requestor).append(SERVICES_PATH).append(scheme)
				.append(DOUBLE_SEPARATOR).append(docId).append(":returnService");
		return uri.toString();
	}
	
	/**
	 * Obtain service metadata info from SMP by participantId and documentTypeId
	 * <p>scheme : service :: participantId : EvicendeTypeId : version</br>
	 * ej.: iso6523-actorid-upis:service::9921:ESS2833002E:BirthCertificate:1.0</p>
	 * 
	 * @param uri
	 * 		Url to retrieve service metadata from SMP
	 * @param isReturnService
	 * 		Determine if the process looked for it is a returnService or not
	 * @return NodeInfo
	 * 		Service metadata
	 */
	public NodeInfo getNodeInfo(String uri, boolean isReturnService) {
		logger.debug("Consulta SMP {}", uri);
		final String serviceProc = "service";
		final String returnServiceProc = "returnService";

		NodeInfo nodeInfo = new NodeInfo();
		try {
			SignedServiceMetadataType signedServiceMetadata = restTemplate.getForObject(uri,
					SignedServiceMetadataType.class);
			ServiceMetadataType serviceMetadata = signedServiceMetadata.getServiceMetadata();

			nodeInfo.setParticipantIdentifier(
					serviceMetadata.getServiceInformation().getParticipantIdentifier().getValue());
			nodeInfo.setDocumentIdentifier(serviceMetadata.getServiceInformation().getDocumentIdentifier().getValue());

			if (!CollectionUtils.isEmpty(serviceMetadata.getServiceInformation().getProcessList().getProcess())) {
				List<ProcessType> processes = serviceMetadata.getServiceInformation().getProcessList().getProcess();
				processes.stream().forEach(elem -> {
					String processId = elem.getProcessIdentifier().getValue();
					if (returnServiceProc.equals(processId) && isReturnService
							|| serviceProc.equals(processId) && !isReturnService) {
						// TODO en base a que se selecciona un endpoint u otro de la lista
						nodeInfo.setEndpointURI(elem.getServiceEndpointList().getEndpointAtIndex(0).getEndpointURI());
						nodeInfo.setCertificate(elem.getServiceEndpointList().getEndpointAtIndex(0).getCertificate());
					}
				});

			}

			// TODO control errores
		} catch (NullPointerException nPe) {
			logger.warn("Se ha producido un error en el parseo de la respuesta SMP", nPe);
			return new NodeInfo();
		} catch (HttpClientErrorException | HttpServerErrorException httpClientOrServerExc) {
			logger.error("No se ha encontrado informaci�n del servicio en el servidor SMP");
			return new NodeInfo();
		}

		return nodeInfo;
	}
	
	public IssuingAuthorityType getIssuingAuthority(String canonicalEvidenceType, String countryCode) {
		
		StringBuilder uri = new StringBuilder(idkEndpoint);
		uri.append(canonicalEvidenceType);
		uri.append("/").append(countryCode);
		
		return restTemplate.getForObject(uri.toString(), IssuingAuthorityType.class);
	}
	
	public EvidenceServiceType getEvidenceService(String canonicalEvidenceType, String countryCode, String ...args) {
		
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
		
		return restTemplate.getForObject(uri.toString(), EvidenceServiceType.class);
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
