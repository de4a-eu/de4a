package eu.de4a.scsp.ws.server;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
 
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.datatype.DatatypeConstants;

import org.apache.commons.fileupload.disk.DiskFileItem;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import eu.de4a.conn.api.requestor.RequestTransferEvidence;
import eu.de4a.conn.xml.DOMUtils;
import eu.de4a.exception.MessageException;
import eu.de4a.scsp.translate.EvidenceMapper;
import eu.de4a.scsp.translate.EvidenceTranslator;
import eu.de4a.scsp.ws.client.ClientePidWS;
import eu.de4a.util.DE4AConstants;

@Controller  
public class RequestPidController {
	private static final Logger logger =  LoggerFactory.getLogger (RequestPidController.class); 
	private static final String XPATH_SERVICE_URI="//*[local-name()='EvidenceServiceURI']";
	@Autowired
	private ClientePidWS clientePidWS;  
	@Autowired
	private EvidenceMapper evidenceMapper; 
	@PostMapping(value = "/request", consumes = {"application/xml","application/json"}, produces = { MediaType.APPLICATION_OCTET_STREAM_VALUE } )
	public   ResponseEntity<Resource>  sendRequest(@RequestBody RequestTransferEvidence request) throws MessageException {   
 
			logger.debug("Received Canonical Request for being  sent to PID ");
			Element evidenceRequest= marshall(request).getDocumentElement();
			String evidenceServiceUri= DOMUtils.getValueFromXpath(XPATH_SERVICE_URI, evidenceRequest);    
			EvidenceTranslator translator=evidenceMapper.getTranslator(evidenceServiceUri);
			Element scspRequest=translator.translateEvidenceRequest(evidenceRequest);
			Element scspResponse=(Element) clientePidWS.sendRequest(scspRequest);
			String id=DOMUtils.getNodeFromXpath(String.format(DE4AConstants.XPATH_REQUEST_ID,DE4AConstants.TAG_EVIDENCE_REQUEST), evidenceRequest).getNodeValue();
			String idevaluator=DOMUtils.getValueFromXpath( DE4AConstants.XPATH_EVALUATOR_ID, evidenceRequest);
			String nameevaluator=DOMUtils.getValueFromXpath( DE4AConstants.XPATH_EVALUATOR_NAME, evidenceRequest);
			String idowner=DOMUtils.getValueFromXpath( DE4AConstants.XPATH_OWNER_ID, evidenceRequest);
			String nameownerr=DOMUtils.getValueFromXpath( DE4AConstants.XPATH_OWNER_NAME, evidenceRequest);
			
			Element evidenceResponse= translator.translateEvidenceResponse(scspResponse) ;
			DOMUtils.changeNodo(evidenceResponse.getOwnerDocument(), String.format(DE4AConstants.XPATH_REQUEST_ID,DE4AConstants.TAG_EVIDENCE_RESPONSE), id);
			DOMUtils.changeNodo(evidenceResponse.getOwnerDocument(),  DE4AConstants.XPATH_EVALUATOR_ID_NODE   , idevaluator);
			DOMUtils.changeNodo(evidenceResponse.getOwnerDocument(),  DE4AConstants.XPATH_EVALUATOR_NAME_NODE   , nameevaluator);
			DOMUtils.changeNodo(evidenceResponse.getOwnerDocument(),  DE4AConstants.XPATH_OWNER_NAME_NODE   , nameownerr );
			DOMUtils.changeNodo(evidenceResponse.getOwnerDocument(),  DE4AConstants.XPATH_OWNER_ID_NODE   , idowner );
			
			MultipartFile[] payloads=new MultipartFile[2];
			MultipartFile multipartCanonical=
					getMultipart(DE4AConstants.TAG_EVIDENCE_RESPONSE, MediaType.APPLICATION_XML.toString(), DOMUtils.documentToByte(evidenceResponse.getOwnerDocument()));
			 
			MultipartFile multipartScsp=
					getMultipart(DE4AConstants.TAG_NATIONAL_EVIDENCE_RESPONSE, MediaType.APPLICATION_XML.toString(), DOMUtils.documentToByte(scspResponse.getOwnerDocument()));
			 
			 
//			payloads[0]=multipartCanonical;
//			payloads[1]=multipartScsp;
//			HttpHeaders responseHeaders = new HttpHeaders();
//			responseHeaders.add("Content-Type", MediaType.MULTIPART_FORM_DATA_VALUE); 
//			 
//			MultiValueMap<String, Object> parts = new LinkedMultiValueMap<>();
//			parts.add(DE4AConstants.TAG_EVIDENCE_RESPONSE,    new FileSystemResource(convert(multipartCanonical)));
//			parts.add(DE4AConstants.TAG_NATIONAL_EVIDENCE_RESPONSE,    new FileSystemResource(convert(multipartScsp)));
//			  
//			HttpHeaders headers = new HttpHeaders();
//			headers.setContentType(MediaType.MULTIPART_FORM_DATA);
//			ResponseEntity<MultiValueMap<String, Object>> responseHttp = new ResponseEntity<MultiValueMap<String, Object>>(parts,  HttpStatus.OK) ;
// 
			boolean ok=true;
			List<File> files = new ArrayList<>(2); 
            File tempdir=null;
			try {
				tempdir = Files.createTempDirectory("de4a-temp").toFile();
			} catch (IOException e1) {
				ok=false;
				logger.error("Error",e1);
				return returnErrorResource();
			}
			files.add(convert(multipartCanonical,tempdir))  ;
			files.add(convert(multipartScsp,tempdir))  ; 
			byte[]data=null;
			try {
				data=empaquetarZip(tempdir);
			} catch (IOException e) {
				ok=false;
				logger.error("Error",e);
				return returnErrorResource();
			} 
	        ByteArrayResource resource=  new ByteArrayResource(data); 
			if(ok)
	        return ResponseEntity.ok() 
	                .contentLength(data.length)
	                .contentType(MediaType.parseMediaType( MediaType.APPLICATION_OCTET_STREAM_VALUE))
	                .body(resource);
			else return returnErrorResource();
			  
					
	} 

	private String FILE_SEPARATOR = System.getProperty("file.separator");
    public byte[] empaquetarZip(  File tempDir) throws IOException{
    	ByteArrayOutputStream baos = new ByteArrayOutputStream();
    	try {
    		ZipOutputStream zos = new ZipOutputStream(baos);
    		byte bytes[] = new byte[2048];
    	    for (String fileName : tempDir.list()) {
	    	    FileInputStream fis = new FileInputStream(tempDir.getPath() + FILE_SEPARATOR + fileName);
	    	    BufferedInputStream bis = new BufferedInputStream(fis);
	    	    zos.putNextEntry(new ZipEntry(fileName));
	    	    int bytesRead;
	    	    while ((bytesRead = bis.read(bytes)) != -1) {
	    	    	zos.write(bytes, 0, bytesRead);
	    	    }
	    	    zos.closeEntry();
	    	    bis.close();
	    	    fis.close();
    	    }
    	    zos.flush();
	   		baos.flush();
	   		zos.close();
	   		baos.close();
	   	} catch ( Exception e) {
    		logger.error("Error zipping",e);
    		return null;
    	}
    	return baos.toByteArray();
    }
	private  ResponseEntity<Resource> returnErrorResource(){
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ByteArrayResource(null));
	}
 
	/* 
	@PostMapping(value = "/request", consumes = {"application/xml","application/json"}, produces = { MediaType.MULTIPART_FORM_DATA_VALUE }  )
	public     ResponseEntity<MultiValueMap<String, Object>>    sendRequest(@RequestBody RequestTransferEvidence request, HttpServletResponse response) throws MessageException {   
 
			logger.debug("Received Canonical Request for being  sent to PID ");
			Element evidenceRequest= marshall(request).getDocumentElement();
			String evidenceServiceUri= DOMUtils.getValueFromXpath(XPATH_SERVICE_URI, evidenceRequest);    
			EvidenceTranslator translator=evidenceMapper.getTranslator(evidenceServiceUri);
			Element scspRequest=translator.translateEvidenceRequest(evidenceRequest);
			Element scspResponse=(Element) clientePidWS.sendRequest(scspRequest);
			String id=DOMUtils.getNodeFromXpath(String.format(DE4AConstants.XPATH_REQUEST_ID,DE4AConstants.TAG_EVIDENCE_REQUEST), evidenceRequest).getNodeValue();
			String idevaluator=DOMUtils.getValueFromXpath( DE4AConstants.XPATH_EVALUATOR_ID, evidenceRequest);
			String nameevaluator=DOMUtils.getValueFromXpath( DE4AConstants.XPATH_EVALUATOR_NAME, evidenceRequest);
			String idowner=DOMUtils.getValueFromXpath( DE4AConstants.XPATH_OWNER_ID, evidenceRequest);
			String nameownerr=DOMUtils.getValueFromXpath( DE4AConstants.XPATH_OWNER_NAME, evidenceRequest);
			
			Element evidenceResponse= translator.translateEvidenceResponse(scspResponse) ;
			DOMUtils.changeNodo(evidenceResponse.getOwnerDocument(), String.format(DE4AConstants.XPATH_REQUEST_ID,DE4AConstants.TAG_EVIDENCE_RESPONSE), id);
			DOMUtils.changeNodo(evidenceResponse.getOwnerDocument(),  DE4AConstants.XPATH_EVALUATOR_ID_NODE   , idevaluator);
			DOMUtils.changeNodo(evidenceResponse.getOwnerDocument(),  DE4AConstants.XPATH_EVALUATOR_NAME_NODE   , nameevaluator);
			DOMUtils.changeNodo(evidenceResponse.getOwnerDocument(),  DE4AConstants.XPATH_OWNER_NAME_NODE   , nameownerr );
			DOMUtils.changeNodo(evidenceResponse.getOwnerDocument(),  DE4AConstants.XPATH_OWNER_ID_NODE   , idowner );
			
			MultipartFile[] payloads=new MultipartFile[2];
			MultipartFile multipartCanonical=
					getMultipart(DE4AConstants.TAG_EVIDENCE_RESPONSE, MediaType.APPLICATION_XML.toString(), DOMUtils.documentToByte(evidenceResponse.getOwnerDocument()));
			 
			MultipartFile multipartScsp=
					getMultipart(DE4AConstants.TAG_NATIONAL_EVIDENCE_RESPONSE, MediaType.APPLICATION_XML.toString(), DOMUtils.documentToByte(scspResponse.getOwnerDocument()));
			 
			 
			payloads[0]=multipartCanonical;
			payloads[1]=multipartScsp;
			HttpHeaders responseHeaders = new HttpHeaders();
			responseHeaders.add("Content-Type", MediaType.MULTIPART_FORM_DATA_VALUE); 
			 
			MultiValueMap<String, Object> parts = new LinkedMultiValueMap<>();
			parts.add(DE4AConstants.TAG_EVIDENCE_RESPONSE,    new FileSystemResource(convert(multipartCanonical)));
			parts.add(DE4AConstants.TAG_NATIONAL_EVIDENCE_RESPONSE,    new FileSystemResource(convert(multipartScsp)));
			  
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.MULTIPART_FORM_DATA);
			ResponseEntity<MultiValueMap<String, Object>> responseHttp = new ResponseEntity<MultiValueMap<String, Object>>(parts,  HttpStatus.OK) ;
			  
			  
			return responseHttp; 
					
	} */
	  public static File convert(MultipartFile file,File tempdir)
	  {    
	    File convFile  =null;
	    try {
	    	convFile = File.createTempFile("de4a-",".xml",tempdir) ;
	          FileOutputStream fos = new FileOutputStream(convFile); 
	            fos.write(file.getBytes());
	            fos.close(); 
	    } catch (IOException e) {
	        // TODO Auto-generated catch block
	        e.printStackTrace();
	    } 

	    return convFile;
	 }
	private MultipartFile getMultipart(String label,String mimetype,byte[] data)   { 
		File tempFile = null; 
		try {
			tempFile = File.createTempFile("de4a-", null);
			 
			DiskFileItem item= new  org.apache.commons.fileupload.disk.DiskFileItem
					(  label, mimetype, false ,label,   1, tempFile.getParentFile());
			ByteArrayInputStream in=new ByteArrayInputStream(data);
			OutputStream out=item.getOutputStream();
			org.apache.commons.io.IOUtils.copy(in,  out);
			in.close();
			out.close();
			return new  CommonsMultipartFile(item);
		} catch (IOException e1) {
			logger.error("Error attaching files",e1);
		} 
		return null ;
	
		
	}
	private Document marshall(RequestTransferEvidence request ) {   
        try
        {
        	Date date=new Date(request.getDataRequestSubject().getDataSubjectPerson().getDateOfBirth().toGregorianCalendar().getTimeInMillis(  ));
        	Calendar time=Calendar.getInstance( );  
        	time.setTime(date);
        	request.getDataRequestSubject().getDataSubjectPerson().getDateOfBirth() .setDay(time.get(Calendar.DAY_OF_MONTH));
        	request.getDataRequestSubject().getDataSubjectPerson().getDateOfBirth() .setMonth(time.get(Calendar.MONTH)+1);
        	request.getDataRequestSubject().getDataSubjectPerson().getDateOfBirth() .setYear(time.get(Calendar.YEAR));  
        	request.getDataRequestSubject().getDataSubjectPerson().getDateOfBirth() .setTimezone(DatatypeConstants.FIELD_UNDEFINED);
            JAXBContext jaxbContext = JAXBContext.newInstance(RequestTransferEvidence.class);
            javax.xml.bind.Marshaller jaxbMarshaller = (Marshaller) jaxbContext.createMarshaller(); 
            jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            StringWriter sw = new StringWriter(); 
            jaxbMarshaller.marshal(request, sw); 
            return DOMUtils.stringToDocument(sw.toString()); 
 
        } catch ( Exception e) {
           logger.error("Error building request DOM",e);
           return null;
        } 
}
}
