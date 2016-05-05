package config.environment.test;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;
import config.environment.Environment;
import org.flywaydb.core.Flyway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.context.annotation.*;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;

import javax.sql.DataSource;
import java.util.Properties;

/**
 * @author ngnmhieu
 */
@Profile(Environment.TEST)
public class TestEnvironment
{
    /**
     * @return local data source or travis-ci data source
     *         depending on if the environment variable "travis_ci"
     *         is set or not.
     */
    @Bean
    public DataSource dataSource()
    {
        return System.getProperty("travis_ci") == null ? localDataSource() : travisCIDataSource();
    }

    public DataSource localDataSource()
    {
        MysqlDataSource ds = new MysqlDataSource();
        ds.setUrl("jdbc:mysql://localhost:3306/ticklr_test");
        ds.setUser("root");
        ds.setPassword("123456789");
        return ds;
    }

    public DataSource travisCIDataSource()
    {
        MysqlDataSource ds = new MysqlDataSource();
        ds.setUrl("jdbc:mysql://localhost:3306/ticklr_test");
        ds.setUser("root");
        ds.setPassword("");
        return ds;
    }

    @Bean
    public JpaVendorAdapter jpaVendorAdapter()
    {
        HibernateJpaVendorAdapter jpaVendorAdapter = new HibernateJpaVendorAdapter();
        jpaVendorAdapter.setShowSql(true);
        jpaVendorAdapter.setDatabase(Database.MYSQL);
        jpaVendorAdapter.setGenerateDdl(false);
        return jpaVendorAdapter;
    }

    @Bean
    @Autowired
    public Flyway flyway(DataSource dataSource)
    {
        Flyway flyway = new Flyway();
        flyway.setDataSource(dataSource);
        flyway.setLocations("filesystem:./migrations");

        return flyway;
    }

    @Bean
    public PropertyPlaceholderConfigurer propertyPlaceholderConfigurer()
    {
        Properties properties = new Properties();
        properties.setProperty("auth.secret", "test_secret");

        PropertyPlaceholderConfigurer placeholderConfigurer = new PropertyPlaceholderConfigurer();
        placeholderConfigurer.setProperties(properties);

        return placeholderConfigurer;
    }
}
