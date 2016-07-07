package config.web;

import config.environment.Profiles;
import config.web.interceptors.CorsInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;

/**
 * @author ngnmhieu
 * @since 04.05.16
 */
@Configuration
@Profile(Profiles.DEVELOPMENT)
public class DevWebConfig extends BaseWebConfig
{
    @Override
    public void addInterceptors(InterceptorRegistry registry)
    {
        registry.addInterceptor(new CorsInterceptor());
    }
}
