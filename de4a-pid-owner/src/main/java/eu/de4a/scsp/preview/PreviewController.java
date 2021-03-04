package eu.de4a.scsp.preview;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.w3c.dom.Document;

import eu.de4a.conn.api.rest.Ack;
import eu.de4a.conn.owner.model.PreviewResponse;
import eu.de4a.exception.MessageException;
import eu.de4a.model.RequestorRequest;
import eu.de4a.model.RequestorRequestData;
import eu.de4a.scsp.owner.model.PreviewRequest;
import eu.de4a.scsp.preview.manager.PreviewManager;
import eu.de4a.util.DE4AConstants;
import eu.de4a.util.DOMUtils;

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
		Document response = previewManager.gimmePreview(previewRequest.getIdRequest());
		try {
			model.addAttribute("responseFormat", DOMUtils.loadString(response));
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
				
		RequestorRequestData reqData = previewManager.getPendingRequest(previewResponse.getRequestId(), 
				DE4AConstants.TAG_EVIDENCE_REQUEST);
		if(reqData != null) {
			try {
				Document docReq = DOMUtils.byteToDocument(reqData.getData());
				Ack ack = client.sendTransferEvidenceUsi(previewManager.getPIDResponse(docReq.getDocumentElement()));
				if(Ack.OK.equals(ack.getCode())) {
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
