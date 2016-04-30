package app.data;

import static org.junit.Assert.*;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

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
public class UserRepositoryTest
{
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    EntityManager em;

    @Mock
    User user;

    UUID userId = UUID.fromString("4eab8080-0f0e-11e6-9f74-0002a5d5c51b");

    protected UserRepository userRepository;

    @Before
    public void setUp()
    {
        userRepository = new UserRepository(em);
    }

    @Test
    public void findById_shouldReturnTheCorrectUser() throws Exception
    {
        when(em.find(User.class, userId)).thenReturn(user);

        assertEquals(user, userRepository.findById(userId));
    }

    @Test
    public void findById_shouldReturnNullIfNoUserFound() throws Exception
    {
        when(em.find(User.class, userId)
        ).thenThrow(NoResultException.class);

        assertNull(userRepository.findById(userId));
    }

    @Test
    public void findByEmail_ShouldReturnUser() throws Exception
    {
        when(em.createQuery(anyString())
                .setParameter(anyString(), anyString())
                .getSingleResult()
        ).thenReturn(user);

        UserRepository repo = new UserRepository(em);
        assertEquals(user, repo.findByEmail("someEmail@gmail.com"));
    }

    @Test
    public void findByEmail_ShouldReturnNullIfNoUserFound() throws Exception
    {
        when(em.createQuery(anyString())
                .setParameter(anyString(), anyString())
                .getSingleResult()
        ).thenThrow(NoResultException.class);


        assertNull(userRepository.findByEmail("non_existent_email@gmail.com"));
    }

    @Test
    public void save_ShouldCreateNewUser() throws Exception
    {
        User mockUser = mock(User.class);
        assertEquals(mockUser, userRepository.save(mockUser));
        verify(em, times(1)).persist(mockUser);
    }

    // todo saveOrUpdate fail
}
