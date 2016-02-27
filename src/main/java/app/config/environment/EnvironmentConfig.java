package app.config.environment;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * @author ngnmhieu
 */
@Configuration
@Import({DevelopmentProfile.class, TestProfile.class})
public class EnvironmentConfig
{
}
