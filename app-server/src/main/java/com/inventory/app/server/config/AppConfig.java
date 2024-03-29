package com.inventory.app.server.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.*;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.Properties;

@Configuration
@EnableTransactionManagement
@PropertySource("classpath:application.properties")
public class AppConfig {
    @Value("${spring.jpa.generate-ddl}")
    private String ddlAuto;

    @Value("${spring.jpa.database-platform}")
    private String hibernateDialect;
    @Value("${spring.datasource.url}")
    private String datasourceUrl;
    @Value("${spring.datasource.driverclassname}")
    private String datasourceDriverClassName;
    @Value("${spring.datasource.username}")
    private String username;
    @Value("${spring.datasource.password}")
    private String password;
    @Value("${spring.jpa.show-sql}")
    private String showSql;


    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
        final LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(dataSource());
        em.setPackagesToScan("com.inventory.app.server.entity");
        Properties jpaProperties = getAdditionalProperties();
        em.setJpaProperties(jpaProperties);

        final HibernateJpaVendorAdapter hibernateJpaVendorAdapter = new HibernateJpaVendorAdapter();
        hibernateJpaVendorAdapter.setDatabasePlatform(getHibernateDialect());
        hibernateJpaVendorAdapter.setShowSql(getShowSql());
        em.setJpaVendorAdapter(hibernateJpaVendorAdapter);

        return em;
    }

    @Bean
    public DataSource dataSource() {
        final String datasourceDriverClassName = getDatasourceDriverClassName();
        final String dataSourceUrl = getDatasourceUrl();
        final String username = getUsername();
        final String password = getPassword();

        if (datasourceDriverClassName == null || dataSourceUrl == null || username == null || password == null) {
            throw new IllegalArgumentException("Database config properties cannot be null.");
        }
        final DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(datasourceDriverClassName);
        dataSource.setUrl(dataSourceUrl);
        dataSource.setUsername(username);
        dataSource.setPassword(password);
        return dataSource;
    }

    @Bean
    public PlatformTransactionManager transactionManager() {
        final JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(entityManagerFactory().getObject());
        return transactionManager;
    }

    @Bean
    public PersistenceExceptionTranslationPostProcessor exceptionTranslation() {
        return new PersistenceExceptionTranslationPostProcessor();
    }

    final Properties getAdditionalProperties() {
        final Properties jpaProperties = new Properties();
        final String ddlAuto = getDdlAuto();

        if (ddlAuto == null) {
            throw new IllegalArgumentException("Additional database config properties cannot be null.");
        }
       jpaProperties.setProperty("spring.jpa.hibernate.ddl-auto", ddlAuto);
        return jpaProperties;
    }

    public String getDdlAuto() {
        return ddlAuto;
    }

    public String getHibernateDialect() {
        return hibernateDialect;
    }

    public String getDatasourceUrl() {
        return datasourceUrl;
    }

    public String getDatasourceDriverClassName() {
        return datasourceDriverClassName;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public boolean getShowSql() {
        return Boolean.parseBoolean(showSql);
    }
}
