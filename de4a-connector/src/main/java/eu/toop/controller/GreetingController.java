package eu.toop.controller;

import java.util.Calendar;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import eu.toop.req.model.EvaluatorRequest;
import eu.toop.req.repository.EvaluatorRequestRepository;
import eu.toop.service.EvidenceRequestorManager;
import eu.toop.service.ResponseManager;

@Controller
public class GreetingController {
	private static final Logger logger =  LoggerFactory.getLogger (GreetingController.class);
	@Autowired
	private EvidenceRequestorManager evidenceRequestorManager; 
	@Autowired 
	private EvaluatorRequestRepository evaluatorRequestRepository; 
	@Autowired
	private ResponseManager responseManager; 
	@GetMapping("/greetingee")
	public String greetingForm(Model model) {
		model.addAttribute("greeting", new Greeting());
		return "greeting";
	}
	@RequestMapping(value = "/greeting", method = RequestMethod.POST)
	public String greetingSubmit( @RequestParam String eidas,HttpServletRequest request) {
//		evidenceRequestorManager.manageRequest(eidas);
		return "result";
	}
//	@RequestMapping(value = "/getreponse_old", method = RequestMethod.GET)
//	public String greetingSubmit(@RequestParam String id) {
//		 
//		return "result";
//	}
	
//	@GetMapping(value = "/download")
//	public void getEvidence(@RequestParam(value="id", required=false) String id,
//			HttpServletRequest request, 
//            HttpServletResponse response)  { 
//		File tempDir=null;
//		try {
////			ServletOutputStream baos= response.getOutputStream();
////			tempDir = FilesUtils.createTempDirectory();
////			waitAratito();
////			DOMSource source = new DOMSource(responseManager.getRespuesta());
////			FileWriter writer = new FileWriter(new File(tempDir.getPath()+"/responseexported.xml"));
////			StreamResult result = new StreamResult(writer);
////			TransformerFactory transformerFactory = TransformerFactory.newInstance();
////			Transformer transformer = transformerFactory.newTransformer();
////			transformer.transform(source, result);
////			byte[] zip = FilesUtils.empaquetarZip(tempDir);
////			baos.write(zip);
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} finally{
//			try {
//				response.getOutputStream().flush();
//				if(tempDir!=null){
//					FileUtils.deleteDirectory(tempDir);
//				}
//				
//			} catch (IOException e) {
//				logger.error("Error eliminando el directorio temporal donde se guardaron los pdf a ser exportados",e);
//			}
//		}
//	      ;
//	    
//	    
//	} 
	private boolean waitAratito(String id) throws InterruptedException { 
		long init=Calendar.getInstance().getTimeInMillis();
		boolean wait=!responseManager.isDone(id);
		boolean ok=!wait;
			while ( wait) {
			       logger.debug("Waiting for ThreadB to complete...");
			       Thread.sleep(500);
			       ok=responseManager.isDone(id);
			       wait=!ok && Calendar.getInstance().getTimeInMillis()-init<120000;
			}  
			return ok;
		     
	}
//	@RequestMapping(value = "/greetinggo", method = RequestMethod.POST)
//	public ModelAndView selectCUAA(@RequestParam(value="userID", required=false) String cuaa, ModelMap model) {
//	    //query & other...
//	    model.addAttribute("response", responseManager.getRespuesta()); 
//	    return new ModelAndView("redirect:http://localhost:8682/tc-evaluator/ReturnPage", model);
//	}
	@RequestMapping(value = "/getreponse", method = RequestMethod.GET)
	public RedirectView redirectWithUsingRedirectView(@RequestParam String id,RedirectAttributes attributes) { 
		        attributes.addAttribute("id", id);
		        try {
					boolean ok=waitAratito(id);
					if(!ok)logger.error("No se ha conseguido la repsuesta antes del timeout!!!");
				} catch (InterruptedException e) {
					logger.error("timeout waiting for data", e); 
				} 
				EvaluatorRequest evaluatorinfo=evaluatorRequestRepository.findById(id).orElse(null);
		        return new RedirectView(evaluatorinfo.getUrlreturn());
		        //return new RedirectView("https://des-de4a.redsara.es/de4a-tc-evaluator/ReturnPage");
	}
	/*
	@RequestMapping(value = "/greetinggo", method = RequestMethod.GET)
	public RedirectView method(HttpServletResponse httpServletResponse) {
	 	httpServletResponse.setHeader("Location", "http://localhost:8682/tc-evaluator/ReturnPage");
	    httpServletResponse.setStatus(302); 
		return rediretView;
	}*/
//	@PostMapping("/greeting")
//	public String greetingSubmit(@ModelAttribute Greeting greeting, Model model) {
//		model.addAttribute("greeting", greeting);
//		return "result";
//	}

}
