package eu.toop.scsp.spring;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.KeyStore;

import javax.net.ssl.SSLContext;

import org.apache.http.client.HttpClient;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.ssl.SSLContextBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.FileSystemResource;
import org.springframework.ws.client.support.interceptor.ClientInterceptor;
import org.springframework.ws.soap.axiom.AxiomSoapMessageFactory;
import org.springframework.ws.soap.security.wss4j2.Wss4jSecurityInterceptor;
import org.springframework.ws.soap.security.wss4j2.support.CryptoFactoryBean;
import org.springframework.ws.transport.http.HttpComponentsMessageSender;

import eu.de4a.scsp.ws.client.ClientePidWS; 
@Configuration    
@PropertySource("classpath:application-pid.properties")
@ComponentScan("eu.de4a.scsp")   
@Order(Ordered.LOWEST_PRECEDENCE)
public class ConfPid    { 
  private static final Logger LOGGER = LoggerFactory.getLogger (ConfPid.class);
 
	  @Bean
	  public ClientePidWS clientePidWS( 
		   @Value("${scsp.keystore.path}")String keyStoreLocation,@Value("${scsp.keystore.password}") String keyStorePassword,
		   @Value("${scsp.pid.birthdaycertificate.endpoint}") String endpoint,@Value("${scsp.keystore.alias}") String keyAlias,   @Value("${scsp.keystore.alias.password}") String keyAliasPassword) {
 		   ClientePidWS cliente =  new ClientePidWS(messageFactoryPid()); 
 	   ClientInterceptor[] interceptors=new ClientInterceptor[1];
 	   interceptors[0]=signWssHandler(keyStoreLocation, keyStorePassword,keyAlias,keyAliasPassword);
 		   cliente.setInterceptors(interceptors);
 		   cliente.setEndpointPid(endpoint); 
 		   cliente.setMessageSender(httpComponentsMessageSenderPid());
 		   return cliente;
 		  }
		   public AxiomSoapMessageFactory messageFactoryPid() {
		   return new AxiomSoapMessageFactory();
	   } 
		    @Bean
		public HttpComponentsMessageSender httpComponentsMessageSenderPid() {
		     HttpComponentsMessageSender httpComponentsMessageSender = new HttpComponentsMessageSender();  
		     try { 
		    	 httpComponentsMessageSender.setHttpClient(httpClientPid()); 
			} catch (Exception e) {
				LOGGER.error("Error creando el sender http",e);
			}
		     return httpComponentsMessageSender;
		}
	   
		public HttpClient httpClientPid()   {
			   	SSLConnectionSocketFactory factory;
				try {
					factory = sslConnectionSocketFactoryPid();
					 return HttpClientBuilder.create().setSSLSocketFactory(factory) .build();
				} catch (Exception e) {
					LOGGER.error("No se puede crear la factorya ssl",e);
				}
				 return HttpClientBuilder.create()  .build();
			   
			  }

			  public SSLConnectionSocketFactory sslConnectionSocketFactoryPid() throws Exception   {
			    // NoopHostnameVerifier essentially turns hostname verification off as otherwise following error
			    // is thrown: java.security.cert.CertificateException: No name matching localhost found
				SSLContext context= sslContextPid();
				  return new SSLConnectionSocketFactory( context, NoopHostnameVerifier.INSTANCE);
	  }
	   public SSLContext sslContextPid() throws Exception {
		   String keystore=System.getProperties().getProperty("javax.net.ssl.keyStore");
		   String keyStorePassword=System.getProperties().getProperty("javax.net.ssl.keyStorePassword");
		   String trustStore=System.getProperties().getProperty("javax.net.ssl.trustStore");
		   String trustStorePassword=System.getProperties().getProperty("javax.net.ssl.trustStorePassword");
		   String type=System.getProperties().getProperty("javax.net.ssl.keyStoreType");
		   LOGGER.warn(String.format("Usando ssl %s  %s  %s  %s ",keystore,keyStorePassword,trustStore,trustStorePassword));
		   if(keystore==null ||  keyStorePassword==null ||trustStore==null ||trustStorePassword==null ||type==null  ) {
			   LOGGER.error("No se ira por SSLContext alguno de los parametros es null");
			   return null;
		   }
		   KeyStore keyStore = KeyStore.getInstance(type.toUpperCase());
		   keyStore.load(new FileInputStream( new File(keystore)),keyStorePassword.toCharArray());

			
		   return SSLContextBuilder.create()
				        .loadKeyMaterial(keyStore,keyStorePassword.toCharArray())
				        .loadTrustMaterial(new File(trustStore), trustStorePassword.toCharArray()).
				        build();
	   }
	   @Bean
	   public Wss4jSecurityInterceptor signWssHandler(@Value("${scsp.keystore.path}")String keyStoreLocation,@Value("${scsp.keystore.password}") String keyStorePassword,
			   @Value("${scsp.keystore.alias}") String keyAlias,   @Value("${scsp.keystore.alias.password}") String keyAliasPassword){
			Wss4jSecurityInterceptor ws=new Wss4jSecurityInterceptor();
			ws.setSecurementActions("Signature");
			ws.setSecurementSignatureKeyIdentifier("DirectReference");
			ws.setSecurementMustUnderstand(false);
			CryptoFactoryBean cryptoBean=crypto(keyStoreLocation, keyStorePassword);
			try {
				ws.setSecurementSignatureCrypto(  cryptoBean.getObject());
				ws.setSecurementSignatureUser(keyAlias);
				ws.setSecurementPassword(keyAliasPassword);
			} catch (Exception e) {
				LOGGER.error("Error config wss4j",e);
			}
			return ws;
		}
		@Bean
		CryptoFactoryBean crypto(@Value("${scsp.keystore.path}")String keyStoreLocation,@Value("${scsp.keystore.password}") String keyStorePassword) {
			CryptoFactoryBean crypto=new  CryptoFactoryBean();
			try {
				crypto.setKeyStoreLocation(new FileSystemResource(keyStoreLocation));
				crypto.setKeyStorePassword(keyStorePassword );
				crypto.setKeyStoreType("PKCS12");
			} catch (IOException e) {
				LOGGER.error("Error config crypto wss4j",e);
			}
			return crypto;
		}  
//	@Bean
//	ScspPropertyPlaceholderConfigurer scspPropertyPlaceholderConfigurer(SessionFactory sessionFactory) {
//		ScspPropertyPlaceholderConfigurer p= new ScspPropertyPlaceholderConfigurer(sessionFactory);
//		p.setLocation(new ClassPathResource("application.properties"));
//		return p;
//	}
//	 
//	@Bean
//	es.scsp.common.security.PolicyBuilder policy(){
//		return new PolicyBuilder("/scsp-ws-policy.xml");
//	}
//	@Bean
//	SessionFactoryManager sessionFactory() {
//		SessionFactoryManager sessionFactory=new SessionFactoryManager("classpath:/scsp-service.properties");
//		sessionFactory.setPackagesToScan("es.scsp.common.domain.**");	
//		return sessionFactory;
//	} 
//	@Bean
//	java.text.SimpleDateFormat dateFormater() {
//		return new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
//	} 
//	@Bean
//	AxiomSoapMessageFactory messageFactory() {
//		return new AxiomSoapMessageFactory();
//	}

//	
//	@Bean
//	org.springframework.ws.soap.security.wss4j.Wss4jSecurityInterceptor afirmaWssHandler(@Value("${scsp.keystore.path}")String keyStoreLocation,@Value("${scsp.keystore.password}") String keyStorePassword){
//		Wss4jSecurityInterceptor ws=new Wss4jSecurityInterceptor();
//		ws.setSecurementActions("Signature");
//		ws.setSecurementSignatureKeyIdentifier("DirectReference");
//		ws.setSecurementMustUnderstand(false);
//		CryptoFactoryBean cryptoBean=crypto(keyStoreLocation, keyStorePassword);
//		try {
//			ws.setSecurementSignatureCrypto(  cryptoBean.getObject());
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		return ws;
//	}
//	<bean id="afirmaWssHandler" class="org.springframework.ws.soap.security.wss4j.Wss4jSecurityInterceptor">
//	<property name="securementActions" value="Signature" />
//	<property name="securementSignatureKeyIdentifier" value="DirectReference" />
//	<property name="securementMustUnderstand" value="false" />
//	<property name="securementSignatureCrypto">
//		<bean class="org.springframework.ws.soap.security.wss4j.support.CryptoFactoryBean">
//			<property name="keyStorePassword" value="${config:keystorePass}" />
//			<property name="keyStoreLocation" value="${config:keystoreFile}" />
//		</bean>
//	</property>
//</bean>
}
