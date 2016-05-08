package config.web;

import config.environment.Environment;
import config.web.WebConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.servlet.config.annotation.CorsRegistry;

/**
 * @author ngnmhieu
 * @since 04.05.16
 */
@Configuration
@Profile(Environment.DEVELOPMENT)
public class DevWebConfig extends WebConfig
{
    public void addCorsMappings(CorsRegistry registry)
    {
        registry.addMapping("/**")
                .allowedOrigins("*")
                .allowedMethods("*")
                .allowedHeaders("*")
                .exposedHeaders("Location");
    }
}
