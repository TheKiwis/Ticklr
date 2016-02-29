package app.web.forms;

import app.data.User;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.Length;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * @author ngnmhieu
 */
public class UserForm
{
    @NotNull
    @Email(regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$")
    // todo unique email
    private String email;

    @NotNull
    @Length(min=8)
    private String password;

    public String getEmail()
    {
        return email;
    }

    public UserForm setEmail(String email)
    {
        this.email = email;
        return this;
    }

    public String getPassword()
    {
        return password;
    }

    public UserForm setPassword(String password)
    {
        this.password = password;
        return this;
    }

    public User getUser()
    {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        return new User(email, encoder.encode(password));
    }
}
