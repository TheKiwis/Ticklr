package config.web;

import config.environment.Profiles;
import config.web.interceptors.CorsInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
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
        registry.addInterceptor(CorsInterceptor.get()
                .withOrigin("*")
                .withMethods("POST, PUT, GET, OPTIONS, DELETE")
                .withMaxAge("3600")
                .withExposeHeaders("Content-Type, x-requested-with, X-Custom-Header, Location")
                .withAllowHeaders("Origin, Accept, X-Requested-With, Content-Type, Access-Control-Request-Method, Access-Control-Request-Headers, Authorization"));

    }
}
