package app.data;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.mapping.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.persistence.*;
import javax.persistence.Column;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import java.util.UUID;

/**
 * @author ngnmhieu
 */
@Entity
@Table(name = "users")
public class User
{
    @Id
    @Column(name = "id")
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    private UUID id;

    @OneToOne(optional = false)
    @JoinColumn(name = "identity_id")
    protected Identity identity;

    @OneToOne(mappedBy = "user", fetch = FetchType.EAGER)
    protected Basket basket;

    protected User()
    {
    }

    public User(UUID id, Identity identity)
    {
        this(identity);
        setId(id);
    }

    public User(Identity identity)
    {
        setIdentity(identity);
    }

    public UUID getId()
    {
        return id;
    }

    public void setId(UUID id)
    {
        this.id = id;
    }

    /**
     * @return Identity that uniquely identifies this User
     */
    public Identity getIdentity()
    {
        return identity;
    }

    public void setIdentity(Identity identity)
    {
        this.identity = identity;
    }
}
