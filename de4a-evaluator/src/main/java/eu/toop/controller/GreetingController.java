package eu.toop.controller;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.data.domain.Example;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.w3c.dom.Document;

import eu.de4a.conn.api.requestor.AtuItem;
import eu.de4a.conn.api.requestor.RequestLookupEvidenceServiceData;
import eu.de4a.conn.api.requestor.RequestLookupRoutingInformation;
import eu.de4a.conn.api.requestor.RequestTransferEvidence;
import eu.de4a.conn.api.requestor.ResponseLookupEvidenceServiceData;
import eu.de4a.conn.api.requestor.ResponseLookupRoutingInformation;
import eu.de4a.conn.api.requestor.ResponseTransferEvidence;
import eu.de4a.conn.api.rest.Ack;
import eu.de4a.conn.owner.model.PreviewRequest;
import eu.de4a.exception.MessageException;
import eu.de4a.model.EvaluatorRequest;
import eu.de4a.model.EvaluatorRequestData;
import eu.de4a.repository.EvaluatorRequestDataRepository;
import eu.de4a.repository.EvaluatorRequestRepository;
import eu.de4a.util.DE4AConstants;
import eu.de4a.util.DOMUtils;
import eu.de4a.util.EvidenceTypeIds;
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
	
	private final static String REDIRECT_ADDR = "redirect:/%s";

	@GetMapping(value = "/welcome.html")
	public String welcome(Model model) { 
		model.addAttribute("evidenceForm", new RequestLookupRoutingInformation());
		return "welcome";
	}
	@GetMapping("/greeting")
	public String greetingForm(Model model) { 
		model.addAttribute("userForm", new User());
		return "greeting";
	}
	
	@GetMapping(value = "/redirectOwner")
	public String redirectOwner(Model model) { 
		model.addAttribute("userForm", new User());
		return "redirectOwner";
	}
	
	@PostMapping(value ="/goEvidenceForm")
	public String goEvidenceForm(Model model,@ModelAttribute("evidenceForm") RequestLookupRoutingInformation lookupRouting,HttpServletRequest requesthttp,HttpServletResponse httpServletResponse) { 
		User u = new User();
		u.setEvidenceTypeId(lookupRouting.getCanonicalEvidenceId());
		u.setCountry(lookupRouting.getCountry());
		model.addAttribute("userForm",u );
		
		try {
			ResponseLookupRoutingInformation responseRouting = client.getRoutingInfo(lookupRouting);
			Map<String, String> iaOrganisationalStructure = responseRouting.getIssuingAuthority().getIaOrganisationalStructure().stream().collect(
	                Collectors.toMap(AtuItem::getAtuCode, AtuItem::getAtuName));
			model.addAttribute("iaOrganisationalStructure", iaOrganisationalStructure);
		} catch (MessageException e) {
			logger.error("Ha ocurrido un error en la consulta de rounting info", e);
		}
		
		if(EvidenceTypeIds.BIRTHCERTIFICATE.toString().equals(lookupRouting.getCanonicalEvidenceId())) {
			return "nacimiento";
		}
		return "dba";
	}
	
	@PostMapping(value = "/greetinggo") 
	public String greetingSubmit(@ModelAttribute("userForm") User user,HttpServletRequest requesthttp,HttpServletResponse httpServletResponse) {
			RequestLookupEvidenceServiceData request = new RequestLookupEvidenceServiceData();
			request.setAdminTerritorialUnit(user.getAtuCode());
			request.setCanonicalEvidenceId(user.getEvidenceTypeId());
			request.setCountry(user.getCountry());
			
			//Obtenemos el evidenceSeriviceUri del DR-IDK para incluirlo en la RequestTransferEvidence
			ResponseLookupEvidenceServiceData responseServiceData = client.getLookupServiceData(request);			
		
			requestEvidencia = client.buildRequest(user, responseServiceData.getEvidenceService());
			user.setRequest(DOMUtils.jaxbObjectToXML(requestEvidencia, RequestTransferEvidence.class));
			id=requestEvidencia.getRequestId();  
			return "showRequest";
	}
	
	@PostMapping(value = "/requestEvidence") 
	public String sendRequest(Model model, HttpServletRequest requesthttp,HttpServletResponse httpServletResponse,RedirectAttributes redirectAttributes) {  
		try {
			EvaluatorRequest request = new EvaluatorRequest();
			request.setIdrequest(id);
			
			String redirectAddr = String.format(REDIRECT_ADDR, "errorPage.jsp");
			if(!StringUtils.isEmpty(requestEvidencia.getDataOwner().getUrlRedirect())) {
				//USI pattern
				request.setUsi(true);
				evaluatorRequestRepository.save(request);
				client.getEvidenceRequestUSI(requestEvidencia);
				PreviewRequest previewRequest = new PreviewRequest();
				previewRequest.setIdRequest(requestEvidencia.getRequestId());
				previewRequest.setReturnUrl(requestEvidencia.getDataEvaluator().getUrlRedirect());
				model.addAttribute("previewRequest", previewRequest);
				model.addAttribute("idRequest", requestEvidencia.getRequestId());
				model.addAttribute("redirectUrl", requestEvidencia.getDataOwner().getUrlRedirect());
				redirectAddr = "redirectOwner";
			} else {
				//IM pattern
				request.setUsi(false);
				evaluatorRequestRepository.save(request);
				client.getEvidenceRequestIM(requestEvidencia);
				redirectAttributes.addAttribute("id", id);
				redirectAddr = String.format(REDIRECT_ADDR, "returnPage.jsp");			
			}									
			return redirectAddr;
		} catch (MessageException e) {
			logger.error("Error getting evidence request",e);
			return String.format(REDIRECT_ADDR, "errorPage.jsp");
		}
		
	}
	@PostMapping(value = "/requestEvidenceUSI") 
	public void sendRequestUSI(@ModelAttribute("userForm") User user,HttpServletRequest requesthttp,HttpServletResponse httpServletResponse) {  
		try {
			EvaluatorRequest request=new EvaluatorRequest();
			request.setIdrequest(id);
			evaluatorRequestRepository.save(request);
			client.getEvidenceRequestUSI(requestEvidencia);
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
	
	@PostMapping(value = "/viewresponse", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
			consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE}) 
	public @ResponseBody Ack viewresponse(@RequestBody ResponseTransferEvidence responseTransferEvidence, HttpServletRequest requesthttp, 
			HttpServletResponse httpServletResponse,RedirectAttributes redirectAttributes) { 
		Ack ack = new Ack();		
		try {
			responseManager.manageResponse(responseTransferEvidence);
			ack.setCode(Ack.OK);
		} catch (MessageException e) {
			logger.error("There was a problem processing owner USI response");
			ack.setCode(Ack.FAIL); 
		}
		return ack;
	}
	
	@PostMapping(value = "/viewresponse")
	public String viewresponse(@RequestParam String requestId, HttpServletRequest requesthttp,
			HttpServletResponse httpServletResponse, Model model) {
		User user = new User();
		EvaluatorRequest request = evaluatorRequestRepository.findById(requestId).orElse(null);
		EvaluatorRequestData data = new EvaluatorRequestData();
		data.setRequest(request);
		Example<EvaluatorRequestData> example = Example.of(data);
		List<EvaluatorRequestData> registros = evaluatorRequestDataRepository.findAll(example);
		EvaluatorRequestData dataresponse = registros.stream()
				.filter(d -> d.getIddata().equals(DE4AConstants.TAG_EVIDENCE_RESPONSE)).findFirst().orElse(null);
		if(dataresponse != null) {
			try {
				Document response = DOMUtils.byteToDocument(dataresponse.getData());
				user.setResponse(DOMUtils.loadString(response));
				String national = DOMUtils
						.getNodeFromXpath(DE4AConstants.XPATH_EVIDENCE_DATA, response.getDocumentElement())
						.getTextContent();
				user.setNationalResponse(DOMUtils.loadString(DOMUtils.decodeCompressed(national.getBytes())));
				model.addAttribute("isResponseReady", "true");
			} catch (Exception | MessageException e) {
				logger.error("Fatality!", e);
			}
		} else {
			model.addAttribute("isResponseReady", "false");
		}
		model.addAttribute("userForm", user);
		model.addAttribute("requestId", requestId);
		return "viewresponse";
	}
}
