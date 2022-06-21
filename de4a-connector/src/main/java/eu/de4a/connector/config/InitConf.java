package eu.de4a.connector.config;

import java.nio.charset.StandardCharsets;
import java.util.Locale;
import javax.servlet.ServletContext;
import org.apache.logging.log4j.ThreadContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ApplicationEventMulticaster;
import org.springframework.context.event.SimpleApplicationEventMulticaster;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.web.context.ServletContextAware;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.springframework.web.servlet.view.JstlView;
import com.helger.dcng.core.http.DcngHttpClientSettings;

import eu.de4a.connector.api.service.DeliverServiceIT1;
import eu.de4a.kafkaclient.DE4AKafkaSettings;

@Configuration
public class InitConf implements ServletContextAware {
	
	 private static final Logger LOGGER = LoggerFactory.getLogger (InitConf.class);
			 
  private ServletContext servletContext;

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
  @Value("${legacy.do.url}")
  private String legacyDO;
 
  public void setServletContext(final ServletContext servletContext) {
    this.servletContext = servletContext;
  }

  @Bean
  ViewResolver viewResolver() {
    final InternalResourceViewResolver ret = new InternalResourceViewResolver();
    ret.setViewClass(JstlView.class);
    ret.setPrefix("/WEB-INF/view/");
    ret.setSuffix(".jsp");
    return ret;
  }

  @Bean(name = "localeResolver")
  LocaleResolver localeResolver(@Value("${spring.messages.default_locale:#{null}}") final String locale) {
    final SessionLocaleResolver ret = new SessionLocaleResolver();
    if (locale != null && !locale.trim().isEmpty())
      ret.setDefaultLocale(new Locale(locale));
    else
      ret.setDefaultLocale(Locale.US);
    return ret;
  }

  @Bean
  CharacterEncodingFilter characterEncodingFilter() {
    final CharacterEncodingFilter ret = new CharacterEncodingFilter();
    ret.setEncoding(StandardCharsets.UTF_8.name());
    ret.setForceEncoding(true);
    return ret;
  }

  @Bean
  ReloadableResourceBundleMessageSource messageSource() {
    final var ret = new ReloadableResourceBundleMessageSource();
    ret.setBasenames("classpath:messages/messages");
    ret.setDefaultEncoding(StandardCharsets.UTF_8.name());
    ret.setUseCodeAsDefaultMessage(true);
    return ret;
  }

  @Bean(initMethod = "start", destroyMethod = "stop")
  void kafkaSettings() {
    DE4AKafkaSettings.defaultProperties().put("bootstrap.servers", kafkaUrl);
    DE4AKafkaSettings.setKafkaEnabled(kafkaEnabled);
    DE4AKafkaSettings.setKafkaHttp(kafkaHttp);
    if (kafkaHttp) {
      DE4AKafkaSettings.setHttpClientSettings(new DcngHttpClientSettings());
    }
    DE4AKafkaSettings.setLoggingEnabled(kafkaLoggingEnabled);
    DE4AKafkaSettings.setKafkaTopic(kafkaTopic);

    ThreadContext.put("metrics.enabled", "false");
  }
  
  @Bean(initMethod = "start", destroyMethod = "stop")
  void legacySettings() {
	  DeliverServiceIT1.setLegacyDOURL(legacyDO);
  }

  @Bean(name = "applicationEventMulticaster")
  ApplicationEventMulticaster simpleApplicationEventMulticaster() {
    final SimpleApplicationEventMulticaster ret = new SimpleApplicationEventMulticaster();
    ret.setTaskExecutor(new SimpleAsyncTaskExecutor());
    return ret;
  }
}
