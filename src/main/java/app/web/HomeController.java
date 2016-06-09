package app.web;

import app.data.user.Identity;
import app.data.user.User;
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
