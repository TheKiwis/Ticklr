package app.services;

import static org.junit.Assert.*;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

import app.data.user.Identity;
import app.data.user.User;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import java.util.UUID;

/**
 * @author ngnmhieu
 */
@RunWith(MockitoJUnitRunner.class)
public class UserServiceTest
{
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    EntityManager em;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    IdentityService identityService;

    @Mock
    User user;

    UUID userId = UUID.fromString("4eab8080-0f0e-11e6-9f74-0002a5d5c51b");

    protected UserService userService;

    @Before
    public void setUp()
    {
        userService = new UserService(em, identityService);
    }

    @Test
    public void findById_found() throws Exception
    {
        when(em.find(User.class, userId)).thenReturn(user);

        assertEquals(user, userService.findById(userId));
    }

    @Test
    public void findById_not_found() throws Exception
    {
        when(em.find(User.class, userId)).thenReturn(null);

        assertNull(userService.findById(userId));
    }

    @Test
    public void findByIdentity_found() throws Exception
    {
        Identity mockIdentity = mock(Identity.class);
        User user = new User(mockIdentity);
        when(em.createQuery(anyString())
                .setParameter(anyString(), anyString())
                .getSingleResult()).thenReturn(user);

        assertEquals(user, userService.findByIdentity(mockIdentity));
    }
    @Test
    public void findByIdentity_not_found() throws Exception
    {
        when(em.createQuery(anyString())
                .setParameter(anyString(), anyString())
                .getSingleResult()
        ).thenThrow(NoResultException.class);


        assertNull(userService.findByIdentity(new Identity("email", "password")));
    }

    @Test
    public void createWithIdentity() throws Exception
    {
        Identity mockIdentity = mock(Identity.class);
        User user = userService.createWithIdentity(mockIdentity);

        assertEquals(mockIdentity, user.getIdentity());
        verify(em, times(1)).persist(any(User.class));
    }

}
