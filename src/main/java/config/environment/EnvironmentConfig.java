package config.environment;

import config.environment.env.Development;
import config.environment.env.Integration;
import config.environment.env.Production;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * @author ngnmhieu
 */
@Configuration
@Import({Development.class, Integration.class, Production.class})
public class EnvironmentConfig
{
}
