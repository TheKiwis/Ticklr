package app.config.security;

import app.data.UserRepository;
import app.web.authentication.JwtAuthenticationProvider;
import app.web.authentication.JwtAuthenticationFilter;
import app.web.authentication.JwtAuthenticator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
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
        return new JwtAuthenticationProvider(userRepository, jwtAuthenticator);
    }

    @Override
    // todo: things to configure
    //  - configure places that require HTTPS (requires Channels)
    //  - configure roles (how can it work with jwtAuthenticationFilter?)
    //  - consider if we need csrf, or jwt token is enough see: https://docs.spring.io/spring-security/site/docs/current/reference/html/csrf.html
    protected void configure(HttpSecurity http) throws Exception
    {
        // temporarily disable csrf
        http.csrf().disable();

        // performs Jwt Authentication
        http.addFilterAfter(jwtAuthenticationFilter(), BasicAuthenticationFilter.class);

        http.authorizeRequests()
                .antMatchers(HttpMethod.GET, "/users/**").authenticated()
                .antMatchers("/users/*/events/**", "/users/*/basket/**").authenticated();

    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() throws Exception
    {
        return new JwtAuthenticationFilter(authenticationManagerBean(), new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED));
    }
}
