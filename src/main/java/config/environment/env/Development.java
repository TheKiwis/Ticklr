package config.environment.env;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;
import config.environment.Profile;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;

import javax.sql.DataSource;
import java.util.Properties;

/**
 * @author ngnmhieu
 */
@org.springframework.context.annotation.Profile(Profile.DEVELOPMENT)
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
     * This propertyPlaceholderConfigurer only resolves values inside root-container
     * other values that are resolved in servlet-container are ignored by this
     */
    @Bean
    public PropertyPlaceholderConfigurer propertyPlaceholderConfigurer()
    {
        Properties properties = new Properties();
        properties.setProperty("app.auth.secret", "dev_secret");

        PropertyPlaceholderConfigurer placeholderConfigurer = new PropertyPlaceholderConfigurer();
        placeholderConfigurer.setProperties(properties);
        placeholderConfigurer.setIgnoreUnresolvablePlaceholders(true);

        return placeholderConfigurer;
    }
}
