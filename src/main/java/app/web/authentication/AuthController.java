package app.web.authentication;

import app.data.Buyer;
import app.data.Identity;
import app.data.User;
import app.services.BuyerService;
import app.services.IdentityService;
import app.services.UserService;
import app.web.ResourceURI;
import app.web.user.LoginForm;
import app.web.user.UserURI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.token.Token;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

/**
 * @author ngnmhieu
 * @since 12.05.16
 */
@RestController
public class AuthController
{
    public static final ResponseEntity UNAUTHORIZED = new ResponseEntity(HttpStatus.UNAUTHORIZED);
    private PasswordEncoder passwordEncoder;

    private UserService userService;

    private IdentityService idService;

    private JwtHelper jwtHelper;

    private BuyerService buyerService;

    private ResourceURI resURI;

    /**
     * @param idService
     * @param userService
     * @param buyerService
     * @param jwtHelper    Jwt Authentication helper object
     * @param resURI
     */
    @Autowired
    public AuthController(IdentityService idService, UserService userService, BuyerService buyerService,
                          JwtHelper jwtHelper, PasswordEncoder passwordEncoder, ResourceURI resURI)
    {
        this.userService = userService;
        this.idService = idService;
        this.jwtHelper = jwtHelper;
        this.resURI = resURI;
        this.passwordEncoder = passwordEncoder;
        this.buyerService = buyerService;
    }

    /**
     * Grants client with authentication token (with expiration)
     * Client uses this token for subsequent requests to access authorized resources
     *
     * @param form contains authentication information (i.e. email and password)
     * @return
     */
    @RequestMapping(value = AuthURI.AUTH_URI, method = RequestMethod.POST)
    public ResponseEntity requestToken(@RequestBody LoginForm form)
    {
        Identity id = idService.findByEmail(form.getEmail());

        if (id == null)
            return UNAUTHORIZED;

        if (!passwordEncoder.matches(form.getPassword(), id.getPassword()))
            return UNAUTHORIZED;

        String token = jwtHelper.generateToken(id);

        // we assume that there always exists a user corresponding to an identity,
        // ensure that a user is created when an identity is created
        User user = userService.findByIdentity(id);
        Buyer buyer = buyerService.findByIdentity(id);

        return new ResponseEntity(new AuthResponse(token, user, buyer, resURI), HttpStatus.OK);
    }
}
