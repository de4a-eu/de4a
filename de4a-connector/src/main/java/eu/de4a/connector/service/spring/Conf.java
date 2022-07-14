package eu.de4a.connector.service.spring;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
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
import org.apache.http.conn.routing.HttpRoutePlanner;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.DefaultProxyRoutePlanner;
import org.apache.http.protocol.HttpContext;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.ssl.SSLContexts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
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
import org.springframework.web.filter.CharacterEncodingFilter;
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
import com.helger.httpclient.HttpClientSettings;
import com.helger.peppol.sml.ISMLInfo;
import com.helger.peppol.sml.SMLInfo;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import eu.de4a.config.DataSourceConf;
import eu.de4a.connector.as4.domibus.soap.DomibusClientWS;
import eu.de4a.iem.jaxb.common.types.RequestForwardEvidenceType;
import eu.de4a.iem.jaxb.common.types.RequestLookupRoutingInformationType;
import eu.de4a.iem.jaxb.common.types.RequestTransferEvidenceUSIDTType;
import eu.de4a.iem.jaxb.common.types.RequestTransferEvidenceUSIIMDRType;
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
@EnableJpaRepositories (entityManagerFactoryRef = "entityManagerFactory", value = "eu.de4a.connector")
@EnableWebMvc
@PropertySource ({ "classpath:application.properties", "classpath:phase4.properties" })
@ConfigurationProperties (prefix = "database")
@EnableAspectJAutoProxy
@EnableAutoConfiguration
@EnableTransactionManagement
@EnableScheduling
@ComponentScan ("eu.de4a.connector")
@EnableSwagger2
public class Conf implements WebMvcConfigurer
{
  private static final Logger LOG = LoggerFactory.getLogger (Conf.class);

  private DataSourceConf dataSourceConf = new DataSourceConf ();
  private HttpClient httpClient;
  private final HttpClientSettings httpSettings = new HttpClientSettings ();

  @Value ("${ssl.context.enabled}")
  private boolean sslContextEnabled;

  @Value ("#{'${ssl.keystore.path:}'}")
  private String keystore;
  @Value ("#{'${ssl.keystore.password:}'}")
  private String keyStorePassword;
  @Value ("#{'${ssl.truststore.path:}'}")
  private String trustStore;
  @Value ("#{'${ssl.truststore.password:}'}")
  private String trustStorePassword;
  @Value ("#{'${ssl.keystore.type:}'}")
  private String type;

  @Value ("#{'${http.proxy.enabled:false}'}")
  private boolean proxyEnabled;
  @Value ("#{'${http.proxy.address:}'}")
  private String proxyHost;
  @Value ("#{'${http.proxy.port:0}'}")
  private int proxyPort;
  @Value ("#{'${http.proxyUsername:}'}")
  private String proxyUser;
  @Value ("#{'${http.proxyPassword:}'}")
  private String proxyPassword;
  @Value ("#{'${http.proxy.non-proxy:}'}")
  private String proxyNonHosts;

  @Value ("${de4a.kafka.enabled:false}")
  private boolean kafkaEnabled;
  @Value ("${de4a.kafka.logging.enabled:true}")
  private boolean kafkaLoggingEnabled;
  @Value ("${de4a.kafka.http.enabled:false}")
  private boolean kafkaHttp;
  @Value ("${de4a.kafka.url:#{null}}")
  private String kafkaUrl;
  @Value ("${de4a.kafka.topic:#{de4a-connector}}")
  private String kafkaTopic;

  @Bean
  public Docket api ()
  {
    final TypeResolver typeResolver = new TypeResolver ();
    return new Docket (DocumentationType.SWAGGER_2).select ()
                                                   .apis (RequestHandlerSelectors.basePackage ("eu"))
                                                   .paths (PathSelectors.any ())
                                                   .build ()
                                                   .additionalModels (typeResolver.resolve (RequestTransferEvidenceUSIIMDRType.class),
                                                                      typeResolver.resolve (ResponseTransferEvidenceType.class),
                                                                      typeResolver.resolve (ResponseErrorType.class),
                                                                      typeResolver.resolve (RequestForwardEvidenceType.class),
                                                                      typeResolver.resolve (RequestLookupRoutingInformationType.class),
                                                                      typeResolver.resolve (ResponseLookupRoutingInformationType.class),
                                                                      typeResolver.resolve (RequestTransferEvidenceUSIDTType.class))
                                                   .apiInfo (apiInfo ());
  }

  private ApiInfo apiInfo ()
  {
    return new ApiInfoBuilder ().title ("DE4A - Connector")
                                .description ("DE4A Connector component - eDelivery Exchange")
                                .version ("0.1.7")
                                .termsOfServiceUrl ("http://www.de4a.eu")
                                .licenseUrl ("https://www.apache.org/licenses/LICENSE-2.0")
                                .license ("APACHE2")
                                .build ();
  }

  @Override
  public void addResourceHandlers (final ResourceHandlerRegistry registry)
  {
    registry.addResourceHandler ("/swagger-ui/**")
            .addResourceLocations ("classpath:/META-INF/resources/webjars/springfox-swagger-ui/")
            .resourceChain (false);
  }

  @Bean (initMethod = "start", destroyMethod = "stop")
  @ConditionalOnProperty (prefix = "h2.console", name = "port")
  public org.h2.tools.Server h2WebConsonleServer (@Value ("${h2.console.port}") final String h2ConsolePort) throws SQLException
  {
    return org.h2.tools.Server.createWebServer ("-web", "-ifNotExists", "-webDaemon", "-webPort", h2ConsolePort);
  }

  @Bean
  public ISMLInfo smlConfig (@Value ("${sml.service.id}") final String id,
                             @Value ("${sml.displayname}") final String displayName,
                             @Value ("${sml.dnszone}") final String dnsZone,
                             @Value ("${sml.managementservice.endpoint}") final String managementService,
                             @Value ("${sml.certificate.required}") final boolean isCertificateRequired)
  {
    return new SMLInfo (id, displayName, dnsZone, managementService, isCertificateRequired);
  }

  @Bean
  public DomibusClientWS clienteWS ()
  {
    final DomibusClientWS cliente = new DomibusClientWS (messageFactory ());
    cliente.setMessageSender (httpComponentsMessageSender ());
    cliente.setMarshaller (marshallerDomibus ());
    cliente.setUnmarshaller (marshallerDomibus ());
    return cliente;
  }

  public AxiomSoapMessageFactory messageFactory ()
  {
    return new AxiomSoapMessageFactory ();
  }

  @Bean
  public HttpComponentsMessageSender httpComponentsMessageSender ()
  {
    final HttpComponentsMessageSender httpComponentsMessageSender = new HttpComponentsMessageSender ();
    try
    {
      httpComponentsMessageSender.setHttpClient (httpClient ());
    }
    catch (final Exception e)
    {
      LOG.error ("Error creating http sender", e);
    }
    return httpComponentsMessageSender;
  }

  @Bean
  public RestTemplate restTemplate ()
  {
    final HttpComponentsClientHttpRequestFactory httpComponentsClientHttpRequestFactory = new HttpComponentsClientHttpRequestFactory (httpClient ());
    return new RestTemplate (httpComponentsClientHttpRequestFactory);
  }

  public HttpClient httpClient ()
  {
    if (this.httpClient == null)
    {
      try
      {
        LOG.debug ("SSL context setted to: {}", sslContextEnabled);
        SSLConnectionSocketFactory factory;
        if (sslContextEnabled)
        {
          final SSLContext sslContext = sslContext ();
          factory = new SSLConnectionSocketFactory (sslContext,
                                                    new String [] { "TLSv1.2", "TLSv1.3" },
                                                    null,
                                                    SSLConnectionSocketFactory.getDefaultHostnameVerifier ());
          httpSettings.setSSLContext (sslContext);
        }
        else
        {
          factory = new SSLConnectionSocketFactory (sslContextTrustAll ());
          httpSettings.setSSLContextTrustAll ();
        }
        this.httpClient = HttpClientBuilder.create ()
                                           .setSSLSocketFactory (factory)
                                           .setRoutePlanner (buildRoutePlanner ())
                                           .build ();
      }
      catch (final Exception e)
      {
        LOG.error ("Unable to create SSL factory", e);
      }
    }
    kafkaSettings ();
    return this.httpClient;
  }

  private HttpRoutePlanner buildRoutePlanner ()
  {
    if (!proxyEnabled)
      return null;
    final HttpHost proxy = new HttpHost (proxyHost, proxyPort);
    this.httpSettings.setProxyHost (proxy);
    return new DefaultProxyRoutePlanner (proxy)
    {
      @Override
      public HttpHost determineProxy (final HttpHost target,
                                      final HttpRequest request,
                                      final HttpContext context) throws HttpException
      {
        if (skipProxy (target.getHostName ()))
        {
          return null;
        }
        return super.determineProxy (target, request, context);
      }

      private boolean skipProxy (final String host)
      {
        if (proxyHost.isEmpty ())
          return false;
        final StringTokenizer st = new StringTokenizer (proxyNonHosts, "|");
        while (st.hasMoreTokens ())
        {
          String pattern = st.nextToken ();
          pattern = pattern.replace ("\\*", "");
          if (host.contains (pattern))
            return true;
        }
        return false;
      }
    };
  }

  public SSLContext sslContextTrustAll ()
  {
    final TrustStrategy acceptingTrustStrategy = (final X509Certificate [] chain, final String authType) -> true;
    try
    {
      return SSLContexts.custom ().loadTrustMaterial (null, acceptingTrustStrategy).build ();
    }
    catch (final NoSuchAlgorithmException | KeyStoreException | KeyManagementException e)
    {
      LOG.error ("There was a problem creating sslContextTrustAll", e);
      throw new IllegalStateException ("There was a problem creating sslContextTrustAll", e);
    }
  }

  public SSLContext sslContext ()
  {
    if (keystore == null ||
        keyStorePassword == null ||
        trustStore == null ||
        trustStorePassword == null ||
        type == null)
    {
      LOG.error ("SSL connection will not stablished, some parameters are not setted");
      throw new IllegalStateException ("SSL connection will not stablished, some parameters are not setted");
    }
    try (FileInputStream fis = new FileInputStream (new File (keystore)))
    {
      final KeyStore keyStore = KeyStore.getInstance (type.toUpperCase ());
      keyStore.load (fis, keyStorePassword.toCharArray ());

      return SSLContextBuilder.create ()
                              .loadKeyMaterial (keyStore, keyStorePassword.toCharArray ())
                              .loadTrustMaterial (new File (trustStore), trustStorePassword.toCharArray ())
                              .build ();
    }
    catch (IOException | NoSuchAlgorithmException | CertificateException | KeyStoreException | KeyManagementException
        | UnrecoverableKeyException e)
    {
      LOG.error ("There was a problem creating sslContext", e);
      throw new IllegalStateException ("There was a problem creating sslContext", e);
    }
  }

  @Bean
  public Jaxb2Marshaller marshallerDomibus ()
  {
    final Jaxb2Marshaller marshaller = new Jaxb2Marshaller ();
    marshaller.setContextPath ("eu.de4a.connector.as4.domibus.soap.auto");
    return marshaller;
  }

  @Override
  public void addViewControllers (final ViewControllerRegistry registry)
  {
    registry.addViewController ("/").setViewName ("index");
    registry.addViewController ("/swagger-ui/").setViewName ("redirect:/swagger-ui/index.html");
  }

  @Bean
  public ViewResolver viewResolver ()
  {
    final InternalResourceViewResolver bean = new InternalResourceViewResolver ();

    bean.setViewClass (JstlView.class);
    bean.setPrefix ("/WEB-INF/view/");
    bean.setSuffix (".jsp");

    return bean;
  }

  @Bean (name = "localeResolver")
  public LocaleResolver localeResolver (@Value ("${spring.messages.default_locale:#{null}}") final String locale)
  {
    final SessionLocaleResolver slr = new SessionLocaleResolver ();
    if (locale != null && !locale.trim ().isEmpty ())
      slr.setDefaultLocale (new Locale (locale));
    else
      slr.setDefaultLocale (Locale.ENGLISH);
    return slr;
  }

  @Bean
  CharacterEncodingFilter characterEncodingFilter ()
  {
    final CharacterEncodingFilter filter = new CharacterEncodingFilter ();
    filter.setEncoding (StandardCharsets.UTF_8.name ());
    filter.setForceEncoding (true);
    return filter;
  }

  @Bean
  public MessageSource messageSource ()
  {
    final ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource ();
    messageSource.setBasename ("classpath:messages/messages");
    messageSource.setDefaultEncoding (StandardCharsets.UTF_8.name ());
    return messageSource;
  }

  public void kafkaSettings ()
  {
    DE4AKafkaSettings.defaultProperties ().put ("bootstrap.servers", kafkaUrl);
    DE4AKafkaSettings.setKafkaEnabled (kafkaEnabled);
    DE4AKafkaSettings.setKafkaHttp (kafkaHttp);
    if (kafkaHttp)
    {
      DE4AKafkaSettings.setHttpClientSetting (this.httpSettings);
    }
    DE4AKafkaSettings.setLoggingEnabled (kafkaLoggingEnabled);
    DE4AKafkaSettings.setKafkaTopic (kafkaTopic);
  }

  @Bean
  @Order (0)
  public MultipartFilter multipartFilter ()
  {
    final MultipartFilter multipartFilter = new MultipartFilter ();
    multipartFilter.setMultipartResolverBeanName ("multipartResolver");
    return multipartFilter;
  }

  @Bean (name = "multipartResolver")
  public CommonsMultipartResolver createMultipartResolver ()
  {
    final CommonsMultipartResolver resolver = new CommonsMultipartResolver ();
    resolver.setDefaultEncoding (StandardCharsets.UTF_8.name ());
    return resolver;
  }

  @Bean (name = "applicationEventMulticaster")
  public ApplicationEventMulticaster simpleApplicationEventMulticaster ()
  {
    final SimpleApplicationEventMulticaster eventMulticaster = new SimpleApplicationEventMulticaster ();

    eventMulticaster.setTaskExecutor (new SimpleAsyncTaskExecutor ());
    return eventMulticaster;
  }

  @Bean (destroyMethod = "close")
  public DataSource dataSource ()
  {
    final HikariConfig dataSourceConfig = new HikariConfig ();
    dataSourceConfig.setDriverClassName (dataSourceConf.getDriverClassName ());
    dataSourceConfig.setJdbcUrl (dataSourceConf.getUrl ());
    dataSourceConfig.setUsername (dataSourceConf.getUsername ());
    dataSourceConfig.setPassword (dataSourceConf.getPassword ());

    try
    {
      return new HikariDataSource (dataSourceConfig);
    }
    catch (final Exception e)
    {
      e.printStackTrace ();
      LOG.error ("Fatallity!...error datasource", e);
      return null;
    }
  }

  @Bean
  public LocalContainerEntityManagerFactoryBean entityManagerFactory (final DataSource dataSource)
  {
    final LocalContainerEntityManagerFactoryBean entityManagerFactoryBean = new LocalContainerEntityManagerFactoryBean ();
    entityManagerFactoryBean.setDataSource (dataSource);
    entityManagerFactoryBean.setJpaVendorAdapter (new HibernateJpaVendorAdapter ());
    entityManagerFactoryBean.setPackagesToScan ("eu");

    final Properties jpaProperties = new Properties ();

    // Configures the used database dialect. This allows Hibernate to create SQL
    // that is optimized for the used database.
    jpaProperties.put ("hibernate.dialect", dataSourceConf.getJpaHibernate ().getDialectPlatform ());

    // Specifies the action that is invoked to the database when the Hibernate
    // SessionFactory is created or closed.
    jpaProperties.put ("hibernate.hbm2ddl.auto", dataSourceConf.getJpaHibernate ().getDdlAuto ());

    // Configures the naming strategy that is used when Hibernate creates
    // new database objects and schema elements
    jpaProperties.put ("hibernate.ejb.naming_strategy", dataSourceConf.getJpaHibernate ().getNamingStrategy ());

    // If the value of this property is true, Hibernate writes all SQL
    // statements to the console.
    jpaProperties.put ("hibernate.show_sql", dataSourceConf.getJpaHibernate ().getShowSql ());

    // If the value of this property is true, Hibernate will format the SQL
    // that is written to the console.
    jpaProperties.put ("hibernate.format_sql", dataSourceConf.getJpaHibernate ().getFormatSql ());

    entityManagerFactoryBean.setJpaProperties (jpaProperties);

    return entityManagerFactoryBean;
  }

  @Bean
  JpaTransactionManager transactionManager (final EntityManagerFactory entityManagerFactory)
  {
    final JpaTransactionManager transactionManager = new JpaTransactionManager ();
    transactionManager.setEntityManagerFactory (entityManagerFactory);
    return transactionManager;
  }

  public DataSourceConf getDataSourceConf ()
  {
    return dataSourceConf;
  }

  public void setDataSourceConf (final DataSourceConf dataSourceConf)
  {
    this.dataSourceConf = dataSourceConf;
  }

}
