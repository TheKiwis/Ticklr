package config;

import app.web.authentication.JwtHelper;
import app.web.authorization.UserAuthorizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.context.WebApplicationContext;

/**
 * @author DucNguyenMinh
 * @since 29.02.16.
 */
@Configuration
public class ApplicationBeans
{
    @Bean
    public JwtHelper jwtAuthenticator(@Value("${auth.secret}") String authSecret)
    {
        return new JwtHelper(authSecret);
    }

    /**
     * This bean is defined with Request scope, thus is created
     * in each request.
     *
     * @return Authentication information, which contains the Principal.
     *         The Principal is retrieved via getPrincipal() method.
     */
    @Bean
    @Scope(value = WebApplicationContext.SCOPE_REQUEST, proxyMode = ScopedProxyMode.INTERFACES)
    public Authentication authInfo()
    {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    /**
     * @param authInfo Authentication Information is created on each request
     */
    @Bean
    @Autowired
    public UserAuthorizer userAuthorizer(Authentication authInfo)
    {
        return new UserAuthorizer(authInfo);
    }
}
