package app.web;

import app.data.User;
import app.data.UserRepository;
import app.web.forms.UserForm;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.token.DefaultToken;
import org.springframework.security.core.token.Token;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

/**
 * @author ngnmhieu
 */
@RestController
@RequestMapping("/users")
public class UserController
{
    private UserRepository repo;

    private String authSecret;

    @Autowired
    public UserController(UserRepository repo, @Value("${auth.secret}") String authSecret)
    {
        this.repo = repo;
        this.authSecret = authSecret;
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

    @RequestMapping(value="/login", method = RequestMethod.POST)
    public ResponseEntity login(UserForm form)
    {
        // todo handle exception
        User user = repo.findByEmail(form.getEmail());

        Token token = null;
        HttpStatus status;
        if (user.authenticate(form.getPassword())) {
            String jwtToken = Jwts.builder()
                    .setHeaderParam("typ", "JWT")
                    .setSubject(form.getEmail())
                    .signWith(SignatureAlgorithm.HS256, authSecret.getBytes(StandardCharsets.UTF_8)).compact();
            token = new DefaultToken(jwtToken, LocalDateTime.now().toEpochSecond(ZoneOffset.UTC), "");
            status = HttpStatus.OK;
        } else {
            status = HttpStatus.UNAUTHORIZED;
        }

        return new ResponseEntity(token, status);
    }

}
