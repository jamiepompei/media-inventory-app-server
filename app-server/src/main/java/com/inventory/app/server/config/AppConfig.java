package com.inventory.app.server.config;

import com.google.common.base.Preconditions;
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
        final DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(Preconditions.checkNotNull(getDatasourceDriverClassName()));
        dataSource.setUrl(Preconditions.checkNotNull(getDatasourceUrl()));
        dataSource.setUsername(Preconditions.checkNotNull(getUsername()));
        dataSource.setPassword(Preconditions.checkNotNull(getPassword()));

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
       jpaProperties.setProperty("spring.jpa.hibernate.ddl-auto", getDdlAuto());

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
