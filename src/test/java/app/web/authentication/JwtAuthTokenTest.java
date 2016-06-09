package app.web.authentication;

import app.data.user.Identity;
import app.data.user.User;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author ngnmhieu
 */
public class JwtAuthTokenTest
{
    @Test
    public void testJwtAuthenticationTokenEquality() throws Exception
    {
        JwtAuthToken unauthenticatedToken1 = new JwtAuthToken("credential");
        JwtAuthToken unauthenticatedToken2 = new JwtAuthToken("credential");
        assertTrue(unauthenticatedToken1.equals(unauthenticatedToken2));


        JwtAuthToken authenticatedToken1 = new JwtAuthToken(new User(new Identity("user1@example.com", "pass")));
        JwtAuthToken authenticatedToken2 = new JwtAuthToken(new User(new Identity("user2@example.com", "pass")));
        assertFalse(authenticatedToken1.equals(authenticatedToken2));
    }
}