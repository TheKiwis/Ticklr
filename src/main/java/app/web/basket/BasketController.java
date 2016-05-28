package app.web.basket;

import app.data.*;
import app.services.BasketRepository;
import app.services.TicketSetRepository;
import app.services.BuyerRepository;
import app.web.authorization.IdentityAuthorizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.UUID;

/**
 * @author DucNguyenMinh
 * @since 08.03.16
 */
@RestController
public class BasketController
{
    public static final ResponseEntity NOT_FOUND = new ResponseEntity(HttpStatus.NOT_FOUND);

    protected BasketRepository basketRepository;

    protected BuyerRepository buyerRepository;

    protected TicketSetRepository ticketSetRepository;

    protected IdentityAuthorizer identityAuthorizer;

    protected BasketURI basketURI;

    @Autowired
    public BasketController(BasketRepository basketRepository, BuyerRepository buyerRepository,
                            TicketSetRepository ticketSetRepository, IdentityAuthorizer identityAuthorizer,
                            BasketURI basketURI)
    {
        this.basketRepository = basketRepository;
        this.buyerRepository = buyerRepository;
        this.ticketSetRepository = ticketSetRepository;
        this.identityAuthorizer = identityAuthorizer;
        this.basketURI = basketURI;
    }

    @RequestMapping(value = BasketURI.BASKET_URI, method = RequestMethod.GET)
    public ResponseEntity show(@PathVariable UUID buyerId)
    {
        Basket basket = null;

        HttpStatus status = HttpStatus.OK;

        Buyer buyer = buyerRepository.findById(buyerId);

        if (buyer == null)
            return NOT_FOUND;

        if (!identityAuthorizer.authorize(buyer.getIdentity()))
            return new ResponseEntity(HttpStatus.FORBIDDEN);

        basket = basketRepository.findByBuyerId(buyerId);

        if (basket == null) {
            basket = basketRepository.save(new Basket(buyer));
        }

        return new ResponseEntity(basket, status);
    }


    @RequestMapping(value = BasketURI.ITEMS_URI, method = RequestMethod.POST)
    public ResponseEntity addItem(@PathVariable UUID buyerId, @Valid BasketItemForm basketItemForm, BindingResult bindingResult)
    {
        Buyer buyer = buyerRepository.findById(buyerId);
        if (buyer == null) {
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }

        if (!identityAuthorizer.authorize(buyer.getIdentity())) return new ResponseEntity(HttpStatus.FORBIDDEN);

        if (bindingResult.hasFieldErrors()) {
            return new ResponseEntity(bindingResult.getFieldErrors(), HttpStatus.BAD_REQUEST);
        }

        TicketSet ticketSet = ticketSetRepository.findById(basketItemForm.getTicketSetId());

        if (ticketSet == null) {
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }


        HttpStatus status = HttpStatus.CREATED;

        Basket basket = basketRepository.findByBuyerId(buyerId);

        if (basket == null) { // if basket doesn't existed yet, then create new one
            basket = new Basket(buyer);
        }

        BasketItem item = basketRepository.findItemByBasketIdAndTicketSetId(basket.getId(), basketItemForm.getTicketSetId());

        if (item != null) {

            item.setQuantity(item.getQuantity() + basketItemForm.getQuantity());

            basketRepository.updateItem(item);
        } else {

            item = new BasketItem(basket, ticketSet, basketItemForm.getQuantity(), ticketSet.getPrice());

            basket.addItem(item);

            basketRepository.saveOrUpdate(basket);
        }

        return new ResponseEntity(status);
    }

    @RequestMapping(value = BasketURI.ITEM_URI, method = RequestMethod.DELETE)
    public ResponseEntity deleteItem(@PathVariable UUID buyerId, @PathVariable long itemId)
    {
        Buyer buyer = buyerRepository.findById(buyerId);

        if (buyer == null)
            return NOT_FOUND;

        if (!identityAuthorizer.authorize(buyer.getIdentity()))
            return new ResponseEntity(HttpStatus.FORBIDDEN);

        Basket basket = basketRepository.findByBuyerId(buyerId);

        BasketItem item = basketRepository.findItemById(itemId);

        if (item == null || basket == null || !basket.getItems().contains(item))
            return NOT_FOUND;

        basketRepository.deleteItem(item);

        return new ResponseEntity(HttpStatus.OK);
    }

    @RequestMapping(value = BasketURI.ITEM_URI, method = RequestMethod.PUT)
    public ResponseEntity updateItem(@PathVariable UUID buyerId, @PathVariable Long itemId, BasketItemUpdateForm basketItemUpdateForm, BindingResult bindingResult)
    {
        Buyer buyer = buyerRepository.findById(buyerId);

        if (buyer == null)
            return NOT_FOUND;

        if (!identityAuthorizer.authorize(buyer.getIdentity())) return new ResponseEntity(HttpStatus.FORBIDDEN);

        if (bindingResult.hasFieldErrors()) {
            return new ResponseEntity(bindingResult.getFieldErrors(), HttpStatus.BAD_REQUEST);
        }

        Basket basket = basketRepository.findByBuyerId(buyerId);

        BasketItem basketItem = basketRepository.findItemById(itemId);

        if (basket == null || basketItem == null) {
            return NOT_FOUND;
        }

        basketItem.setQuantity(basketItemUpdateForm.getQuantity());

        basketRepository.updateItem(basketItem);

        return new ResponseEntity(HttpStatus.OK);
    }
}
