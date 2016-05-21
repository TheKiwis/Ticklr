package app.web.user;

import app.data.User;
import app.services.UserRepository;
import app.web.authorization.IdentityAuthorizer;
import app.web.basket.BasketURI;
import app.web.event.EventURI;
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
    private BasketURI basketURI;

    private EventURI eventURI;

    private IdentityAuthorizer identityAuthorizer;

    private UserURI userURI;

    private UserRepository repo;

    /**
     * @param repo fetches and saves User object
     */
    @Autowired
    public UserController(UserRepository repo, UserURI userURI, EventURI eventURI,
                          BasketURI basketURI, IdentityAuthorizer identityAuthorizer)
    {
        this.repo = repo;
        this.userURI = userURI;
        this.eventURI = eventURI;
        this.basketURI = basketURI;
        this.identityAuthorizer = identityAuthorizer;
    }

    /**
     * @param userId
     * @return information about a user
     */
    @RequestMapping(value = UserURI.USER_URI, method = RequestMethod.GET)
    public ResponseEntity show(@PathVariable UUID userId)
    {
        User user = repo.findById(userId);

        if (user == null)
            return new ResponseEntity(HttpStatus.NOT_FOUND);

        if (!identityAuthorizer.authorize(user.getIdentity()))
            return new ResponseEntity(user, HttpStatus.FORBIDDEN);

        return new ResponseEntity(new UserResponse(user, userURI, eventURI, basketURI), HttpStatus.OK);
    }

    /**
     * Creates a user
     *
     * @param userForm      contains registration information (e.g. email and password)
     * @param bindingResult validation information
     */
    @RequestMapping(value = UserURI.USERS_URI, method = RequestMethod.POST)
    public ResponseEntity create(@RequestBody @Valid UserForm userForm, BindingResult bindingResult)
    {
        HttpHeaders headers = new HttpHeaders();
        HttpStatus status;

        if (!bindingResult.hasFieldErrors()) {

            status = HttpStatus.CREATED;

            try {
                User user = repo.save(userForm.getUser());
                headers.setLocation(URI.create(userURI.userURL(user.getId())));
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
