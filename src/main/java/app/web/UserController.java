package app.web;

import app.data.User;
import app.data.UserRepository;
import app.web.forms.UserForm;
import app.web.authentication.JwtAuthenticator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.token.Token;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.persistence.PersistenceException;
import javax.validation.Valid;

/**
 * Contains REST API Endpoints for following services:
 *  - user registration
 *  - authentication token request
 *  - ...
 *
 * @author ngnmhieu
 */
@RestController
@RequestMapping("/users")
public class UserController
{
    private UserRepository repo;

    private JwtAuthenticator jwtAuthenticator;

    /**
     * @param repo fetches and saves User object
     * @param jwtAuthenticator Jwt Authentication helper object
     */
    @Autowired
    public UserController(UserRepository repo, JwtAuthenticator jwtAuthenticator)
    {
        this.repo = repo;
        this.jwtAuthenticator = jwtAuthenticator;
    }

    /**
     * Register user
     * @param userForm contains registration information (e.g. email and password)
     * @param bindingResult validation information
     * @return
     */
    @RequestMapping(method = RequestMethod.POST)
    // todo accept other types of request content-type like json, xml (not only x-www-form-urlencoded)
    public ResponseEntity processRegistration(@Valid UserForm userForm, BindingResult bindingResult)
    {
        HttpStatus status;
        if (!bindingResult.hasFieldErrors()) {

            status = HttpStatus.CREATED;

            try {
                repo.save(userForm.getUser());
            } catch (PersistenceException e) {
                status = HttpStatus.CONFLICT; // duplicated email found
            }

        } else {
            status = HttpStatus.BAD_REQUEST;
        }

        return new ResponseEntity(bindingResult.getFieldErrors(), status);
    }

    /**
     * Grants client with authentication token (with expiration)
     * Client uses this token for subsequent requests to access authorized resources
     *
     * @param form contains authentication information (i.e. email and password)
     * @return
     */
    @RequestMapping(value= "/request-auth-token", method = RequestMethod.POST)
    public ResponseEntity requestAuthToken(UserForm form)
    {
        User user = repo.findByEmail(form.getEmail());

        Token token = null;
        HttpStatus status;
        if (user != null && user.authenticate(form.getPassword())) {
            token = jwtAuthenticator.generateToken(form.getEmail());
            status = HttpStatus.OK;
        } else {
            status = HttpStatus.UNAUTHORIZED;
        }

        return new ResponseEntity(token, status);
    }

}
