package config;

import app.web.authorization.IdentityAuthorizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.context.WebApplicationContext;

/**
 * @author DucNguyenMinh
 * @since 29.02.16.
 */
@Configuration
public class ApplicationBeans
{
    /**
     * This bean is defined with Request scope, thus is created
     * in each request.
     *
     * @return Authentication information, which contains the Principal.
     * The Principal is retrieved via getPrincipal() method.
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
    public IdentityAuthorizer userAuthorizer(Authentication authInfo)
    {
        return new IdentityAuthorizer(authInfo);
    }

    /**
     * @return PasswordEncoder encodes identity's raw password, which will be saved into the database
     */
    @Bean
    public PasswordEncoder passwordEncoder()
    {
        return new BCryptPasswordEncoder();
    }
}
