package eu.toop.controller;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.w3c.dom.Document;

import eu.de4a.conn.api.requestor.RequestTransferEvidence;
import eu.de4a.conn.api.rest.Ack;
import eu.de4a.exception.MessageException;
import eu.de4a.util.DE4AConstants;
import eu.toop.req.model.EvaluatorRequest;
import eu.toop.req.model.EvaluatorRequestData;
import eu.toop.req.repository.EvaluatorRequestDataRepository;
import eu.toop.req.repository.EvaluatorRequestRepository;
import eu.toop.rest.Client;

@Controller 
@Scope("session")
public class GreetingController {
	private static final Logger logger =  LoggerFactory.getLogger (GreetingController.class);
	@Autowired
	private Client client;
	@Autowired 
	private EvaluatorRequestRepository evaluatorRequestRepository; 
	@Autowired
	private EvaluatorRequestDataRepository evaluatorRequestDataRepository; 
	@Autowired
	private ResponseManager responseManager; 
	@Value("${de4a.connector.url.requestor.redirect}")
	private String urlRequestorRedirect;
	private String id;
	private RequestTransferEvidence requestEvidencia;
	//private String response;

	@RequestMapping(value = "/welcome.html")
	public String welcome(Model model) { 
		model.addAttribute("evidenceForm", new Evidencia());
		return "welcome";
	}
	@GetMapping("/greeting")
	public String greetingForm(Model model) { 
		model.addAttribute("userForm", new User());
		return "greeting";
	}
	@RequestMapping(value ="/goEvidenceForm", method = RequestMethod.POST)
	public String goEvidenceForm(Model model,@ModelAttribute("evidenceForm") Evidencia evidencia,HttpServletRequest requesthttp,HttpServletResponse httpServletResponse) { 
		User u=new User();
		model.addAttribute("userForm",u );
		if(evidencia.getTipo().equals("DBA")) {  
			return "dba"; 
		} 
		return "nacimiento";
	}
	
	@RequestMapping(value = "/greetinggo", method = RequestMethod.POST) 
	public String greetingSubmit(@ModelAttribute("userForm") User user,HttpServletRequest requesthttp,HttpServletResponse httpServletResponse) {   
			requestEvidencia=client.buildRequest(user);
			user.setRequest(jaxbObjectToXML(requestEvidencia));
			id=requestEvidencia.getRequestId();  
			return "showRequest";
	} 
	@RequestMapping(value = "/requestEvidence", method = RequestMethod.POST) 
	public void sendRequest(@ModelAttribute("userForm") User user,HttpServletRequest requesthttp,HttpServletResponse httpServletResponse) {  
		try {
			EvaluatorRequest request=new EvaluatorRequest();
			request.setIdrequest(id);
			evaluatorRequestRepository.save(request);
			boolean ok = client.getEvidenceRequestUSI(requestEvidencia);
			httpServletResponse.setHeader("Location", String.format(urlRequestorRedirect, id));//"http://localhost:8083/de4a-connector/getreponse?id="+id);
			//httpServletResponse.setHeader("Location", "https://des-de4a.redsara.es/de4a-tc-requestor/getreponse?id="+id);
			httpServletResponse.setStatus(302);  
		} catch (MessageException e) {
			logger.error("Error getting evidence request",e);
		}
		
	}
	@RequestMapping(value = "/requestEvidenceUSI", method = RequestMethod.POST) 
	public void sendRequestUSI(@ModelAttribute("userForm") User user,HttpServletRequest requesthttp,HttpServletResponse httpServletResponse) {  
		try {
			EvaluatorRequest request=new EvaluatorRequest();
			request.setIdrequest(id);
			evaluatorRequestRepository.save(request);
			boolean ok = client.getEvidenceRequestUSI(requestEvidencia);
			httpServletResponse.setHeader("Location", String.format(urlRequestorRedirect, id));//"http://localhost:8083/de4a-connector/getreponse?id="+id);
			//httpServletResponse.setHeader("Location", "https://des-de4a.redsara.es/de4a-tc-requestor/getreponse?id="+id);
			httpServletResponse.setStatus(302);  
		} catch (MessageException e) {
			logger.error("Error getting evidence request",e);
		}
		
	}
	@GetMapping(value = "/returnEvidence"  )
	public String receiveEvidence(@RequestParam String id,RedirectAttributes redirectAttributes) 
	{   

		   redirectAttributes.addAttribute("id", id);
		return "redirect:/returnPage.jsp";
	} 
	@PostMapping(value = "/returnEvidence", produces = {  "application/xml" }  )
	public @ResponseBody Ack receiveEvidence(@RequestParam("file") MultipartFile[] files ) 
	{ 
		Ack ack=new Ack();
		try {
			responseManager.manageResponse( files);
			ack.setCode( Ack.OK );
			ack.setMessage("Done"); 
		} catch (IOException | MessageException e) {
			logger.error("Error processing response files ",e);
			ack.setCode( Ack.FAIL );
			ack.setMessage(e.getMessage()); 
		}  
		return ack;
	} 
	@RequestMapping(value = "/viewresponse", method = RequestMethod.POST) 
	public String viewresponse(@RequestParam String id,HttpServletRequest requesthttp,HttpServletResponse httpServletResponse,Model model) { 
		User user=new User();
		EvaluatorRequest request=evaluatorRequestRepository.findById(id).orElse(null);
		EvaluatorRequestData data=new EvaluatorRequestData();
		data.setRequest(request);
		Example<EvaluatorRequestData> example = Example.of(data);
		List<EvaluatorRequestData>registros=evaluatorRequestDataRepository.findAll(example); 
		EvaluatorRequestData dataresponse=registros.stream().filter(d->d.getIddata().equals(DE4AConstants.TAG_EVIDENCE_RESPONSE)).findFirst().orElse(null);
		EvaluatorRequestData dataNationalresponse=registros.stream().filter(d->d.getIddata().equals(DE4AConstants.TAG_NATIONAL_EVIDENCE_RESPONSE)).findFirst().orElse(null);
		try {
			user.setResponse(loadString(dataresponse.getData()));
			user.setNationalResponse(loadString(dataNationalresponse.getData()));
			logger.debug("!------------------------------------------------------");
			logger.debug("Peique molon "+loadString(dataresponse.getData()));
			logger.debug("!------------------------------------------------------");
		} catch (Exception e) {
			logger.error("Fatality!",e); 
		}
		model.addAttribute("userForm", user);
		return "viewresponse";
	}
	private String jaxbObjectToXML(RequestTransferEvidence request) 
    {
        try
        { 
            JAXBContext jaxbContext = JAXBContext.newInstance(RequestTransferEvidence.class); 
            Marshaller jaxbMarshaller = jaxbContext.createMarshaller(); 
            jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE); 
            StringWriter sw = new StringWriter(); 
            jaxbMarshaller.marshal(request, sw); 
            return sw.toString(); 
 
        } catch (JAXBException e) {
            logger.error("Error marshalling object",e);
            return "";
        }
    }
	private Document obtenerDocumentDeByte(byte[] documentoXml) throws Exception {
	    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	    factory.setNamespaceAware(true);
	    DocumentBuilder builder = factory.newDocumentBuilder();
	    return builder.parse(new ByteArrayInputStream(documentoXml));
	}
	private String doctoString(Document xmlDocument)
	{
	    TransformerFactory tf = TransformerFactory.newInstance();
	    Transformer transformer;
	    try {
	        transformer = tf.newTransformer();
	         
	        // Uncomment if you do not require XML declaration
	        // transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
	         
	        //A character stream that collects its output in a string buffer, 
	        //which can then be used to construct a string.
	        StringWriter writer = new StringWriter();
	 
	        //transform document to string 
	        transformer.transform(new DOMSource(xmlDocument), new StreamResult(writer));
	        return  writer.getBuffer().toString();                 //Print to console or logs
	    } 
	    catch (TransformerException e) 
	    {
	        e.printStackTrace();
	    }
	    catch (Exception e) 
	    {
	        e.printStackTrace();
	    }
	    return null;
	}
	private String loadString(byte[] msg) throws Exception {
		Document doc = obtenerDocumentDeByte(msg);
		Source xmlInput = new StreamSource(new StringReader(doctoString(doc)));
        StringWriter stringWriter = new StringWriter();
        StreamResult xmlOutput = new StreamResult(stringWriter);
        TransformerFactory transformerFactory  = TransformerFactory.newInstance();
     
         
        Transformer transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.transform(xmlInput, xmlOutput);
        return xmlOutput.getWriter().toString(); 
	}
}
