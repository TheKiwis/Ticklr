package app.web.user;

import app.data.user.Buyer;
import app.data.user.Identity;
import app.data.user.User;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;

/**
 * @author ngnmhieu
 */
public class LoginForm
{
    @NotNull
    @Email(regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$")
    @Length(max=255)
    private String email;

    @NotNull
    @Length(min=8, max=255)
    private String password;

    public String getEmail()
    {
        return email;
    }

    public LoginForm setEmail(String email)
    {
        this.email = email;
        return this;
    }

    public String getPassword()
    {
        return password;
    }

    public LoginForm setPassword(String password)
    {
        this.password = password;
        return this;
    }

    public User getUser()
    {
        return new User(new Identity(email, password));
    }

    public Buyer getBuyer()
    {
        return new Buyer(new Identity(email, password));
    }

    public Identity getIdentity()
    {
        return new Identity(email, password);
    }
}
