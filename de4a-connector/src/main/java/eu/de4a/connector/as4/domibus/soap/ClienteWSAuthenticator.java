package eu.de4a.connector.as4.domibus.soap; 

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.auth.UsernamePasswordCredentials;
/**
 * Proporciona los credenciales para autenticarse frente al servicio de amberpoint
 * Recupera los datos de la tabla mon_ws_parametros
 * Usuario bajo el parametro: amberpoint.ws.user
 * Password bajo el parametro: amberpoint.ws.password
 * */
public class ClienteWSAuthenticator   { 
	private static final Log LOG = LogFactory.getLog(ClienteWSAuthenticator.class); 
	public UsernamePasswordCredentials getAuth() {
			LOG.debug("Solicitando autenticacion del cliente de amberpoint");
			 
			return new UsernamePasswordCredentials("user","pass"); 
 	}

}
