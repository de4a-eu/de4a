package eu.de4a.connector.config;

import java.nio.charset.StandardCharsets;
import java.util.Locale;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.event.ApplicationEventMulticaster;
import org.springframework.context.event.SimpleApplicationEventMulticaster;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.springframework.web.servlet.view.JstlView;


@PropertySource("classpath:application-test.properties")
@Configuration
@ComponentScan(basePackages = {"eu.de4a.connector.api", "eu.de4a.connector.utils"})
@Profile("test")
public class MockConf {
  // Required for testing only
  @Bean
  public RestTemplate restTemplate() {
    return new RestTemplate();
  }

  @Bean
  public ViewResolver viewResolver() {
    final InternalResourceViewResolver ret = new InternalResourceViewResolver();
    ret.setViewClass(JstlView.class);
    ret.setPrefix("/WEB-INF/view/");
    ret.setSuffix(".jsp");
    return ret;
  }

  @Bean(name = "localeResolver")
  public LocaleResolver localeResolver(@Value("${spring.messages.default_locale:#{null}}") final String locale) {
    final SessionLocaleResolver ret = new SessionLocaleResolver();
    if (locale != null && !locale.trim().isEmpty())
      ret.setDefaultLocale(new Locale(locale));
    else
      ret.setDefaultLocale(Locale.ENGLISH);
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
  public ReloadableResourceBundleMessageSource messageSource() {
    final var ret = new ReloadableResourceBundleMessageSource();
    ret.setBasenames("classpath:messages/messages");
    ret.setDefaultEncoding(StandardCharsets.UTF_8.name());
    ret.setUseCodeAsDefaultMessage(true);
    return ret;
  }

  @Bean(name = "applicationEventMulticaster")
  public ApplicationEventMulticaster simpleApplicationEventMulticaster() {
    final SimpleApplicationEventMulticaster ret = new SimpleApplicationEventMulticaster();
    ret.setTaskExecutor(new SimpleAsyncTaskExecutor());
    return ret;
  }
}
