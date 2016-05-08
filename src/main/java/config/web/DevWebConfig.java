package config.web;

import config.environment.Profile;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;

import java.util.Properties;

/**
 * @author ngnmhieu
 * @since 04.05.16
 */
@Configuration
@org.springframework.context.annotation.Profile(Profile.DEVELOPMENT)
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

    /**
     * This propertyPlaceholderConfigurer only resolves values inside servlet-container
     * other values that are resolved in root-container are ignored by this
     */
    @Bean
    public PropertyPlaceholderConfigurer servletPropertyPlaceholderConfigurer()
    {
        Properties properties = new Properties();
        properties.setProperty("app.server.host", "http://localhost:8080");

        PropertyPlaceholderConfigurer placeholderConfigurer = new PropertyPlaceholderConfigurer();
        placeholderConfigurer.setProperties(properties);
        placeholderConfigurer.setIgnoreUnresolvablePlaceholders(true);

        return placeholderConfigurer;
    }
}
