package app.web.user;

import app.data.User;
import app.services.UserRepository;
import app.web.authentication.JwtHelper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    JwtHelper jwtHelper;

    UserController controller;

    UUID userId = UUID.fromString("4eab8080-0f0e-11e6-9f74-0002a5d5c51b");

    @Before
    public void setup()
    {
        UserURI userURI = mock(UserURI.class);
        when(userURI.userURL(any())).thenReturn("http://localhost/api/users/4eab8080-0f0e-11e6-9f74-0002a5d5c51b");
        controller = new UserController(userRepository, userURI);
    }

    @Test
    public void show_shouldReturnHttpOK() throws Exception
    {
        User mockUser = mock(User.class);

        when(userRepository.findById(userId)).thenReturn(mockUser);

        ResponseEntity response = controller.show(userId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockUser, response.getBody());
    }

    @Test
    public void show_shouldReturnHttpNotFound() throws Exception
    {
        when(userRepository.findById(userId)).thenReturn(null);

        ResponseEntity response = controller.show(userId);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
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
}
