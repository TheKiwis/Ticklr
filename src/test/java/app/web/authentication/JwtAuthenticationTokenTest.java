package app.web.authentication;

import app.data.User;
import org.junit.Test;
import org.springframework.security.core.GrantedAuthority;

import java.util.ArrayList;

import static org.junit.Assert.*;

/**
 * @author ngnmhieu
 */
public class JwtAuthenticationTokenTest
{
    @Test
    public void testJwtAuthenticationTokenEquality() throws Exception
    {
        JwtAuthenticationToken unauthenticatedToken1 = new JwtAuthenticationToken("credential");
        JwtAuthenticationToken unauthenticatedToken2 = new JwtAuthenticationToken("credential");
        assertTrue(unauthenticatedToken1.equals(unauthenticatedToken2));


        JwtAuthenticationToken authenticatedToken1 = new JwtAuthenticationToken(new User("user1@example.com", "pass"), "credential1", new ArrayList<GrantedAuthority>());
        JwtAuthenticationToken authenticatedToken2 = new JwtAuthenticationToken(new User("user2@example.com", "pass"), "credential2", new ArrayList<GrantedAuthority>());
        assertFalse(authenticatedToken1.equals(authenticatedToken2));
    }
}