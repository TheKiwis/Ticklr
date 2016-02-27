package app.web;

import app.data.User;
import app.data.UserRepository;
import app.web.forms.UserForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author ngnmhieu
 */
//@RestController
@RestController
@RequestMapping("/users")
public class UserController
{
    private UserRepository repo;

    @Autowired
    public UserController(UserRepository repo)
    {
        this.repo = repo;
    }

    @RequestMapping(method = RequestMethod.POST)
    // todo accept other types of request content-type like json, xml (not only x-www-form-urlencoded)
    public ResponseEntity processRegistration(@Valid UserForm userForm, BindingResult bindingResult)
    {
        HttpStatus status;
        if (!bindingResult.hasErrors()) {
            repo.save(userForm.getUser());
            status = HttpStatus.CREATED;
        } else {
            status = HttpStatus.BAD_REQUEST;
        }

        return new ResponseEntity(bindingResult.getFieldErrors(), status);
    }
}
