package app.config;

import app.web.authentication.JwtAuthenticator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;

import javax.sql.DataSource;

/**
 * Created by DucNguyenMinh on 29.02.16.
 */
@Configuration
public class ApplicationBeans
{
    @Bean
    public JwtAuthenticator jwtAuthenticator(@Value("${auth.secret}") String authSecret)
    {
        return new JwtAuthenticator(authSecret);
    }
}
