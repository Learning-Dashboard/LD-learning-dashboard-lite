package com.upc.gessi.qrapids.app.config;

import org.junit.Before;
import org.junit.Test;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.test.util.ReflectionTestUtils;

import javax.sql.DataSource;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class AppConfigTest {

    private AppConfig appConfig;

    @Before
    public void setUp() {
        appConfig = new AppConfig();
        ReflectionTestUtils.setField(appConfig, "url", "jdbc:h2:mem:testdb");
        ReflectionTestUtils.setField(appConfig, "driverClass", "org.h2.Driver");
        ReflectionTestUtils.setField(appConfig, "username", "sa");
        ReflectionTestUtils.setField(appConfig, "password", "");
        ReflectionTestUtils.setField(appConfig, "dialect", "org.hibernate.dialect.H2Dialect");
    }

    @Test
    public void dataSourceUsesConfiguredConnectionProperties() {
        DataSource dataSource = appConfig.dataSource();

        assertTrue(dataSource instanceof DriverManagerDataSource);
        DriverManagerDataSource driverManagerDataSource = (DriverManagerDataSource) dataSource;
        assertEquals("jdbc:h2:mem:testdb", driverManagerDataSource.getUrl());
        assertEquals("sa", driverManagerDataSource.getUsername());
        assertEquals("", driverManagerDataSource.getPassword());
    }

    @Test
    public void jpaVendorAdapterDisablesSqlLoggingByDefault() {
        ReflectionTestUtils.setField(appConfig, "showSql", false);

        JpaVendorAdapter vendorAdapter = appConfig.jpaVendorAdapter();

        assertTrue(vendorAdapter instanceof HibernateJpaVendorAdapter);
        Map<String, Object> properties = ((HibernateJpaVendorAdapter) vendorAdapter).getJpaPropertyMap();
        assertEquals("org.hibernate.dialect.H2Dialect", properties.get("hibernate.dialect"));
        assertEquals("update", properties.get("hibernate.hbm2ddl.auto"));
        assertFalse(properties.containsKey("hibernate.show_sql"));
    }

    @Test
    public void entityManagerFactoryEnablesSqlLoggingWhenConfigured() {
        ReflectionTestUtils.setField(appConfig, "showSql", true);

        LocalContainerEntityManagerFactoryBean entityManagerFactory = appConfig.entityManagerFactory();

        assertTrue(entityManagerFactory.getDataSource() instanceof DriverManagerDataSource);
        DriverManagerDataSource dataSource = (DriverManagerDataSource) entityManagerFactory.getDataSource();
        assertEquals("jdbc:h2:mem:testdb", dataSource.getUrl());
        assertEquals("true", entityManagerFactory.getJpaPropertyMap().get("hibernate.show_sql"));
        assertEquals("100", entityManagerFactory.getJpaPropertyMap().get("hibernate.jdbc.fetch_size"));
        assertEquals("update", entityManagerFactory.getJpaPropertyMap().get("hibernate.hbm2ddl.auto"));
    }
}
