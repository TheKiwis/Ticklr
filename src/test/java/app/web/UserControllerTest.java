package app.web;

import app.data.User;
import app.data.UserRepository;
import app.web.forms.UserForm;
import app.web.authentication.JwtAuthenticator;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.token.Token;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * @author ngnmhieu
 */
@RunWith(MockitoJUnitRunner.class)
public class UserControllerTest
{
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    UserRepository userRepository;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    JwtAuthenticator jwtAuthenticator;

    UserController controller;

    // Signature Secret for JWT
    private static final String authSecret = "test_secret";

    @Before
    public void setup()
    {
        controller = new UserController(userRepository, jwtAuthenticator);
    }

    @Test
    public void processRegistration_ShouldRespondWith201HTTPStatus() throws Exception
    {
        // mock objects
        UserForm userForm = new UserForm();
        userForm.setEmail("user@email.com").setPassword("123456789");

        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.hasFieldErrors()).thenReturn(false);
        when(bindingResult.getFieldErrors()).thenReturn(new ArrayList<FieldError>());

        // test object
        ResponseEntity response = controller.processRegistration(userForm, bindingResult);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertTrue(((List) response.getBody()).isEmpty());
    }

    @Test
    public void processRegistration_ShouldSaveUser() throws Exception
    {
        // mock objects
        UserForm userForm = new UserForm();
        userForm.setEmail("email").setPassword("123456789");

        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.hasFieldErrors()).thenReturn(false);
        when(bindingResult.getFieldErrors()).thenReturn(new ArrayList<FieldError>());

        // test object
        controller.processRegistration(userForm, bindingResult);

        verify(userRepository, atLeastOnce()).save(any(User.class));
    }

    @Test
    public void processRegistration_ShouldRespondWith400HTTPStatusOnInvalidInput() throws Exception
    {
        // mock objects
        UserForm userForm = new UserForm();
        userForm.setEmail("invalid email").setPassword("123456789");

        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.hasFieldErrors()).thenReturn(true);
        when(bindingResult.getFieldErrors()).thenReturn(Arrays.asList(new FieldError[]{new FieldError("", "", "")}));

        // test object
        ResponseEntity response = controller.processRegistration(userForm, bindingResult);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertFalse(((List) response.getBody()).isEmpty());
    }

    @Test
    public void login_shouldReturnAuthenticationToken() throws Exception
    {
        // mock objects
        when(userRepository
                .findByEmail(anyString())
                .authenticate(anyString())
        ).thenReturn(true);
        Token token = mock(Token.class);
        when(jwtAuthenticator.generateToken(anyString())).thenReturn(token);

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
        when(userRepository
            .findByEmail(anyString())
            .authenticate(anyString())
        ).thenReturn(false);

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
