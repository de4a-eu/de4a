package eu.de4a.connector.service.spring;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.sql.SQLException;
import java.util.Locale;
import java.util.Properties;
import java.util.StringTokenizer;

import javax.net.ssl.SSLContext;
import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.apache.http.HttpException;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.client.HttpClient;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.DefaultProxyRoutePlanner;
import org.apache.http.protocol.HttpContext;
import org.apache.http.ssl.SSLContextBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.boot.web.client.RestTemplateCustomizer;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.event.ApplicationEventMulticaster;
import org.springframework.context.event.SimpleApplicationEventMulticaster;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.core.annotation.Order;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.multipart.support.MultipartFilter;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.springframework.web.servlet.view.JstlView;
import org.springframework.ws.soap.axiom.AxiomSoapMessageFactory;
import org.springframework.ws.transport.http.HttpComponentsMessageSender;

import com.fasterxml.classmate.TypeResolver;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import eu.de4a.config.DataSourceConf;
import eu.de4a.connector.as4.domibus.soap.ClienteWS;
import eu.de4a.connector.as4.domibus.soap.ClienteWSAuthenticator;
import eu.de4a.iem.jaxb.common.types.RequestForwardEvidenceType;
import eu.de4a.iem.jaxb.common.types.RequestLookupRoutingInformationType;
import eu.de4a.iem.jaxb.common.types.RequestTransferEvidenceUSIIMDRType;
import eu.de4a.iem.jaxb.common.types.ResponseErrorType;
import eu.de4a.iem.jaxb.common.types.ResponseLookupRoutingInformationType;
import eu.de4a.iem.jaxb.common.types.ResponseTransferEvidenceType;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableJpaRepositories(entityManagerFactoryRef = "entityManagerFactory", value = "eu.de4a.connector")
@EnableWebMvc
@PropertySource("classpath:application.properties")
@ConfigurationProperties(prefix = "database")
@EnableAspectJAutoProxy
@EnableAutoConfiguration
@EnableTransactionManagement
@EnableScheduling
@ComponentScan("eu.de4a.connector")
@EnableSwagger2
public class Conf implements WebMvcConfigurer {
	private static final Logger LOG = LoggerFactory.getLogger(Conf.class);
	
	private DataSourceConf dataSourceConf = new DataSourceConf();
		
	@Value("#{'${h2.console.port.jvm:${h2.console.port:21080}}'}")
	private String h2ConsolePort;

	@Value("#{'${proxy.host: }'}")
	private String proxyHost;
	@Value("#{'${proxy.port:0 }'}")
	private int proxyPort;
	@Value("#{'${proxy.user: }'}")
	private String proxyUser;
	@Value("#{'${proxy.password: }'}")
	private String proxyPassword;
	@Value("#{'${proxy.non.hosts: }'}")
	private String proxyNonHosts;
	
	@Bean
	public Docket api() {
		TypeResolver typeResolver = new TypeResolver();
		return new Docket(DocumentationType.SWAGGER_2)
				.select()
				.apis(RequestHandlerSelectors.basePackage("eu"))
				.paths(PathSelectors.any()).build()
				.additionalModels(typeResolver.resolve(RequestTransferEvidenceUSIIMDRType.class), 
						typeResolver.resolve(ResponseTransferEvidenceType.class),
						typeResolver.resolve(ResponseErrorType.class),
						typeResolver.resolve(RequestForwardEvidenceType.class),
						typeResolver.resolve(RequestLookupRoutingInformationType.class),
						typeResolver.resolve(ResponseLookupRoutingInformationType.class))
				.apiInfo(apiInfo());
	}
	
	private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
            .title("DE4A - Connector")
            .description("DE4A Connector component - eDelivery Exchange")
            .version("0.1.0")
            .termsOfServiceUrl("http://www.de4a.eu")
            .licenseUrl("https://www.apache.org/licenses/LICENSE-2.0")
            .license("APACHE2")
            .build();
    }
	
	@Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.
            addResourceHandler("/swagger-ui/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/springfox-swagger-ui/")
                .resourceChain(false);
    }
	
	@Bean(initMethod = "start", destroyMethod = "stop")
	public org.h2.tools.Server h2WebConsonleServer() throws SQLException {
		return org.h2.tools.Server.createWebServer("-web", "-webAllowOthers", 
				"-ifNotExists", "-webDaemon", "-webPort", h2ConsolePort);
	}

	@Bean
	public ClienteWS clienteWS() {
		ClienteWS cliente = new ClienteWS(messageFactory());
		cliente.setMessageSender(httpComponentsMessageSender());
		cliente.setMarshaller(marshallerDomibus());
		cliente.setUnmarshaller(marshallerDomibus());
		return cliente;
	}

	public AxiomSoapMessageFactory messageFactory() {
		return new AxiomSoapMessageFactory();
	}

	@Bean
	public HttpComponentsMessageSender httpComponentsMessageSender() {
		HttpComponentsMessageSender httpComponentsMessageSender = new HttpComponentsMessageSender();
		try {
			httpComponentsMessageSender.setHttpClient(httpClient());
			httpComponentsMessageSender.setCredentials(clienteWSAuthenticator().getAuth());
		} catch (Exception e) {
			LOG.error("Error creating http sender", e);
		}
		return httpComponentsMessageSender;
	}

	@Bean
	public RestTemplate restTemplate() {
		RestTemplate template;
		if(!proxyHost.isEmpty()) {
			template=   new RestTemplateBuilder(new ProxyCustomizer()).build();
		}else {
			HttpComponentsClientHttpRequestFactory httpComponentsClientHttpRequestFactory = new HttpComponentsClientHttpRequestFactory(httpClient());
			template=  new RestTemplate(httpComponentsClientHttpRequestFactory);
		} 
		 
		return  template;
	} 
	public HttpClient httpClient() {
		SSLConnectionSocketFactory factory;
		try {
			factory = sslConnectionSocketFactory();
			return HttpClientBuilder.create().setSSLSocketFactory(factory).build();
		} catch (Exception e) {
			LOG.error("Error building SSL Factory", e);
		}
		return HttpClientBuilder.create().build();

	}
	class ProxyCustomizer implements RestTemplateCustomizer {

	    @Override
	    public void customize(RestTemplate restTemplate) {
	        HttpHost proxy = new HttpHost(proxyHost, proxyPort);
	        SSLConnectionSocketFactory factory;
			try {
				factory = sslConnectionSocketFactory();
			} catch (Exception e) {
				LOG.error("Error building SSL Factory", e);
				factory=SSLConnectionSocketFactory.getSocketFactory();
			}	
	        HttpClient httpClient = HttpClientBuilder.create()
	            .setRoutePlanner(new DefaultProxyRoutePlanner(proxy) {
	                @Override
	                public HttpHost determineProxy(HttpHost target, HttpRequest request, HttpContext context) throws HttpException {
	                	if (skipProxy(target.getHostName())) {
	                		return null;
	                	}
	                    return super.determineProxy(target, request, context);
	                }
	            }).setSSLSocketFactory(factory)
	            .build();
	        restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(httpClient));
	    }
	    private boolean skipProxy(String host) {
	    	if(proxyHost.isEmpty())return false;
	    	StringTokenizer st=new StringTokenizer(proxyNonHosts,";");
	    	while(st.hasMoreTokens()) {
	    		String pattern=st.nextToken();
	    		pattern=pattern.replaceAll("\\*","");
	    		if(host.contains(pattern))return true;
	    	}
	    	return false;
	    }
	}
	public SSLConnectionSocketFactory sslConnectionSocketFactory() {
		SSLContext context = sslContext();
		return new SSLConnectionSocketFactory(context);
	}

	public SSLContext sslContext() {
		String keystore = System.getProperties().getProperty("javax.net.ssl.keyStore");
		String keyStorePassword = System.getProperties().getProperty("javax.net.ssl.keyStorePassword");
		String trustStore = System.getProperties().getProperty("javax.net.ssl.trustStore");
		String trustStorePassword = System.getProperties().getProperty("javax.net.ssl.trustStorePassword");
		String type = System.getProperties().getProperty("javax.net.ssl.keyStoreType");
		if (keystore == null || keyStorePassword == null || trustStore == null || trustStorePassword == null
				|| type == null) {
			LOG.error("No se ira por SSLContext alguno de los parametros es null");
			return null;
		}
		try (FileInputStream fis = new FileInputStream(new File(keystore))) {
			KeyStore keyStore = KeyStore.getInstance(type.toUpperCase());
			keyStore.load(fis, keyStorePassword.toCharArray());

			return SSLContextBuilder.create().loadKeyMaterial(keyStore, keyStorePassword.toCharArray())
					.loadTrustMaterial(new File(trustStore), trustStorePassword.toCharArray()).build();
		} catch (IOException | NoSuchAlgorithmException | CertificateException | KeyStoreException
				| KeyManagementException | UnrecoverableKeyException e) {
			String msg = "Cannot load certificate";
			LOG.error(msg, e);
			return null;
		}
	}

	@Bean
	public Jaxb2Marshaller marshallerDomibus() {
		Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
		marshaller.setContextPath("eu.de4a.connector.as4.domibus.soap.auto");
		return marshaller;
	}

	@Bean
	public ClienteWSAuthenticator clienteWSAuthenticator() {
		return new ClienteWSAuthenticator();
	}

	@Override
	public void addViewControllers(ViewControllerRegistry registry) {
		registry.addViewController("/").setViewName("index");
		registry.addViewController("/swagger-ui/").setViewName("forward:/swagger-ui/index.html");
	}

	@Bean
	public ViewResolver viewResolver() {
		InternalResourceViewResolver bean = new InternalResourceViewResolver();

		bean.setViewClass(JstlView.class);
		bean.setPrefix("/WEB-INF/view/");
		bean.setSuffix(".jsp");

		return bean;
	}

	@Bean(name = "localeResolver")
	public LocaleResolver localeResolver(@Value("${spring.messages.default_locale:#{null}}") String locale) {
		SessionLocaleResolver slr = new SessionLocaleResolver();
		if (locale != null && !locale.trim().isEmpty())
			slr.setDefaultLocale(new Locale(locale));
		else
			slr.setDefaultLocale(Locale.ENGLISH);
		return slr;
	}

	@Bean
	public MessageSource messageSource() {
		ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
		messageSource.setBasename("classpath:messages/messages");
		messageSource.setDefaultEncoding("UTF-8");
		return messageSource;
	}

	@Bean
	@Order(0)
	public MultipartFilter multipartFilter() {
		MultipartFilter multipartFilter = new MultipartFilter();
		multipartFilter.setMultipartResolverBeanName("multipartResolver");
		return multipartFilter;
	}

	@Bean(name = "multipartResolver")
	public CommonsMultipartResolver createMultipartResolver() {
		CommonsMultipartResolver resolver = new CommonsMultipartResolver();
		resolver.setDefaultEncoding("UTF-8");
		return resolver;
	}

	@Bean(name = "applicationEventMulticaster")
	public ApplicationEventMulticaster simpleApplicationEventMulticaster() {
		SimpleApplicationEventMulticaster eventMulticaster = new SimpleApplicationEventMulticaster();

		eventMulticaster.setTaskExecutor(new SimpleAsyncTaskExecutor());
		return eventMulticaster;
	}

	@Bean(destroyMethod = "close")
	public DataSource dataSource() {
		HikariConfig dataSourceConfig = new HikariConfig();
		dataSourceConfig.setDriverClassName(dataSourceConf.getDriverClassName());
		dataSourceConfig.setJdbcUrl(dataSourceConf.getUrl());
		dataSourceConfig.setUsername(dataSourceConf.getUsername());
		dataSourceConfig.setPassword(dataSourceConf.getPassword());
		
		try {
			return new HikariDataSource(dataSourceConfig);
		} catch (Exception e) {
			e.printStackTrace();
			LOG.error("Fatallity!...error datasource", e);
			return null;
		}
	}

	@Bean
	public LocalContainerEntityManagerFactoryBean entityManagerFactory(DataSource dataSource) {
		LocalContainerEntityManagerFactoryBean entityManagerFactoryBean = new LocalContainerEntityManagerFactoryBean();
		entityManagerFactoryBean.setDataSource(dataSource);
		entityManagerFactoryBean.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
		entityManagerFactoryBean.setPackagesToScan("eu");

		Properties jpaProperties = new Properties();

		// Configures the used database dialect. This allows Hibernate to create SQL
		// that is optimized for the used database.
		jpaProperties.put("hibernate.dialect", dataSourceConf.getJpaHibernate().getDialectPlatform());

		// Specifies the action that is invoked to the database when the Hibernate
		// SessionFactory is created or closed.
		jpaProperties.put("hibernate.hbm2ddl.auto", dataSourceConf.getJpaHibernate().getDdlAuto());

		// Configures the naming strategy that is used when Hibernate creates
		// new database objects and schema elements
		jpaProperties.put("hibernate.ejb.naming_strategy", dataSourceConf.getJpaHibernate().getNamingStrategy());

		// If the value of this property is true, Hibernate writes all SQL
		// statements to the console.
		jpaProperties.put("hibernate.show_sql", dataSourceConf.getJpaHibernate().getShowSql());

		// If the value of this property is true, Hibernate will format the SQL
		// that is written to the console.
		jpaProperties.put("hibernate.format_sql", dataSourceConf.getJpaHibernate().getFormatSql());

		entityManagerFactoryBean.setJpaProperties(jpaProperties);

		return entityManagerFactoryBean;
	}

	@Bean
	JpaTransactionManager transactionManager(EntityManagerFactory entityManagerFactory) {
		JpaTransactionManager transactionManager = new JpaTransactionManager();
		transactionManager.setEntityManagerFactory(entityManagerFactory);
		return transactionManager;
	}
	
	public DataSourceConf getDataSourceConf() {
		return dataSourceConf;
	}

	public void setDataSourceConf(DataSourceConf dataSourceConf) {
		this.dataSourceConf = dataSourceConf;
	}

}
