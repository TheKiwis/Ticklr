package app.web.authentication;

import app.data.User;
import app.services.UserRepository;
import app.web.user.UserForm;
import app.web.user.UserURI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
import java.util.UUID;

/**
 * @author ngnmhieu
 * @since 12.05.16
 */
@RestController
public class AuthController
{
    private PasswordEncoder passwordEncoder;

    private UserRepository repo;

    private UserURI userURI;

    private JwtHelper jwtHelper;

    /**
     * @param repo      fetches and saves User object
     * @param jwtHelper Jwt Authentication helper object
     * @param userURI
     */
    @Autowired
    public AuthController(UserRepository repo, JwtHelper jwtHelper, PasswordEncoder passwordEncoder, UserURI userURI)
    {
        this.repo = repo;
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
    public ResponseEntity requestAuthToken(@RequestBody UserForm form)
    {
        User user = repo.findByEmail(form.getEmail());

        if (user != null && passwordEncoder.matches(form.getPassword(), user.getIdentity().getPassword())) {
            Token token = jwtHelper.generateToken(user.getIdentity());
            HttpHeaders headers = new HttpHeaders();
            headers.setLocation(URI.create(userURI.userURL(user.getId())));
            return new ResponseEntity(token, headers, HttpStatus.OK);
        }

        return new ResponseEntity(HttpStatus.UNAUTHORIZED);
    }
}
