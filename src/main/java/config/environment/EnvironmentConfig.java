package config.environment;

import config.environment.env.Development;
import config.environment.env.Test;
import config.environment.env.Production;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * @author ngnmhieu
 */
@Configuration
@Import({Development.class, Test.class, Production.class})
public class EnvironmentConfig
{
}
