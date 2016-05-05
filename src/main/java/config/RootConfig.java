package config;

import config.environment.EnvironmentConfig;
import config.security.SecurityConfig;
import config.persistence.PersistenceConfig;
import org.springframework.context.annotation.*;


/**
 * @author ngnmhieu
 */
@Configuration
@ComponentScan(basePackages = "app")
@Import({EnvironmentConfig.class, PersistenceConfig.class, SecurityConfig.class, ApplicationBeans.class})
public class RootConfig
{
}
