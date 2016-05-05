package config.environment.production;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;
import config.environment.Environment;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;

import javax.sql.DataSource;
import java.util.Properties;

/**
 * @author ngnmhieu
 */
@Profile(Environment.PRODUCTION)
public class ProductionEnvironment
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

    @Bean
    public PropertyPlaceholderConfigurer propertyPlaceholderConfigurer()
    {
        Properties properties = new Properties();
        properties.setProperty("auth.secret", "dev_secret");

        PropertyPlaceholderConfigurer placeholderConfigurer = new PropertyPlaceholderConfigurer();
        placeholderConfigurer.setProperties(properties);

        return placeholderConfigurer;
    }
}
