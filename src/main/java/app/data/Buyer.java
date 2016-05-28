package app.data;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.UUID;

/**
 * @author ngnmhieu
 */
@Entity
@Table(name = "buyers")
public class Buyer
{
    @Id
    @Column(name = "id")
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    private UUID id;

    @OneToOne(optional = false)
    @JoinColumn(name = "identity_id")
    protected Identity identity;

    @OneToOne(mappedBy = "buyer", fetch = FetchType.EAGER)
    protected Basket basket;

    protected Buyer()
    {
    }

    public Buyer(UUID id, Identity identity)
    {
        this(identity);
        setId(id);
    }

    public Buyer(Identity identity)
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
