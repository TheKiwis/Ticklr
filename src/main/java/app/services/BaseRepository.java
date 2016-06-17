package app.services;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * @author ngnmhieu
 * @since 17.06.16
 */
@Repository
@Transactional
abstract public class BaseRepository<T, ID>
{
    @PersistenceContext
    private EntityManager em;

    /**
     * @param em manages ORM Entities
     */
    public BaseRepository(EntityManager em)
    {
        this.em = em;
    }

    protected BaseRepository()
    {
    }

    public T save(T entity)
    {
        return em.merge(entity);
    }

    /**
     * Finds an entity by ID
     *
     * @param id
     * @return null no entity found
     */
    public T findById(ID id)
    {
        return (T) em.find(getEntityClass(), id);
    }

    public void remove(T entity)
    {
        em.remove(entity);
    }

    /**
     * @return type of the entity
     */
    protected abstract Class getEntityClass();
}
