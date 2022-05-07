package eu.de4a.connector.config;

import java.nio.charset.StandardCharsets;
import java.util.Locale;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import org.apache.logging.log4j.ThreadContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ApplicationEventMulticaster;
import org.springframework.context.event.SimpleApplicationEventMulticaster;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.ServletContextAware;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.springframework.web.servlet.view.JstlView;
import com.fasterxml.classmate.TypeResolver;
import com.helger.commons.thirdparty.ELicense;
import com.helger.httpclient.HttpClientSettings;
import eu.de4a.connector.as4.servlet.AS4ServletListener;
import eu.de4a.iem.core.jaxb.common.EventNotificationType;
import eu.de4a.iem.core.jaxb.common.RedirectUserType;
import eu.de4a.iem.core.jaxb.common.RequestEventSubscriptionType;
import eu.de4a.iem.core.jaxb.common.RequestExtractMultiEvidenceIMType;
import eu.de4a.iem.core.jaxb.common.RequestExtractMultiEvidenceLUType;
import eu.de4a.iem.core.jaxb.common.RequestExtractMultiEvidenceUSIType;
import eu.de4a.iem.core.jaxb.common.ResponseErrorType;
import eu.de4a.iem.core.jaxb.common.ResponseEventSubscriptionType;
import eu.de4a.iem.core.jaxb.common.ResponseExtractMultiEvidenceType;
import eu.de4a.iem.jaxb.common.types.RequestLookupRoutingInformationType;
import eu.de4a.kafkaclient.DE4AKafkaSettings;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableScheduling
@EnableSwagger2
public class InitConf implements ServletContextAware {
    private ServletContext servletContext;
	private final HttpClientSettings httpSettings = new HttpClientSettings();

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
		final TypeResolver typeResolver = new TypeResolver();
		return new Docket(DocumentationType.SWAGGER_2)
				.select()
				.apis(RequestHandlerSelectors.basePackage("eu"))
				.paths(PathSelectors.any()).build()
				.additionalModels(typeResolver.resolve(RequestExtractMultiEvidenceIMType.class),
				        typeResolver.resolve(RequestExtractMultiEvidenceUSIType.class),
				        typeResolver.resolve(RequestExtractMultiEvidenceLUType.class),
				        typeResolver.resolve(RequestEventSubscriptionType.class),
				        typeResolver.resolve(RedirectUserType.class),
				        typeResolver.resolve(ResponseErrorType.class),
				        typeResolver.resolve(ResponseEventSubscriptionType.class),
				        typeResolver.resolve(ResponseExtractMultiEvidenceType.class),
				        typeResolver.resolve(RequestLookupRoutingInformationType.class),
				        typeResolver.resolve(EventNotificationType.class))
				.apiInfo(apiInfo());
	}

	private ApiInfo apiInfo() {
		return new ApiInfoBuilder()
			.title("DE4A - Connector")
			.description("DE4A Connector component - eDelivery Exchange")
			.version("2.0.0-SNAPSHOT")
			.termsOfServiceUrl("http://www.de4a.eu")
			.licenseUrl(ELicense.APACHE2.getURL())
			.license(ELicense.APACHE2.getDisplayName() + " " + ELicense.APACHE2.getVersion().getAsString(false))
			.build();
	}

	@Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }


  private final AS4ServletListener m_aListener = new AS4ServletListener ();

	/**
	 * Basic initialization of DCNG
	 */
	@PostConstruct
  private void configureAS4() {
	  m_aListener.contextInitialized(new ServletContextEvent(servletContext));
  }


	@PreDestroy
  public void shutDownAS4() {
    m_aListener.contextDestroyed(new ServletContextEvent(servletContext));
  }

	@Bean
	public ViewResolver viewResolver() {
		final InternalResourceViewResolver bean = new InternalResourceViewResolver();

		bean.setViewClass(JstlView.class);
		bean.setPrefix("/WEB-INF/view/");
		bean.setSuffix(".jsp");

		return bean;
	}

	@Bean(name = "localeResolver")
	public LocaleResolver localeResolver(@Value("${spring.messages.default_locale:#{null}}") final String locale) {
		final SessionLocaleResolver slr = new SessionLocaleResolver();
		if (locale != null && !locale.trim().isEmpty())
			slr.setDefaultLocale(new Locale(locale));
		else
			slr.setDefaultLocale(Locale.US);
		return slr;
	}


	@Bean
	CharacterEncodingFilter characterEncodingFilter() {
		final CharacterEncodingFilter filter = new CharacterEncodingFilter();
		filter.setEncoding(StandardCharsets.UTF_8.name());
		filter.setForceEncoding(true);
		return filter;
	}

	@Bean
    public ReloadableResourceBundleMessageSource messageSource() {

        final var source = new ReloadableResourceBundleMessageSource();
        source.setBasenames("classpath:messages/messages");
        source.setDefaultEncoding(StandardCharsets.UTF_8.name());
        source.setUseCodeAsDefaultMessage(true);

        return source;
    }

	@Bean(initMethod = "start", destroyMethod = "stop")
	public void kafkaSettings() {
 	    DE4AKafkaSettings.defaultProperties().put("bootstrap.servers", kafkaUrl);
        DE4AKafkaSettings.setKafkaEnabled(kafkaEnabled);
        DE4AKafkaSettings.setKafkaHttp(kafkaHttp);
        if(kafkaHttp) {
            DE4AKafkaSettings.setHttpClientSettings(this.httpSettings);
        }
        DE4AKafkaSettings.setLoggingEnabled(kafkaLoggingEnabled);
        DE4AKafkaSettings.setKafkaTopic(kafkaTopic);

        ThreadContext.put("metrics.enabled", "false");
	}

	@Bean(name = "applicationEventMulticaster")
	public ApplicationEventMulticaster simpleApplicationEventMulticaster() {
		final SimpleApplicationEventMulticaster eventMulticaster = new SimpleApplicationEventMulticaster();

		eventMulticaster.setTaskExecutor(new SimpleAsyncTaskExecutor());
		return eventMulticaster;
	}

    public void setServletContext(final ServletContext servletContext) {
        this.servletContext = servletContext;
    }
}