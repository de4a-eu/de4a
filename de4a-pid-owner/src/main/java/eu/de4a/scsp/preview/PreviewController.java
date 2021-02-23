package eu.de4a.scsp.preview.controller;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import eu.de4a.conn.api.requestor.ResponseTransferEvidence;
import eu.de4a.scsp.preview.manager.PreviewManager; 
@Controller
public class PreviewController {
	private static final Logger logger =  LoggerFactory.getLogger (PreviewController.class);
	
	@Autowired
	private PreviewManager previewManager;
	@RequestMapping(value = "/preview", method = RequestMethod.POST) 
	public String preview(@RequestParam String id,@RequestParam String urlReturn, Model model,HttpServletResponse httpServletResponse) { 
		logger.debug("Solicitando previsualizacion de eviden cia {} ",id);
//		EvidencerRequest user=new User();
//		EvaluatorRequest request=evaluatorRequestRepository.findById(id).orElse(null);
//		EvaluatorRequestData data=new EvaluatorRequestData();
//		data.setRequest(request);
//		Example<EvaluatorRequestData> example = Example.of(data);
//		List<EvaluatorRequestData>registros=evaluatorRequestDataRepository.findAll(example); 
//		EvaluatorRequestData dataresponse=registros.stream().filter(d->d.getIddata().equals(DE4AConstants.TAG_EVIDENCE_RESPONSE)).findFirst().orElse(null); 
//		try {
//			Document response=DOMUtils.byteToDocument(dataresponse.getData());
//			user.setResponse(loadString(response)); 
//			String national=DOMUtils.getNodeFromXpath(DE4AConstants.XPATH_EVIDENCE_DATA,response.getDocumentElement()).getTextContent(); 
//			user.setNationalResponse(loadString(DOMUtils.decodeCompressed(national.getBytes() ))); 
//		} catch (Exception | MessageException e) {
//			logger.error("Fatality!",e); 
//		}
		ResponseTransferEvidence response=previewManager.gimmePreview(id);
		Ciudadano user=new Ciudadano();
		user.setResponse(puesto que es el preview del owner, aqui metería la respuesta domestica. la maquetacion de la preview de datos es especifica en funcion de servicio.Probablemente
				quieren que sea similar a ClienteLigero donde tendriamos un xhtml,o xslt que maquete la evidencia domestica. Habra que modelar esto en bbdd);
		model.addAttribute("userForm", user);
		return "viewresponse";
	}
	private boolean waitAratito(String id) throws InterruptedException { 
//		long init=Calendar.getInstance().getTimeInMillis();
//		boolean wait=!responseManager.isDone(id);
//		boolean ok=!wait;
//			while ( wait) {
//			       logger.debug("Waiting for ThreadB to complete...");
//			       Thread.sleep(500);
//			       ok=responseManager.isDone(id);
//			       wait=!ok && Calendar.getInstance().getTimeInMillis()-init<timeout;
//			}  
//			return ok;
//		     
		return false;
	} 
}
