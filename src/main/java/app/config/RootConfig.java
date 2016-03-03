package app.config;

import app.config.environment.EnvironmentConfig;
import app.config.security.SecurityConfig;
import app.config.persistence.PersistenceConfig;
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
@Import({EnvironmentConfig.class, PersistenceConfig.class, SecurityConfig.class, ApplicationBeans.class})
public class RootConfig
{
}
