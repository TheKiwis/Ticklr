package app.config;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import javax.sql.DataSource;

/**
 * @author ngnmhieu
 */
@Configuration
@ComponentScan(
        basePackages = "app",
        excludeFilters = {@ComponentScan.Filter(type = FilterType.ANNOTATION, value = EnableWebMvc.class)} // exclude the WebConfig
)
public class RootConfig
{
    @Bean
    public LocalContainerEntityManagerFactoryBean localContainerEntityManagerFactoryBean()
    {
        LocalContainerEntityManagerFactoryBean emfb = new LocalContainerEntityManagerFactoryBean();
        //emfb.setDataSource();
        //emfb.setJpaVendorAdapter(null);
        emfb.setPackagesToScan(new String[] { "app.data" });
        return emfb;
    }

    @Bean(destroyMethod = "")
    public DataSource dataSource()
    {
        MysqlDataSource ds = new MysqlDataSource();
        //ds.setUrl("");
        //ds.setUser("");
        //ds.setPassword("");
        return ds;
    }
}
