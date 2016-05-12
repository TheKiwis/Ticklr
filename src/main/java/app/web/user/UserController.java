package app.web.user;

import app.data.User;
import app.services.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.persistence.PersistenceException;
import javax.validation.Valid;
import java.net.URI;
import java.util.UUID;

/**
 * Contains REST API Endpoints for following services:
 * - user registration
 * - ...
 *
 * @author ngnmhieu
 */
@RestController
public class UserController
{
    private UserURI uri;

    private UserRepository repo;

    /**
     * @param repo fetches and saves User object
     */
    @Autowired
    public UserController(UserRepository repo, UserURI uri)
    {
        this.repo = repo;
        this.uri = uri;
    }

    /**
     * TODO: only authorized user
     * @param userId
     * @return
     */
    @RequestMapping(value = UserURI.USER_URI, method = RequestMethod.GET)
    public ResponseEntity show(@PathVariable UUID userId)
    {
        User user = repo.findById(userId);

        if (user == null)
            return new ResponseEntity(HttpStatus.NOT_FOUND);

        return new ResponseEntity(user, HttpStatus.OK);
    }

    /**
     * Register user
     *
     * @param userForm      contains registration information (e.g. email and password)
     * @param bindingResult validation information
     * @return
     */
    @RequestMapping(value = UserURI.USERS_URI, method = RequestMethod.POST)
    public ResponseEntity processRegistration(@RequestBody @Valid UserForm userForm, BindingResult bindingResult)
    {
        HttpHeaders headers = new HttpHeaders();
        HttpStatus status;

        if (!bindingResult.hasFieldErrors()) {

            status = HttpStatus.CREATED;

            try {
                User user = repo.save(userForm.getUser());
                headers.setLocation(URI.create(uri.userURL(user.getId())));
            } catch (PersistenceException e) {
                status = HttpStatus.CONFLICT; // duplicated email found
            }

        } else {
            status = HttpStatus.BAD_REQUEST;
        }

        return new ResponseEntity(bindingResult.getFieldErrors(), headers, status);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity handleHttpMessageNotReadableException(HttpMessageNotReadableException ex)
    {
        // todo use logger to log ex.getMessage()
        return new ResponseEntity("{\"message\": \"The request sent by the client was syntactically incorrect.\"}", HttpStatus.BAD_REQUEST);
    }
}
