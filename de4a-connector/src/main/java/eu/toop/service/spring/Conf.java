package eu.toop.service.spring;

import java.io.File;
import java.io.FileInputStream;
import java.security.KeyStore;
import java.sql.SQLException;
import java.util.Locale;
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
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
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

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import eu.de4a.config.DataSourceConf;
import eu.toop.as4.domibus.soap.ClienteWS;
import eu.toop.as4.domibus.soap.ClienteWSAuthenticator;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableJpaRepositories(entityManagerFactoryRef = "entityManagerFactory", value = "eu")
@EnableWebMvc
@PropertySource("classpath:application.properties")
@ConfigurationProperties(prefix = "database")
@EnableAspectJAutoProxy
@EnableAutoConfiguration
@EnableTransactionManagement
@EnableScheduling
@ComponentScan("eu")
@EnableSwagger2
public class Conf implements WebMvcConfigurer {
	private static final Logger LOG = LoggerFactory.getLogger(Conf.class);
	
	private DataSourceConf dataSourceConf = new DataSourceConf();
	
	@Value("#{'${h2.console.port.jvm:${h2.console.port:21080}}'}")
	private String h2ConsolePort;
	
	
	@Bean
	public Docket api() {
		return new Docket(DocumentationType.SWAGGER_2).select()
				.apis(RequestHandlerSelectors.basePackage("eu"))
				.paths(PathSelectors.any()).build()
				.apiInfo(apiInfo());
	}
	
	private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
            .title("DE4A - Connector")
            .description("Connector component")
            .version("0.1.0")
            .termsOfServiceUrl("http://www.de4a.eu")
            .license("LICENSE")
            .licenseUrl("APACHE2")
            .build();
    }
	
	@Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("swagger-ui.html").addResourceLocations("classpath:/META-INF/resources/");
        registry.addResourceHandler("/webjars/**").addResourceLocations("classpath:/META-INF/resources/webjars/");
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
			LOG.error("Error creando el sender http", e);
		}
		return httpComponentsMessageSender;
	}

	@Bean
	public RestTemplate restTemplate() {
		HttpComponentsClientHttpRequestFactory httpComponentsClientHttpRequestFactory = new HttpComponentsClientHttpRequestFactory(
				httpClient());
		return new RestTemplate(httpComponentsClientHttpRequestFactory);
	}

	public HttpClient httpClient() {
		SSLConnectionSocketFactory factory;
		try {
			factory = sslConnectionSocketFactory();
			return HttpClientBuilder.create().setSSLSocketFactory(factory).build();
		} catch (Exception e) {
			LOG.error("No se puede crear la factorya ssl", e);
		}
		return HttpClientBuilder.create().build();

	}

	public SSLConnectionSocketFactory sslConnectionSocketFactory() throws Exception {
		// NoopHostnameVerifier essentially turns hostname verification off as otherwise
		// following error
		// is thrown: java.security.cert.CertificateException: No name matching
		// localhost found
		SSLContext context = sslContext();
		return new SSLConnectionSocketFactory(context, NoopHostnameVerifier.INSTANCE);
	}

	public SSLContext sslContext() throws Exception {
		String keystore = System.getProperties().getProperty("javax.net.ssl.keyStore");
		String keyStorePassword = System.getProperties().getProperty("javax.net.ssl.keyStorePassword");
		String trustStore = System.getProperties().getProperty("javax.net.ssl.trustStore");
		String trustStorePassword = System.getProperties().getProperty("javax.net.ssl.trustStorePassword");
		String type = System.getProperties().getProperty("javax.net.ssl.keyStoreType");
		LOG.debug(String.format("Usando ssl %s  %s  %s  %s ", keystore, keyStorePassword, trustStore,
				trustStorePassword));
		if (keystore == null || keyStorePassword == null || trustStore == null || trustStorePassword == null
				|| type == null) {
			LOG.error("No se ira por SSLContext alguno de los parametros es null");
			return null;
		}
		KeyStore keyStore = KeyStore.getInstance(type.toUpperCase());
		keyStore.load(new FileInputStream(new File(keystore)), keyStorePassword.toCharArray());

		return SSLContextBuilder.create().loadKeyMaterial(keyStore, keyStorePassword.toCharArray())
				.loadTrustMaterial(new File(trustStore), trustStorePassword.toCharArray()).build();
	}

	@Bean
	public Jaxb2Marshaller marshallerDomibus() {
		Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
		marshaller.setContextPath("eu.toop.as4.domibus.soap.auto");
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
