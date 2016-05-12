package config.web;

import config.environment.Profile;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.web.context.support.ServletContextResource;

import java.io.FileInputStream;
import java.io.InputStream;
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
     * This PropertySourcesPlaceholderConfigurer object only resolves values inside
     * servlet-container other values that are resolved in root-container are ignored
     */
    @Bean
    public PropertySourcesPlaceholderConfigurer servletPropertySourcesPlaceholderConfigurer()
    {
        PropertySourcesPlaceholderConfigurer placeholderConfigurer = new PropertySourcesPlaceholderConfigurer();

        placeholderConfigurer.setLocation(new ClassPathResource("META-INF/config.properties"));
        placeholderConfigurer.setIgnoreUnresolvablePlaceholders(true);

        return placeholderConfigurer;
    }
}
