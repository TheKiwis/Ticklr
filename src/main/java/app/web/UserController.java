package app.web;

import app.data.User;
import app.data.UserRepository;
import app.web.forms.UserForm;
import app.web.authentication.JwtAuthenticator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.token.Token;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.persistence.PersistenceException;
import javax.validation.Valid;
import java.net.URI;

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

    // todo only user authorization
    @RequestMapping(value = "/{userId}", method = RequestMethod.GET)
    public ResponseEntity show(@PathVariable long userId)
    {
        User user = repo.findById(userId);

        if (user == null)
            return new ResponseEntity(HttpStatus.NOT_FOUND);

        return new ResponseEntity(user, HttpStatus.OK);
    }

    /**
     * Register user
     * @param userForm contains registration information (e.g. email and password)
     * @param bindingResult validation information
     * @return
     */
    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity processRegistration(@Valid UserForm userForm, BindingResult bindingResult)
    {
        HttpHeaders headers = new HttpHeaders();
        HttpStatus status;

        if (!bindingResult.hasFieldErrors()) {

            status = HttpStatus.CREATED;

            try {
                User user = repo.save(userForm.getUser());
                headers.setLocation(URI.create("/users/" + user.getId()));
            } catch (PersistenceException e) {
                status = HttpStatus.CONFLICT; // duplicated email found
            }

        } else {
            status = HttpStatus.BAD_REQUEST;
        }

        return new ResponseEntity(bindingResult.getFieldErrors(), headers, status);
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
