package app.services.basket;

import app.data.basket.Basket;
import app.services.BaseRepository;
import org.springframework.stereotype.Repository;

/**
 * @author ngnmhieu
 * @since 17.06.16
 */
@Repository
public class BasketRepository extends BaseRepository<Basket, Long>
{
    @Override
    protected Class getEntityClass()
    {
        return Basket.class;
    }
}
