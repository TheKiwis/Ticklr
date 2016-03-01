package app.config.environment;

import app.data.UserRepository;
import app.web.authentication.JwtAuthententicationProvider;
import app.web.authentication.JwtAuthenticationFilter;
import app.web.authentication.JwtAuthenticator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.Http403ForbiddenEntryPoint;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

/**
 * @author ngnmhieu
 */
@Configuration
@EnableWebSecurity
@ComponentScan
public class SecurityConfig extends WebSecurityConfigurerAdapter
{
    @Autowired
    UserRepository userRepository;

    @Autowired
    JwtAuthenticator jwtAuthenticator;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception
    {
        auth.authenticationProvider(jwtAuthenticationProvider());
    }

    @Bean
    public AuthenticationProvider jwtAuthenticationProvider()
    {
        return new JwtAuthententicationProvider(userRepository, jwtAuthenticator);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception
    {
        http.regexMatcher("/admin.*")
                .addFilterAfter(jwtAuthenticationFilter(), BasicAuthenticationFilter.class)
                .csrf().disable();
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() throws Exception
    {
        return new JwtAuthenticationFilter(authenticationManagerBean(), new Http403ForbiddenEntryPoint());
    }
}
