package config.web;

import config.environment.Environment;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.servlet.config.annotation.CorsRegistry;

/**
 * @author ngnmhieu
 * @since 04.05.16
 */
@Configuration
@Profile(Environment.PRODUCTION)
public class ProdWebConfig extends WebConfig
{
}
