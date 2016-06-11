package config.environment.env;

import app.services.checkout.PaypalConfiguration;
import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;
import com.paypal.base.Constants;
import config.environment.Profiles;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.io.ClassPathResource;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;

import javax.sql.DataSource;

/**
 * @author ngnmhieu
 */
@org.springframework.context.annotation.Profile(Profiles.DEVELOPMENT)
public class Development
{
    @Bean
    public DataSource dataSource()
    {
        MysqlDataSource ds = new MysqlDataSource();
        ds.setUrl("jdbc:mysql://localhost:3306/ticklr");
        ds.setUser("root");
        ds.setPassword("123456789");
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
        return config;
    }
}
