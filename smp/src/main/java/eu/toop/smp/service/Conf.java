package eu.toop.smp.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

@Configuration 
@EnableJpaRepositories(entityManagerFactoryRef = "entityManagerFactory", value = "eu") 
@EnableTransactionManagement
@EnableWebMvc
public class Conf  {
	
	 
	 @Bean(destroyMethod = "close")
	public DataSource dataSource() {
	    HikariConfig dataSourceConfig = new HikariConfig();
	    dataSourceConfig.setDriverClassName("org.h2.Driver");
	    dataSourceConfig.setJdbcUrl("jdbc:h2:mem:datajpa");
	    dataSourceConfig.setUsername("sa");
	    dataSourceConfig.setPassword("");

	    return (DataSource) new HikariDataSource(dataSourceConfig);
    }

    @Bean
    public   LocalContainerEntityManagerFactoryBean entityManagerFactory(DataSource dataSource) {
	    LocalContainerEntityManagerFactoryBean entityManagerFactoryBean = new LocalContainerEntityManagerFactoryBean();
	    entityManagerFactoryBean.setDataSource((javax.sql.DataSource) dataSource);
	    entityManagerFactoryBean.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
	    entityManagerFactoryBean.setPackagesToScan("eu");

	    Properties jpaProperties = new Properties();

	    //Configures the used database dialect. This allows Hibernate to create SQL
	    //that is optimized for the used database.
	    jpaProperties.put("hibernate.dialect", "org.hibernate.dialect.H2Dialect");

	    //Specifies the action that is invoked to the database when the Hibernate
	    //SessionFactory is created or closed.
	    jpaProperties.put("hibernate.hbm2ddl.auto", 
	            "create-drop"
	    );

	    //Configures the naming strategy that is used when Hibernate creates
	    //new database objects and schema elements
	    jpaProperties.put("hibernate.ejb.naming_strategy", 
	            "org.hibernate.cfg.ImprovedNamingStrategy"
	    );

	    //If the value of this property is true, Hibernate writes all SQL
	    //statements to the console.
	    jpaProperties.put("hibernate.show_sql", 
	            "true"
	    );

	    //If the value of this property is true, Hibernate will format the SQL
	    //that is written to the console.
	    jpaProperties.put("hibernate.format_sql", 
	            "true"
	    );

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
