package app.services;

import app.data.Identity;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;

import java.util.UUID;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * @author ngnmhieu
 * @since 21.05.16
 */
@RunWith(MockitoJUnitRunner.class)
public class IdentityServiceTest
{
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private EntityManager em;

    private IdentityService repo;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Before
    public void setUp() throws Exception
    {
        repo = new IdentityService(em, passwordEncoder);
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
    public void save_should_merge_existed_Identity_using_EntityManager()
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

    @Test
    public void findByEmail_should_return_an_identity() throws Exception
    {
        Identity mockIdentity = mock(Identity.class);

        when(em.createQuery(anyString())
                .setParameter(anyString(), anyString())
                .getSingleResult()
        ).thenReturn(mockIdentity);

        assertEquals(mockIdentity, repo.findByEmail("user@example.com"));
    }

    @Test
    public void findByEmail_should_return_null_if_nothing_found() throws Exception
    {
        when(em.createQuery(anyString())
                .setParameter(anyString(), anyString())
                .getSingleResult()
        ).thenThrow(NoResultException.class);

        assertEquals(null, repo.findByEmail("user@example.com"));
    }

}