package app.web;

import app.data.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author ngnmhieu
 */
@Controller
public class HomeController
{
    @RequestMapping("/test")
    @ResponseBody
    public User test()
    {
        return new User("email@example.com", "123456789");
    }
}
