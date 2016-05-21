package app.web.authentication;

import app.services.UserRepository;
import app.web.user.UserForm;
import app.web.user.UserURI;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.token.Token;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.UUID;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author ngnmhieu
 * @since 12.05.16
 */
@RunWith(MockitoJUnitRunner.class)
public class AuthControllerTest
{
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    UserRepository userRepository;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    JwtHelper jwtHelper;

    AuthController controller;

    @Mock
    PasswordEncoder passwordEncoder;

    @Before
    public void setup()
    {
        controller = new AuthController(userRepository, jwtHelper, passwordEncoder, new UserURI("http://localhost"));
    }

    @Test
    public void login_shouldReturnAuthenticationToken() throws Exception
    {
        // mock objects
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);

        when(userRepository
                .findByEmail(anyString())
                .getId()).thenReturn(UUID.randomUUID());

        Token token = mock(Token.class);
        when(jwtHelper.generateToken(any())).thenReturn(token);

        UserForm form = mock(UserForm.class);
        when(form.getEmail()).thenReturn("user@example.com");

        // test object
        ResponseEntity response = controller.requestAuthToken(form);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(token, response.getBody());
    }

    @Test
    public void login_shouldReturnHttpStatusUnauthorizedStatus()
    {
        // mock objects
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);

        UserForm form = mock(UserForm.class);
        when(form.getEmail()).thenReturn("user@example.com");

        // test Object
        ResponseEntity response = controller.requestAuthToken(form);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    public void login_shouldReturnHttpStatusUnauthorizedStatusForNonExistentUser()
    {
        // mock objects
        when(userRepository
                .findByEmail(anyString())
        ).thenReturn(null);

        UserForm form = mock(UserForm.class);
        when(form.getEmail()).thenReturn("nonexistentuser@example.com");

        // test Object
        ResponseEntity response = controller.requestAuthToken(form);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());

    }
}