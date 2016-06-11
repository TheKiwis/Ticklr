package config.web;

import config.environment.Profiles;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author ngnmhieu
 * @since 04.05.16
 */
@Configuration
@org.springframework.context.annotation.Profile(Profiles.PRODUCTION)
public class ProdWebConfig extends BaseWebConfig
{
    /**
     * This propertyPlaceholderConfigurer only resolves values inside servlet-container
     * other values that are resolved in root-container are ignored by this
     * TODO: custom propertyPlaceholderConfigure for PRODUCTION
     */
    @Bean
    public PropertyPlaceholderConfigurer servletPropertyPlaceholderConfigurer()
    {
        return null;
    }
}
