package app.web;

import app.data.User;
import app.data.UserRepository;
import app.web.forms.UserForm;
import io.jsonwebtoken.Jwts;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
 import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.token.DefaultToken;
import org.springframework.security.core.token.Token;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

/**
 * @author ngnmhieu
 */
@RunWith(MockitoJUnitRunner.class)
public class UserControllerTest
{
    MockMvc mockMvc;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    UserRepository userRepository;

    UserController controller;

    @Before
    public void setup()
    {
        controller = new UserController(userRepository, "test_secret");
        mockMvc = standaloneSetup(controller).build();
    }

    @Test
    public void processRegistration_ShouldRespondWith201HTTPStatus() throws Exception
    {
        mockMvc.perform(
                post("/users/")
                        .param("email", "user@gmail.com")
                        .param("password", "123456789")
        ).andExpect(status().isCreated());
    }

    @Test
    public void processRegistration_ShouldSaveUser() throws Exception
    {
        mockMvc.perform(
                post("/users/")
                        .param("email", "user@gmail.com")
                        .param("password", "123456789"));

        verify(userRepository, atLeastOnce()).save(any(User.class));
    }

    @Test
    public void processRegistration_ShouldRespondWith400HTTPStatusOnInvalidInput() throws Exception
    {
        mockMvc.perform(
                post("/users/")
                        .param("email", "invalid email")
                        .param("password", "123456789"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$").isNotEmpty());
    }

    @Test
    public void login_shouldReturnAuthenticationToken() throws Exception
    {
        when(userRepository
                .findByEmail(anyString())
                .authenticate(anyString())
        ).thenReturn(true);

        UserForm form = mock(UserForm.class);
        when(form.getEmail()).thenReturn("user@example.com");

        ResponseEntity response = controller.login(form);

        assertEquals(response.getStatusCode(), HttpStatus.OK);

        assertEquals("user@example.com", Jwts.parser().setSigningKey("test_secret").parseClaimsJws(((Token)response.getBody()).getKey()).getBody().getSubject());
    }

    // todo test unauthenticated 401
}
