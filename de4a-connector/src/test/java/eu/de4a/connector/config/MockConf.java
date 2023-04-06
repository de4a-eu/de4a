/*
 * Copyright (C) 2023, Partners of the EU funded DE4A project consortium
 *   (https://www.de4a.eu/consortium), under Grant Agreement No.870635
 * Author:
 *   Austrian Federal Computing Center (BRZ)
 *   Spanish Ministry of Economic Affairs and Digital Transformation -
 *     General Secretariat for Digital Administration (MAETD - SGAD)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
