package app.web;

import app.data.Identity;
import app.data.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

/**
 * @author ngnmhieu
 */
@Controller
public class HomeController
{
    @RequestMapping(value = "/test", method = RequestMethod.GET)
    @ResponseBody
    public User test()
    {
        return new User(new Identity("user", "password"));
    }
}
