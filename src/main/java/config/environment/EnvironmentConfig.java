package config.environment;

import config.environment.development.DevelopmentEnvironment;
import config.environment.production.ProductionEnvironment;
import config.environment.test.TestEnvironment;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * @author ngnmhieu
 */
@Configuration
@Import({DevelopmentEnvironment.class, TestEnvironment.class, ProductionEnvironment.class})
public class EnvironmentConfig
{
}
