package eu.de4a.scsp.spring;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.KeyStore;
import java.util.Properties;

import javax.net.ssl.SSLContext;
import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

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
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.springframework.web.servlet.view.JstlView;
import org.springframework.ws.client.support.interceptor.ClientInterceptor;
import org.springframework.ws.soap.axiom.AxiomSoapMessageFactory;
import org.springframework.ws.soap.security.wss4j2.Wss4jSecurityInterceptor;
import org.springframework.ws.soap.security.wss4j2.support.CryptoFactoryBean;
import org.springframework.ws.transport.http.HttpComponentsMessageSender;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import eu.de4a.scsp.ws.client.ClientePidWS;

@Configuration
@PropertySource("classpath:application-pid.properties")
@ComponentScan("eu.de4a.scsp")
@Order(Ordered.LOWEST_PRECEDENCE)
@EnableJpaRepositories(entityManagerFactoryRef = "entityManagerFactory", value = "eu")
@EnableTransactionManagement
public class ConfPid  implements WebMvcConfigurer {
	private static final Logger LOGGER = LoggerFactory.getLogger(ConfPid.class);
	  @Override
	  public void addViewControllers(ViewControllerRegistry registry) {
	     registry.addViewController("/").setViewName("index");
	  }
	
	  @Bean
	  public ViewResolver viewResolver() {
	     InternalResourceViewResolver bean = new InternalResourceViewResolver();
	
	     bean.setViewClass(JstlView.class);
	     bean.setPrefix("/WEB-INF/view/");
	     bean.setSuffix(".jsp");
	
	     return bean;
  }

	@Bean
	public ClientePidWS clientePidWS(@Value("${scsp.keystore.path}") String keyStoreLocation,
			@Value("${scsp.keystore.password}") String keyStorePassword,
			@Value("${scsp.pid.birthdaycertificate.endpoint}") String endpoint,
			@Value("${scsp.keystore.alias}") String keyAlias,
			@Value("${scsp.keystore.alias.password}") String keyAliasPassword) {
		ClientePidWS cliente = new ClientePidWS(messageFactoryPid());
		ClientInterceptor[] interceptors = new ClientInterceptor[1];
		interceptors[0] = signWssHandler(keyStoreLocation, keyStorePassword, keyAlias, keyAliasPassword);
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
			LOGGER.error("Error creando el sender http", e);
		}
		return httpComponentsMessageSender;
	}

	public HttpClient httpClientPid() {
		SSLConnectionSocketFactory factory;
		try {
			factory = sslConnectionSocketFactoryPid();
			return HttpClientBuilder.create().setSSLSocketFactory(factory).build();
		} catch (Exception e) {
			LOGGER.error("No se puede crear la factorya ssl", e);
		}
		return HttpClientBuilder.create().build();

	}

	public SSLConnectionSocketFactory sslConnectionSocketFactoryPid() throws Exception {
		// NoopHostnameVerifier essentially turns hostname verification off as otherwise
		// following error
		// is thrown: java.security.cert.CertificateException: No name matching
		// localhost found
		SSLContext context = sslContextPid();
		return new SSLConnectionSocketFactory(context, NoopHostnameVerifier.INSTANCE);
	}

	public SSLContext sslContextPid() throws Exception {
		String keystore = System.getProperties().getProperty("javax.net.ssl.keyStore");
		String keyStorePassword = System.getProperties().getProperty("javax.net.ssl.keyStorePassword");
		String trustStore = System.getProperties().getProperty("javax.net.ssl.trustStore");
		String trustStorePassword = System.getProperties().getProperty("javax.net.ssl.trustStorePassword");
		String type = System.getProperties().getProperty("javax.net.ssl.keyStoreType");
		LOGGER.warn(String.format("Usando ssl %s  %s  %s  %s ", keystore, keyStorePassword, trustStore,
				trustStorePassword));
		if (keystore == null || keyStorePassword == null || trustStore == null || trustStorePassword == null
				|| type == null) {
			LOGGER.error("No se ira por SSLContext alguno de los parametros es null");
			return null;
		}
		KeyStore keyStore = KeyStore.getInstance(type.toUpperCase());
		keyStore.load(new FileInputStream(new File(keystore)), keyStorePassword.toCharArray());

		return SSLContextBuilder.create().loadKeyMaterial(keyStore, keyStorePassword.toCharArray())
				.loadTrustMaterial(new File(trustStore), trustStorePassword.toCharArray()).build();
	}

	@Bean
	public Wss4jSecurityInterceptor signWssHandler(@Value("${scsp.keystore.path}") String keyStoreLocation,
			@Value("${scsp.keystore.password}") String keyStorePassword,
			@Value("${scsp.keystore.alias}") String keyAlias,
			@Value("${scsp.keystore.alias.password}") String keyAliasPassword) {
		Wss4jSecurityInterceptor ws = new Wss4jSecurityInterceptor();
		ws.setSecurementActions("Signature");
		ws.setSecurementSignatureKeyIdentifier("DirectReference");
		ws.setSecurementMustUnderstand(false);
		CryptoFactoryBean cryptoBean = crypto(keyStoreLocation, keyStorePassword);
		try {
			ws.setSecurementSignatureCrypto(cryptoBean.getObject());
			ws.setSecurementSignatureUser(keyAlias);
			ws.setSecurementPassword(keyAliasPassword);
		} catch (Exception e) {
			LOGGER.error("Error config wss4j", e);
		}
		return ws;
	}

	@Bean
	CryptoFactoryBean crypto(@Value("${scsp.keystore.path}") String keyStoreLocation,
			@Value("${scsp.keystore.password}") String keyStorePassword) {
		CryptoFactoryBean crypto = new CryptoFactoryBean();
		try {
			crypto.setKeyStoreLocation(new FileSystemResource(keyStoreLocation));
			crypto.setKeyStorePassword(keyStorePassword);
			crypto.setKeyStoreType("PKCS12");
		} catch (IOException e) {
			LOGGER.error("Error config crypto wss4j", e);
		}
		return crypto;
	}

	@Bean(name = "multipartResolver")
	public CommonsMultipartResolver createMultipartResolver() {
		CommonsMultipartResolver resolver = new CommonsMultipartResolver();
		resolver.setDefaultEncoding("UTF-8");
		return resolver;
	}

	@Bean(destroyMethod = "close")
	public DataSource dataSource(@Value("${database.datasourceConf.url}") String url,
			@Value("${database.datasourceConf.driverClassName}") String driverClassName,
			@Value("${database.datasourceConf.username}") String user,
			@Value("${database.datasourceConf.password}") String password) {
		HikariConfig dataSourceConfig = new HikariConfig();
		dataSourceConfig.setDriverClassName(driverClassName);
		dataSourceConfig.setJdbcUrl(url);
		dataSourceConfig.setUsername(user);
		dataSourceConfig.setPassword(password);

		return (DataSource) new HikariDataSource(dataSourceConfig);
	}

	@Bean
	public LocalContainerEntityManagerFactoryBean entityManagerFactory(DataSource dataSource,
			@Value("${database.datasourceConf.jpaHibernate.dialectPlatform}") String dialectPlatform,
			@Value("${database.datasourceConf.jpaHibernate.ddlauto}") String ddlAuto,
			@Value("${database.datasourceConf.jpaHibernate.generateddl}") String generateDdl,
			@Value("${database.datasourceConf.jpaHibernate.namingStrategy}") String namingStrategy,
			@Value("${database.datasourceConf.jpaHibernate.showSql}") String showSql,
			@Value("${database.datasourceConf.jpaHibernate.formatSql}") String formatSql) {
		LocalContainerEntityManagerFactoryBean entityManagerFactoryBean = new LocalContainerEntityManagerFactoryBean();
		entityManagerFactoryBean.setDataSource(dataSource);
		entityManagerFactoryBean.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
		entityManagerFactoryBean.setPackagesToScan("eu");

		Properties jpaProperties = new Properties();

		// Configures the used database dialect. This allows Hibernate to create SQL
		// that is optimized for the used database.
		jpaProperties.put("hibernate.dialect", dialectPlatform);

		// Specifies the action that is invoked to the database when the Hibernate
		// SessionFactory is created or closed.
		jpaProperties.put("hibernate.hbm2ddl.auto", ddlAuto);

		// Configures the naming strategy that is used when Hibernate creates
		// new database objects and schema elements
		jpaProperties.put("hibernate.ejb.naming_strategy", namingStrategy);

		// If the value of this property is true, Hibernate writes all SQL
		// statements to the console.
		jpaProperties.put("hibernate.show_sql", showSql);

		// If the value of this property is true, Hibernate will format the SQL
		// that is written to the console.
		jpaProperties.put("hibernate.format_sql", formatSql);

		entityManagerFactoryBean.setJpaProperties(jpaProperties);

		return entityManagerFactoryBean;
	}

	@Bean
	JpaTransactionManager transactionManager(EntityManagerFactory entityManagerFactory) {
		JpaTransactionManager transactionManager = new JpaTransactionManager();
		transactionManager.setEntityManagerFactory(entityManagerFactory);
		return transactionManager;
	}

}
