package eu.de4a.scsp.preview;

import java.util.Calendar;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.w3c.dom.Document;

import eu.de4a.conn.xml.DOMUtils;
import eu.de4a.exception.MessageException;
import eu.de4a.util.DE4AConstants; 
@Controller
public class PreviewController {
	private static final Logger logger =  LoggerFactory.getLogger (PreviewController.class);
	@RequestMapping(value = "/viewresponse", method = RequestMethod.POST) 
	public String viewresponse(@RequestParam String id,@RequestParam String urlReturn, Model model,HttpServletResponse httpServletResponse) { 
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
		//model.addAttribute("userForm", user);
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
