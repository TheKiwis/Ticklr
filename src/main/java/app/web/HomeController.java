package app.web;

import app.data.User;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author ngnmhieu
 */
@RestController
public class HomeController
{
    @RequestMapping("/admin")
    public String homepage()
    {
        return "Default Homepage";
    }

    @RequestMapping("/users/profile")
    public User test()
    {
        return new User("email@example.com", "123456789");
    }
}
