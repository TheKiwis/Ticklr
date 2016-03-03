package app.config.environment;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;
import org.flywaydb.core.Flyway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import javax.sql.DataSource;
import java.util.Properties;

/**
 * @author ngnmhieu
 */
@Configuration
@Profile("test")
public class TestEnvironment
{
    @Bean
    public DataSource dataSource()
    {
        MysqlDataSource ds = new MysqlDataSource();
        ds.setUrl("jdbc:mysql://localhost:3306/ticklr_test");
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
