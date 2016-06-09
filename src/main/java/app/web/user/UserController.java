package app.web.user;

import app.data.user.Identity;
import app.data.user.User;
import app.services.BuyerService;
import app.services.EventRepository;
import app.services.IdentityService;
import app.services.UserService;
import app.web.ResourceURI;
import app.web.authorization.IdentityAuthorizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
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
    private IdentityAuthorizer identityAuthorizer;

    private UserService userService;

    private BuyerService buyerService;

    private IdentityService identityService;

    private ResourceURI resURI;

    private EventRepository eventService;

    /**
     * @param userService fetches and saves User object
     */
    @Autowired
    public UserController(UserService userService, BuyerService buyerService, IdentityService identityService,
                          ResourceURI resURI, EventRepository eventService, IdentityAuthorizer identityAuthorizer)
    {
        this.userService = userService;
        this.resURI = resURI;
        this.identityAuthorizer = identityAuthorizer;
        this.buyerService = buyerService;
        this.identityService = identityService;
        this.eventService = eventService;
    }

    /**
     * @param userId
     * @return information about a user
     */
    @RequestMapping(value = UserURI.USER_URI, method = RequestMethod.GET)
    public ResponseEntity show(@PathVariable UUID userId)
    {
        User user = userService.findById(userId);

        if (user == null)
            return new ResponseEntity(HttpStatus.NOT_FOUND);

        if (!identityAuthorizer.authorize(user.getIdentity()))
            return new ResponseEntity(user, HttpStatus.FORBIDDEN);

        return new ResponseEntity(new UserResponse(user, resURI), HttpStatus.OK);
    }

    /**
     * Creates a user
     *
     * @param loginForm     contains registration information (e.g. email and password)
     * @param bindingResult validation information
     */
    @RequestMapping(value = UserURI.USERS_URI, method = RequestMethod.POST)
    public ResponseEntity create(@RequestBody @Valid LoginForm loginForm, BindingResult bindingResult)
    {
        if (bindingResult.hasFieldErrors())
            return new ResponseEntity(bindingResult.getFieldErrors(), HttpStatus.BAD_REQUEST);

        if (identityService.findByEmail(loginForm.getEmail()) != null)
            return new ResponseEntity(HttpStatus.CONFLICT);

        Identity id = identityService.save(loginForm.getIdentity());
        userService.createWithIdentity(id);
        buyerService.createWithIdentity(id);

        return new ResponseEntity(new RegistrationResponse(resURI), HttpStatus.CREATED);
    }
}
