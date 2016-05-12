package app.web.authentication;

import app.data.User;
import app.services.UserRepository;
import app.web.user.UserForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.token.Token;
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
@RequestMapping("/api/auth")
public class AuthController
{
    private UserRepository repo;

    private JwtHelper jwtHelper;

    private String hostname;

    /**
     * @param repo      fetches and saves User object
     * @param jwtHelper Jwt Authentication helper object
     * @param hostname  hostname of the server on which the app is running
     */
    @Autowired
    public AuthController(UserRepository repo, JwtHelper jwtHelper, @Value("${app.server.host}") String hostname)
    {
        this.repo = repo;
        this.jwtHelper = jwtHelper;
        this.hostname = hostname;
    }

    /**
     * Grants client with authentication token (with expiration)
     * Client uses this token for subsequent requests to access authorized resources
     *
     * @param form contains authentication information (i.e. email and password)
     * @return
     */
    @RequestMapping(value = "/request-token", method = RequestMethod.POST)
    public ResponseEntity requestAuthToken(@RequestBody UserForm form)
    {
        User user = repo.findByEmail(form.getEmail());

        if (user != null && user.authenticate(form.getPassword())) {
            Token token = jwtHelper.generateToken(user);
            HttpHeaders headers = new HttpHeaders();
            headers.setLocation(URI.create(getFullURL(userURI(user.getId()))));
            return new ResponseEntity(token, headers, HttpStatus.OK);
        }

        return new ResponseEntity(HttpStatus.UNAUTHORIZED);
    }

    /**
     * @param userId user's id; if userId == null, it's not appended
     * @return /{USER_BASE_URI}[/userId]
     */
    public static String userURI(UUID userId)
    {
        return "/api/users" + (userId == null ? "" : "/" + userId);
    }

    /**
     * @param url
     * @return the full URL to a resource containing hostname
     */
    private String getFullURL(String url)
    {
        return hostname + url;
    }
}
