package app.config.environment;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;

import javax.sql.DataSource;

/**
 * @author ngnmhieu
 */
@Configuration
@Profile("dev")
public class DevelopmentProfile
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
        jpaVendorAdapter.setShowSql(true);;
        jpaVendorAdapter.setDatabase(Database.MYSQL);
        jpaVendorAdapter.setGenerateDdl(false);
        return jpaVendorAdapter;
    }

}
