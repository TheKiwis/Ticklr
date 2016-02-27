package app.web.forms;

import app.data.User;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.Length;

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

    public void setEmail(String email)
    {
        this.email = email;
    }

    public String getPassword()
    {
        return password;
    }

    public void setPassword(String password)
    {
        this.password = password;
    }

    public User getUser()
    {
        return new User(email, password);
    }
}
