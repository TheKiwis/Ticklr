package app.web;

import app.data.User;
import app.data.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

/**
 * @author ngnmhieu
 */
@RunWith(MockitoJUnitRunner.class)
public class UserControllerTest
{
    MockMvc mockMvc;

    @Mock
    UserRepository userRepository;

    @Before
    public void setup()
    {
        UserController controller = new UserController(userRepository);
        mockMvc = standaloneSetup(controller).build();
    }

    @Test
    public void processRegistrationShouldRespondWith201HTTPStatus() throws Exception
    {
        mockMvc.perform(
                post("/users/")
                        .param("email", "user@gmail.com")
                        .param("password", "123456789")
        ).andExpect(status().isCreated());
    }

    @Test
    public void processRegistrationShouldSaveUser() throws Exception
    {
        mockMvc.perform(
                post("/users/")
                        .param("email", "user@gmail.com")
                        .param("password", "123456789"));

        verify(userRepository, atLeastOnce()).save(any(User.class));
    }

    @Test
    public void processRegistrationShouldRespondWith400HTTPStatusOnInvalidInput() throws Exception
    {
        mockMvc.perform(
                post("/users/")
                        .param("email", "invalid email")
                        .param("password", "123456789"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$").isNotEmpty());
    }
}
