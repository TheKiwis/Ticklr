package app.data.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.UUID;

/**
 * @author ngnmhieu
 * @since 21.05.16
 */
@Entity
@Table(name = "identities")
public class Identity
{
    @Id
    @Column(name = "id")
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    private UUID id;

    @Column(name = "email", unique = true)
    private String email;

    @Column(name = "password")
    private String password;

    protected Identity()
    {
    }

    public Identity(String email, String password)
    {
        setEmail(email);
        setPassword(password);
    }

    public Identity(UUID id, String email, String password)
    {
        this(email, password);
        setId(id);
    }

    public UUID getId()
    {
        return id;
    }

    public void setId(UUID id)
    {
        this.id = id;
    }

    public String getEmail()
    {
        return email;
    }

    public void setEmail(String email)
    {
        this.email = email;
    }

    @JsonIgnore
    public String getPassword()
    {
        return password;
    }

    public void setPassword(String password)
    {
        this.password = password;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Identity identity = (Identity) o;

        if (id != null ? !id.equals(identity.id) : identity.id != null) return false;
        return email.equals(identity.email);

    }
}
