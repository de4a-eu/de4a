package eu.toop.back.servlet;
 

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
  
 

public abstract class AbstractSPServlet extends HttpServlet {

	private static final long serialVersionUID = 1764139449184461900L;

	/**
	 * Abstract logging impl.
	 * @return the concrete logger of implementing servlet
	 */
	protected abstract org.apache.logging.log4j.Logger getLogger();
 

	/**
	 * Method used to renew the http session in traditional web application.
	 *
	 * @return the new session Id
	 */
	private String sessionIdRegenerationInWebApp(HttpServletRequest request) {
		request.getSession(false).invalidate();
		String currentSession = request.getSession(true).getId();
		// Servlet code to renew the session
		getLogger().debug("Session RENEWED SessionIdRegenerationInWebApp [domain : {}][path {}][sessionId {}]", request.getServerName(), getServletContext().getContextPath(),currentSession);
		return currentSession;
	}

	/**
	 * Sets HTTPOnly Header on the session to prevent cookies from being accessed through
	 * client-side script.
	 *
	 * @param renewSession indicates that the session cookie will be renewed
	 */
	protected final void setHTTPOnlyHeaderToSession(final boolean renewSession, HttpServletRequest request, HttpServletResponse response) {
		if (request != null && request.getSession(false) != null) {
			// Renewing the session if necessary
			String currentSession;
			String messageLog;
			if (renewSession){
				currentSession = sessionIdRegenerationInWebApp(request);
				messageLog = "http session Renewed : {}";
			} else{
				currentSession = request.getSession().getId();
				messageLog = "http session obtained from request : {}";
			}
			getLogger().info(messageLog, currentSession);
			// changing session cookie to http only cookie
			if (request.getCookies() != null && request.isRequestedSessionIdFromCookie()) {
				//Session Id requested by the client, obtained from the cookie
				final String requestedSessionId = request.getRequestedSessionId();
				for (Cookie cookie : request.getCookies()) {
					getLogger().debug("Treating cookie [domain][path][name][value] : [{}][{}][{}][{}]",
							cookie.getName(),
							cookie.getPath(),
							cookie.getName(),
							cookie.getValue());
					if (currentSession.equals(requestedSessionId)) {
						// Removes old version
						boolean isSecure = request.isSecure();
						getLogger().debug("Cookie==session : Remove and replacing with HttpOnly {}", cookie.toString());
						getLogger().debug("Is using SSL?", isSecure);

						// Create new one httpOnly
						StringBuilder httpOnlyCookie = new StringBuilder(cookie.getName()) ;
						response.setHeader("SET-COOKIE", httpOnlyCookie.toString());
					}
				}
			}
		} else {
			getLogger().warn("Request or Session is null !");
		}
	}

	/**
	 * Encodes any given URL.
	 *
	 * @param url The URL to be encoded.
	 *
	 * @return The encoded URL.
	 */
	protected final String encodeURL(final String url, HttpServletResponse response) {
		return response.encodeURL(url);
	}

	@Override
	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		super.service(request, response);
	}



	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		getLogger().warn("GET method invocation : possible spidering");
	}

	@Override
	protected void doHead(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		getLogger().warn("HEAD method invocation : possible spidering");
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		getLogger().warn("POST method invocation : possible spidering");
	}

	@Override
	protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		getLogger().warn("DELETE method invocation : possible spidering");
	}

	@Override
	protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		getLogger().warn("PUT method invocation : possible spidering");
	}

	@Override
	protected void doOptions(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		getLogger().warn("OPTIONS method invocation : possible spidering");
	}

	@Override
	protected void doTrace(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		getLogger().warn("TRACE method invocation : possible spidering");
	}


	protected final boolean acceptsHttpRedirect(){
		Boolean acceptGet = false;
		return acceptGet!=null && acceptGet;
	}
}
