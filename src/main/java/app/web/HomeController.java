package app.web;

import app.data.User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author ngnmhieu
 */
@Controller
public class HomeController
{
    @RequestMapping("/")
    public @ResponseBody String homepage()
    {
        return "Default Homepage";
    }
}
