package app.services.basket;

import app.data.basket.Basket;
import app.data.basket.BasketItem;
import app.data.event.TicketSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import javax.persistence.*;
import java.util.Observable;

/**
 * @author DucNguyenMinh
 * @since 08.03.16
 */
@Repository
public class BasketService extends Observable
{
    private BasketRepository basketRepository;

    @Autowired
    public BasketService(BasketRepository basketRepository)
    {
        this.basketRepository = basketRepository;
    }

    /**
     * Add a new item corresponding to the given ticket set to the basket
     * If the item is already there, then increment the quantity
     *
     * @param basket
     * @param ticketSet
     * @param quantity
     * @return the new BasketItem
     * @throws IllegalArgumentException if quantity > 0
     * @throws TicketOutOfStockException if ticketSet.getStock() < quantity
     * @ensure item.getTicketSet() == ticketSet
     * @ensure item.getUnitPrice().equals(ticketSet.getPrice())
     * @ensure item.getQuantity() == (basket.isInBasket(ticketSet) ? basket.getItem(ticketSet).getQuantity() + quantity : quantity)
     */
    public BasketItem addItemToBasket(Basket basket, TicketSet ticketSet, int quantity)
    {
        Assert.notNull(basket);
        Assert.notNull(ticketSet);
        Assert.isTrue(quantity > 0);

        if (ticketSet.getStock() < quantity)
            throw new TicketOutOfStockException();

        BasketItem item;
        if (basket.isInBasket(ticketSet)) {
            item = basket.getItemFor(ticketSet);
            item.setQuantity(item.getQuantity() + quantity);
        } else {
            item = new BasketItem(ticketSet, quantity, ticketSet.getPrice());
            basket.addItem(item);
        }

        basket = basketRepository.save(basket);

        notifyBasketChanges(basket);

        return basket.getItemFor(ticketSet);
    }

    /**
     * Update quantity of a BasketItem
     *
     * @param item
     * @param quantity
     * @throws IllegalArgumentException if quantity > 0
     * @throws IllegalArgumentException if item == null
     * @throws TicketOutOfStockException quantity exceeds the number of available ticket sets
     */
    public void updateItemQuantity(BasketItem item, int quantity)
    {
        Assert.notNull(item);
        Assert.isTrue(quantity > 0);

        if (item.getTicketSet().getStock() < quantity)
            throw new TicketOutOfStockException();

        item.setQuantity(quantity);

        basketRepository.save(item.getBasket());

        notifyBasketChanges(item.getBasket());
    }

    /**
     * @param basket
     * @param item
     * @throws IllegalArgumentException !basket.isInBasket(item)
     * @ensures false == basket.isInBasket(item)
     */
    public void removeItem(Basket basket, BasketItem item)
    {
        Assert.isTrue(basket.isInBasket(item));

        basket.removeItem(item);

        basketRepository.save(basket);

        notifyBasketChanges(basket);
    }

    /**
     * Clears the basket, removes all items in the basket
     * @param basket
     * @return true if basket is successfully cleared
     */
    public void clearBasket(Basket basket)
    {
        basket.clear();
        basketRepository.save(basket);
    }

    /**
     * Notifies the observer and pass basket as argument
     * @param basket
     */
    private void notifyBasketChanges(Basket basket)
    {
        setChanged();
        notifyObservers(basket);
    }

    public class TicketOutOfStockException extends RuntimeException
    {

    }
}
