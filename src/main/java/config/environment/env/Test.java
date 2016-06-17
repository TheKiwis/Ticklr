package config.environment.env;

import app.services.checkout.PaypalConfiguration;
import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;
import com.paypal.base.Constants;
import config.environment.Profiles;
import org.flywaydb.core.Flyway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.io.ClassPathResource;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;

import javax.sql.DataSource;

/**
 * @author ngnmhieu
 */
@org.springframework.context.annotation.Profile(Profiles.TEST)
public class Test
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

    /**
     * This PropertySourcesPlaceholderConfigurer object only resolves values inside
     * root-container other values that are resolved in servlet-container are ignored
     */
    @Bean
    public PropertySourcesPlaceholderConfigurer rootPropertySourcesPlaceholderConfigurer()
    {
        PropertySourcesPlaceholderConfigurer placeholderConfigurer = new PropertySourcesPlaceholderConfigurer();

        placeholderConfigurer.setLocation(new ClassPathResource("META-INF/config.properties"));
        placeholderConfigurer.setIgnoreUnresolvablePlaceholders(true);

        return placeholderConfigurer;
    }

    @Bean
    public PaypalConfiguration paypalConfiguration(@Value("${app.payment.paypal.clientID}") String clientID,
                                                   @Value("${app.payment.paypal.clientSecret}") String clientSecret)
    {
        PaypalConfiguration config = new PaypalConfiguration(clientID, clientSecret);
        config.put(Constants.MODE, Constants.SANDBOX);
        config.put(Constants.ENDPOINT, "http://localhost:1080");
        return config;
    }
}
