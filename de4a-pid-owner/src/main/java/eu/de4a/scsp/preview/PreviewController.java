package eu.de4a.scsp.preview;


import java.time.LocalDateTime;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import eu.de4a.conn.owner.model.PreviewResponse;
import eu.de4a.exception.MessageException;
import eu.de4a.iem.jaxb.common.types.AckType;
import eu.de4a.iem.jaxb.common.types.RequestForwardEvidenceType;
import eu.de4a.iem.jaxb.common.types.ResponseErrorType;
import eu.de4a.scsp.owner.model.RequestorRequest;
import eu.de4a.scsp.owner.model.PreviewRequest;
import eu.de4a.scsp.preview.manager.PreviewManager;
import eu.de4a.util.DOMUtils;
import eu.de4a.util.XDE4ACanonicalEvidenceType;
import eu.de4a.util.XDE4AMarshaller;

@Controller
public class PreviewController {
	private static final Logger logger =  LoggerFactory.getLogger (PreviewController.class);
	
	@Autowired
	private PreviewManager previewManager;
	@Autowired
	private Client client;
	
	
	@PostMapping(value = "/preview") 
	public String preview(Model model, @ModelAttribute("previewRequest") PreviewRequest request,
			HttpServletRequest requesthttp,HttpServletResponse httpServletResponse) { 
		logger.debug("Solicitando previsualizacion de la evidencia " + request.getIdRequest());
		
		//TODO authenticate user against identities manager system
		//Ciudadano user = new Ciudadano();
		
		//Check if request id exists
		RequestorRequest pendingRequest = previewManager.getRequestorRequest(request.getIdRequest());
		
		if(pendingRequest != null) {
			model.addAttribute("isEvidenceReady", "true");
		} else {
			model.addAttribute("isEvidenceReady", "false");
		}
		
		model.addAttribute("previewRequest", request);
		return "preview";
	}
	
	@PostMapping(value ="/viewResponse")
	public String viewResponse(Model model, @ModelAttribute("previewRequest") PreviewRequest previewRequest,
			HttpServletRequest requesthttp,HttpServletResponse httpServletResponse) {
		
		//Retrieve response logic
		RequestForwardEvidenceType requestForward = previewManager.gimmePreview(previewRequest.getIdRequest());
		try {
			model.addAttribute("nationalEvidence", DOMUtils.loadString(DOMUtils.decodeCompressed(
					requestForward.getDomesticEvidenceList().getDomesticEvidenceAtIndex(0).getEvidenceData())));
			model.addAttribute("requestForwardEvidence", XDE4AMarshaller.deUsiRequestMarshaller(
					XDE4ACanonicalEvidenceType.BIRTH_CERTIFICATE).formatted().getAsString(requestForward));
			model.addAttribute("requestId", previewRequest.getIdRequest());
			model.addAttribute("returnUrl", previewRequest.getReturnUrl());
			model.addAttribute("previewResponse", new PreviewResponse());
			
			// Any possible interaction with preview screen, additional input parameters, evidence
			// selection, etc. will be registered on BD with the request to process response
			// according to that
			
		} catch (Exception e) {
			logger.error("There was a problem on processing Owner response data", e);
		}
		
		return "viewResponse";
	}
	
	@PostMapping(value = "/goResponse")
	public String goResponse(Model model, @ModelAttribute("previewResponse") PreviewResponse previewResponse,
			HttpServletRequest requesthttp,HttpServletResponse httpServletResponse) {
				
		RequestorRequest request = previewManager.getRequestorRequest(previewResponse.getRequestId());
		if(request != null) {
			try {
				RequestForwardEvidenceType  requestForward = previewManager.getRequestForwardEvidenceFromRequest(request);
				requestForward.setRequestId(previewResponse.getRequestId());
				requestForward.setTimeStamp(LocalDateTime.now());
				
				ResponseErrorType response = client.sendRequestForwardEvidence(requestForward);
				if(AckType.OK.equals(response.getAck())) {
					model.addAttribute("previewResponse", previewResponse);
					model.addAttribute("requestId", previewResponse.getRequestId());
					model.addAttribute("returnUrl", previewResponse.getReturnUrl());
					return "redirectEvaluator";
				}
			} catch (MessageException e) {
				logger.error("There was a problem processing USI data request saved", e);
			}
		}
		return "errorPage";
	}
}
