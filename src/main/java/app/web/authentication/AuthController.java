package app.web.authentication;

import app.data.Identity;
import app.data.User;
import app.services.IdentityRepository;
import app.services.UserRepository;
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

    private UserRepository userRepo;

    private IdentityRepository identityRepo;

    private UserURI userURI;

    private JwtHelper jwtHelper;

    /**
     * @param identityRepo
     * @param userRepo     fetches and saves User object
     * @param jwtHelper    Jwt Authentication helper object
     * @param userURI
     */
    @Autowired
    public AuthController(IdentityRepository identityRepo, UserRepository userRepo, JwtHelper jwtHelper, PasswordEncoder passwordEncoder, UserURI userURI)
    {
        this.userRepo = userRepo;
        this.identityRepo = identityRepo;
        this.jwtHelper = jwtHelper;
        this.userURI = userURI;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Grants client with authentication token (with expiration)
     * Client uses this token for subsequent requests to access authorized resources
     *
     * @param form contains authentication information (i.e. email and password)
     * @return
     */
    @RequestMapping(value = AuthURI.AUTH_URI, method = RequestMethod.POST)
    public ResponseEntity requestOrganiserToken(@RequestBody LoginForm form)
    {
        Identity id = identityRepo.findByEmail(form.getEmail());

        if (id == null)
            return UNAUTHORIZED;

        if (!passwordEncoder.matches(form.getPassword(), id.getPassword()))
            return UNAUTHORIZED;

        Token token = jwtHelper.generateToken(id);

        HttpHeaders headers = new HttpHeaders();

        // we assume that there always exists a user corresponding to an identity,
        // ensure that a user is created when an identity is created
        User user = userRepo.findByIdentity(id);
        headers.setLocation(URI.create(userURI.userURL(user.getId())));

        return new ResponseEntity(token, headers, HttpStatus.OK);
    }
}
