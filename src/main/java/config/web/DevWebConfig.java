package config.web;

import config.environment.Profiles;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;

/**
 * @author ngnmhieu
 * @since 04.05.16
 */
@Configuration
@org.springframework.context.annotation.Profile(Profiles.DEVELOPMENT)
public class DevWebConfig extends BaseWebConfig
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
