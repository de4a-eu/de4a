package eu.de4a.pid;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.impl.client.HttpClients;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import eu.de4a.scsp.spring.ConfPid;

@SpringBootTest(classes={ConfPid.class}) 
@RunWith(SpringRunner.class) 
public class TestClientPid {
	
	@Test
	public void testClient() { 
		// String uri = "http://localhost:8083/de4a-connector/request?urlReturn=http://localhost:8682/de4a-evaluator/ReturnPage&evaluatorId="+dataOwnerdI+"&@evaluatorId="+evidenceServiceUri+"&requestId=777"; 
		 RestTemplate plantilla = new RestTemplate();
		HttpComponentsClientHttpRequestFactory requestFactory =
		                new HttpComponentsClientHttpRequestFactory();
		requestFactory.setHttpClient(HttpClients.custom().setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE).build());
		plantilla.setRequestFactory(requestFactory);
		 List<HttpMessageConverter<?>> messageConverters = new ArrayList<>();
  	 MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
 	 converter.setSupportedMediaTypes(Collections.singletonList(MediaType.APPLICATION_XML  ));
 	 messageConverters.add(converter);
 	plantilla.setMessageConverters(messageConverters);
		MultiValueMap<String, Object> body  = new LinkedMultiValueMap<>(); 
		body.add("ids","adsasd") ; 
 		HttpHeaders headers = new HttpHeaders();
 		headers.setContentType(MediaType.APPLICATION_XML); 
 		HttpEntity<MultiValueMap<String, Object>> requestEntity = new    HttpEntity<MultiValueMap<String, Object>>( 	body, headers); 
 		try {
 			plantilla.postForEntity("http://localhost:8682/de4a-pid-owner/request",unmarshallMe() , List .class);  
 		}catch(Exception e) {
 			e.printStackTrace();
 		}
		
		
	}
	private  eu.de4a.conn.api.requestor.RequestTransferEvidence  unmarshallMe()  {  
        try
        {
            JAXBContext jaxbContext = JAXBContext.newInstance(eu.de4a.conn.api.requestor.ResponseTransferEvidence.class);
            javax.xml.bind.Unmarshaller jaxbMarshaller = (Unmarshaller) jaxbContext.createUnmarshaller() ;  
            return (eu.de4a.conn.api.requestor.RequestTransferEvidence) jaxbMarshaller.unmarshal(getXML()); 
 
        } catch (Exception e) {
        	e.printStackTrace();
           return null;
        }  
}
	private Document getXML() {
		try {
			DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			InputSource is = new InputSource();
			String ex="<RequestTransferEvidence xmlns=\"http://www.de4a.eu/2020/data/requestor/pattern/intermediate\" xmlns:ns2=\"http://eidas.europa.eu/attributes/naturalperson\" xmlns:ns3=\"http://eidas.europa.eu/attributes/legalperson\">\r\n"
					+ "    <RequestId>DE4A_EV-1612002963419</RequestId>\r\n"
					+ "    <SpecificationId>de4a.1.0.0</SpecificationId>\r\n"
					+ "    <TimeStamp>2021-01-30T11:36:03.421</TimeStamp>\r\n"
					+ "    <ProcedureId>procedure</ProcedureId>\r\n"
					+ "    <DataEvaluator>\r\n"
					+ "        <id>spanish.evaluator.id</id>\r\n"
					+ "        <name>spanish.evaluator.name</name>\r\n"
					+ "    </DataEvaluator>\r\n"
					+ "    <DataOwner>\r\n"
					+ "        <id>fakeId</id>\r\n"
					+ "        <name>fakeName</name>\r\n"
					+ "    </DataOwner>\r\n"
					+ "    <DataRequestSubject>\r\n"
					+ "        <DataSubjectPerson>\r\n"
					+ "            <Identifier>SI/ES/10000949C</Identifier>\r\n"
					+ "            <GivenName>OLGA</GivenName>\r\n"
					+ "            <FamilyName>SAN MIGUEL</FamilyName>\r\n"
					+ "            <DateOfBirth>1940-06-03</DateOfBirth>\r\n"
					+ "            <BirthName>OLGA SAN MIGUEL CHAO</BirthName>\r\n"
					+ "        </DataSubjectPerson>\r\n"
					+ "    </DataRequestSubject>\r\n"
					+ "    <RequestGrounds>\r\n"
					+ "        <LawELIPermanentLink>http://www.google.es</LawELIPermanentLink>\r\n"
					+ "        <ExplicitRequest>SDGR14</ExplicitRequest>\r\n"
					+ "    </RequestGrounds>\r\n"
					+ "    <EvidenceServiceData>\r\n"
					+ "        <EvidenceServiceURI>service.uri</EvidenceServiceURI>\r\n"
					+ "    </EvidenceServiceData>\r\n"
					+ "    <ReturnServiceId>https://des-de4a.redsara.es/de4a-evaluator//returnEvidence</ReturnServiceId>\r\n"
					+ "</RequestTransferEvidence>\r\n"
					+ "";
			is.setCharacterStream(new StringReader(ex));

			Document doc = db.parse(is);
			return doc;
		}catch(Exception e) {
			e.printStackTrace();return null;
		}
	}
}
