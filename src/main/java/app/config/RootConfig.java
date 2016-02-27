package app.config;

import app.config.environment.DevelopmentProfile;
import app.config.environment.EnvironmentConfig;
import app.config.environment.TestProfile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.*;
import org.springframework.orm.jpa.JpaVendorAdapter;
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
@Import({EnvironmentConfig.class, TransactionManagersConfig.class})
public class RootConfig
{
    @Bean
    @Autowired
    public LocalContainerEntityManagerFactoryBean localContainerEntityManagerFactoryBean(DataSource dataSource, JpaVendorAdapter jpaVendorAdapter)
    {
        LocalContainerEntityManagerFactoryBean emfb = new LocalContainerEntityManagerFactoryBean();
        emfb.setDataSource(dataSource);
        emfb.setJpaVendorAdapter(jpaVendorAdapter);
        emfb.setPackagesToScan(new String[] { "app.data" });
        return emfb;
    }
}
