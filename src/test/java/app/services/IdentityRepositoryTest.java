package app.services;

import app.data.Identity;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.persistence.EntityManager;

import java.util.UUID;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * @author ngnmhieu
 * @since 21.05.16
 */
@RunWith(MockitoJUnitRunner.class)
public class IdentityRepositoryTest
{
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private EntityManager em;

    private IdentityRepository repo;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Before
    public void setUp() throws Exception
    {
        repo = new IdentityRepository(em, passwordEncoder);
    }

    @Test
    public void save_shouldPersistNewIdentityUsingEntityManager()
    {
        Identity id = new Identity("user@example.com", "password");
        repo.save(id);
        verify(em, atLeastOnce()).persist(id);
    }

    @Test
    public void save_should_encrypt_password() throws Exception
    {
        String password = "password";
        Identity id = new Identity("user@example.com", password);
        repo.save(id);
        verify(passwordEncoder, atLeastOnce()).encode(password);
    }

    @Test
    public void save_shouldMergeExistedIdentityUsingEntityManager()
    {
        Identity id = new Identity(UUID.randomUUID(), "user@example.com", "password");
        when(em.merge(id)).thenReturn(id);

        repo.save(id);
        verify(em, atLeastOnce()).merge(id);
    }

    @Test
    public void findById_should_return_an_identity() throws Exception
    {
        UUID uuid = UUID.randomUUID();
        Identity mockIdentity = mock(Identity.class);
        when(em.find(Identity.class, uuid)).thenReturn(mockIdentity);

        assertEquals(mockIdentity, repo.findById(uuid));
    }
}