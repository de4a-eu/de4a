package eu.toop.back.servlet;
 

import java.io.IOException;
import java.util.Collection;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import eu.toop.controller.ResponseManager;

/**
 * This Action receives a SAML Response, shows it to the user and then validates it getting the attributes values
 */
@MultipartConfig()
@WebServlet(urlPatterns = { "/ReturnPage" })
public class ReturnAction extends AbstractSPServlet {

	private static final Logger logger = LogManager.getLogger(ReturnAction.class);
    private static final long serialVersionUID = 3660074009157921579L;
 
    
     
 
    @Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		if (acceptsHttpRedirect()) {
			doPost(request, response);
		} else {
			RequestDispatcher dispatcher = null;
			//String id="cambiame";
			String id=(String)request.getParameterMap().get("id")[0];
			dispatcher = request.getRequestDispatcher("/WEB-INF/view/returnPage.jsp?id="+id);
			dispatcher.forward(request, response);
		}
	}

	/**
	 * Post method
	 *
	 * @param request
	 * @param response
	 * @throws ServletException
	 * @throws IOException
	 */
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {  
		 Collection<Part> parts = request.getParts();
		 
	     WebApplicationContext ctx = WebApplicationContextUtils.getWebApplicationContext(getServletContext());
	     ResponseManager responseManager = (ResponseManager) ctx.getBean("responseManager");
	     responseManager.manageResponse(parts);
    }
	 
	
	
    /**
     * Method to be used by configuration. See sp.properties -> redirect.method key.
     *  This allows to be able to function eihter in
     * EIDAS or STORK mode respectively.
     *
     * @return a redirect method
     */
    public String getRedirectMethod() {
    	String ret = "post"; 
    	return ret;
    }

	@Override
	protected org.apache.logging.log4j.Logger getLogger() {
		// TODO Auto-generated method stub
		return logger;
	}
}