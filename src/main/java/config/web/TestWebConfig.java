package config.web;

import config.environment.Profile;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

/**
 * @author ngnmhieu
 * @since 04.05.16
 */
@Configuration
@org.springframework.context.annotation.Profile(Profile.TEST)
public class TestWebConfig extends BaseWebConfig
{
    /**
     * This propertyPlaceholderConfigurer only resolves values inside servlet-container
     * other values that are resolved in root-container are ignored by this
     */
    @Bean
    public PropertyPlaceholderConfigurer servletPropertyPlaceholderConfigurer()
    {
        Properties properties = new Properties();
        properties.setProperty("app.server.host", "http://integration.localhost");

        PropertyPlaceholderConfigurer placeholderConfigurer = new PropertyPlaceholderConfigurer();
        placeholderConfigurer.setProperties(properties);
        placeholderConfigurer.setIgnoreUnresolvablePlaceholders(true);

        return placeholderConfigurer;
    }
}
