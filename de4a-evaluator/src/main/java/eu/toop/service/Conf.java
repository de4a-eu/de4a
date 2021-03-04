package eu.toop.service;

import java.sql.SQLException;
import java.util.Properties;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.springframework.web.servlet.view.JstlView;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import eu.de4a.config.DataSourceConf;
import eu.de4a.repository.CustomRepositoryImpl;


@Configuration
@EnableJpaRepositories(entityManagerFactoryRef = "entityManagerFactory", value = "eu")
@PropertySource("classpath:application.properties")
@ComponentScan("eu")
@ConfigurationProperties(prefix = "database")
public class Conf implements WebMvcConfigurer {

	private DataSourceConf dataSourceConf = new DataSourceConf();
	
	@Value("#{'${h2.console.port.jvm:${h2.console.port:'21080'}}'}")
	private String h2ConsolePort;

	
	@Bean(initMethod = "start", destroyMethod = "stop")
	public org.h2.tools.Server h2WebConsonleServer() throws SQLException {
		return org.h2.tools.Server.createWebServer("-web", "-webAllowOthers", "-webDaemon", 
				"-ifNotExists", "-webPort", h2ConsolePort);
	}
	
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

	@Bean(name = "multipartResolver")
	public CommonsMultipartResolver createMultipartResolver() {
		CommonsMultipartResolver resolver = new CommonsMultipartResolver();
		resolver.setDefaultEncoding("UTF-8");
		return resolver;
	}

	/*
	 * @Bean(name = "applicationEventMulticaster") public
	 * ApplicationEventMulticaster simpleApplicationEventMulticaster() {
	 * SimpleApplicationEventMulticaster eventMulticaster = new
	 * SimpleApplicationEventMulticaster();
	 * 
	 * eventMulticaster.setTaskExecutor(new SimpleAsyncTaskExecutor()); return
	 * eventMulticaster; }
	 */

	@Bean(destroyMethod = "close")
	public DataSource dataSource() {
		HikariConfig dataSourceConfig = new HikariConfig();
		dataSourceConfig.setDriverClassName(dataSourceConf.getDriverClassName());
		dataSourceConfig.setJdbcUrl(dataSourceConf.getUrl());
		dataSourceConfig.setUsername(dataSourceConf.getUsername());
		dataSourceConfig.setPassword(dataSourceConf.getPassword());
		return new HikariDataSource(dataSourceConfig);
	}

	@Bean
	public LocalContainerEntityManagerFactoryBean entityManagerFactory(DataSource dataSource) {
		LocalContainerEntityManagerFactoryBean entityManagerFactoryBean = new LocalContainerEntityManagerFactoryBean();
		entityManagerFactoryBean.setDataSource((javax.sql.DataSource) dataSource);
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
