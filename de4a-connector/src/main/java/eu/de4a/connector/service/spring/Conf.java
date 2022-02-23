package eu.de4a.connector.service.spring;

import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.Locale;

import org.apache.logging.log4j.ThreadContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.event.ApplicationEventMulticaster;
import org.springframework.context.event.SimpleApplicationEventMulticaster;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.core.annotation.Order;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.multipart.support.MultipartFilter;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.springframework.web.servlet.view.JstlView;
import org.springframework.ws.soap.axiom.AxiomSoapMessageFactory;
import org.springframework.ws.transport.http.HttpComponentsMessageSender;

import com.fasterxml.classmate.TypeResolver;
import com.helger.httpclient.HttpClientSettings;
import com.helger.peppol.sml.ISMLInfo;
import com.helger.peppol.sml.SMLInfo;

import eu.de4a.connector.as4.domibus.soap.DomibusClientWS;
import eu.de4a.iem.jaxb.common.types.RedirectUserType;
import eu.de4a.iem.jaxb.common.types.RequestExtractEvidenceType;
import eu.de4a.iem.jaxb.common.types.RequestForwardEvidenceType;
import eu.de4a.iem.jaxb.common.types.RequestLookupRoutingInformationType;
import eu.de4a.iem.jaxb.common.types.RequestTransferEvidenceUSIDTType;
import eu.de4a.iem.jaxb.common.types.ResponseErrorType;
import eu.de4a.iem.jaxb.common.types.ResponseLookupRoutingInformationType;
import eu.de4a.iem.jaxb.common.types.ResponseTransferEvidenceType;
import eu.de4a.kafkaclient.DE4AKafkaSettings;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@PropertySource({"classpath:application.properties", "classpath:phase4.properties"})
@EnableScheduling
@EnableSwagger2
public class Conf {
	
	private HttpClientSettings httpSettings = new HttpClientSettings();
	
	@Value("${de4a.kafka.enabled:false}")
	private boolean kafkaEnabled;
	@Value("${de4a.kafka.logging.enabled:true}")
	private boolean kafkaLoggingEnabled;
    @Value("${de4a.kafka.http.enabled:false}")
    private boolean kafkaHttp;
    @Value("${de4a.kafka.url:#{null}}")
    private String kafkaUrl;
    @Value("${de4a.kafka.topic:#{de4a-connector}}")
    private String kafkaTopic;
	
	@Bean
	public Docket api() {
		TypeResolver typeResolver = new TypeResolver();
		return new Docket(DocumentationType.SWAGGER_2)
				.select()
				.apis(RequestHandlerSelectors.basePackage("eu"))
				.paths(PathSelectors.any()).build()
				.additionalModels(typeResolver.resolve(RequestExtractEvidenceType.class),
						typeResolver.resolve(ResponseTransferEvidenceType.class),
						typeResolver.resolve(ResponseErrorType.class),
						typeResolver.resolve(RequestForwardEvidenceType.class),
						typeResolver.resolve(RequestLookupRoutingInformationType.class),
						typeResolver.resolve(ResponseLookupRoutingInformationType.class),
				        typeResolver.resolve(RequestTransferEvidenceUSIDTType.class),
				        typeResolver.resolve(RedirectUserType.class))
				.apiInfo(apiInfo());
	}

	private ApiInfo apiInfo() {
		return new ApiInfoBuilder()
			.title("DE4A - Connector")
			.description("DE4A Connector component - eDelivery Exchange")
			.version("0.2.0")
			.termsOfServiceUrl("http://www.de4a.eu")
			.licenseUrl("https://www.apache.org/licenses/LICENSE-2.0")
			.license("APACHE2")
			.build();
	}

	@Bean(initMethod = "start", destroyMethod = "stop")
    @ConditionalOnProperty(prefix = "h2.console", name = "port")
    public org.h2.tools.Server h2WebConsonleServer(@Value("${h2.console.port}") String h2ConsolePort) 
            throws SQLException {
        return org.h2.tools.Server.createWebServer("-web", "-ifNotExists", "-webDaemon", 
                "-webPort", h2ConsolePort);
    }
	
	@Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

	@Bean
	public DomibusClientWS clienteWS() {
		DomibusClientWS cliente = new DomibusClientWS(messageFactory());
		cliente.setMessageSender(new HttpComponentsMessageSender());
		cliente.setMarshaller(marshallerDomibus());
		cliente.setUnmarshaller(marshallerDomibus());
		return cliente;
	}

	public AxiomSoapMessageFactory messageFactory() {
		return new AxiomSoapMessageFactory();
	}

	@Bean
	public Jaxb2Marshaller marshallerDomibus() {
		Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
		marshaller.setContextPath("eu.de4a.connector.as4.domibus.soap.auto");
		return marshaller;
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
	CharacterEncodingFilter characterEncodingFilter() {
		CharacterEncodingFilter filter = new CharacterEncodingFilter();
		filter.setEncoding(StandardCharsets.UTF_8.name());
		filter.setForceEncoding(true);
		return filter;
	}

	@Bean
	public MessageSource messageSource() {
		ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
		messageSource.setBasename("classpath:messages/messages");
		messageSource.setDefaultEncoding(StandardCharsets.UTF_8.name());
		return messageSource;
	}

	public void kafkaSettings() {
 	    DE4AKafkaSettings.defaultProperties().put("bootstrap.servers", kafkaUrl);
        DE4AKafkaSettings.setKafkaEnabled(kafkaEnabled);
        DE4AKafkaSettings.setKafkaHttp(kafkaHttp);
        if(kafkaHttp) {
            DE4AKafkaSettings.setHttpClientSetting(this.httpSettings);
        }
        DE4AKafkaSettings.setLoggingEnabled(kafkaLoggingEnabled);        
        DE4AKafkaSettings.setKafkaTopic(kafkaTopic);
        
        ThreadContext.put("metrics.enabled", "false");
	}
	
	@Bean
	public ISMLInfo smlConfig(@Value("${sml.service.id}") String id, 
            @Value("${sml.displayname}") String displayName, @Value("${sml.dnszone}") String dnsZone,
            @Value("${sml.managementservice.endpoint}") String managementService, 
            @Value("${sml.certificate.required}") boolean isCertificateRequired ) {
	    return new SMLInfo(id, displayName, dnsZone, managementService, isCertificateRequired);
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
		resolver.setDefaultEncoding(StandardCharsets.UTF_8.name());
		return resolver;
	}

	@Bean(name = "applicationEventMulticaster")
	public ApplicationEventMulticaster simpleApplicationEventMulticaster() {
		SimpleApplicationEventMulticaster eventMulticaster = new SimpleApplicationEventMulticaster();

		eventMulticaster.setTaskExecutor(new SimpleAsyncTaskExecutor());
		return eventMulticaster;
	}

}
