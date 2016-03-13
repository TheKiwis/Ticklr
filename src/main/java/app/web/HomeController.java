package app.web;

import app.data.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author ngnmhieu
 */
@RestController
public class HomeController
{
    @RequestMapping("/test")
    public User homepage()
    {
        return new User("email@example.com", "123456789");
    }
}
