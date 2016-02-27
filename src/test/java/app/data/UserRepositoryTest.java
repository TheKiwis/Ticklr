package app.data;

import app.data.User;
import app.data.UserRepository;

import static org.junit.Assert.*;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import javax.persistence.EntityManager;

/**
 * @author ngnmhieu
 */
@RunWith(MockitoJUnitRunner.class)
public class UserRepositoryTest
{
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    EntityManager em;

    @Mock
    User user;

    @Test
    public void shouldExistUserRepository() throws Exception
    {
        assertNotNull(new UserRepository(null));
    }

    @Test
    public void testFindById() throws Exception
    {
        when(em.find(User.class, 123)).thenReturn(user);

        UserRepository repo = new UserRepository(em);
        assertEquals(user, repo.findById(123));
    }

    @Test
    public void testFindByEmail() throws Exception
    {
        when(em.createQuery(anyString())
                .setParameter(anyString(), anyString())
                .getSingleResult()
        ).thenReturn(user);

        UserRepository repo = new UserRepository(em);
        assertEquals(user, repo.findByEmail("someEmail@gmail.com"));
    }
}
