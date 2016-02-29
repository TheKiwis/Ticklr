package app.config;

import app.web.authenticator.JwtAuthenticator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by DucNguyenMinh on 29.02.16.
 */
@Configuration
public class BeansConfig
{
    @Bean
    public JwtAuthenticator jwtAuthenticator(@Value("${auth.secret}") String authSecret)
    {
        return new JwtAuthenticator(authSecret);
    }
}
