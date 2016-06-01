package app.web.basket;

import app.data.*;
import app.services.BasketService;
import app.services.TicketSetRepository;
import app.services.BuyerRepository;
import app.web.ResourceURI;
import app.web.authorization.IdentityAuthorizer;
import app.web.basket.responses.BasketItemResponse;
import app.web.basket.responses.BasketResponse;
import app.web.common.ErrorResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.util.Optional;
import java.util.UUID;

/**
 * @author DucNguyenMinh
 * @since 08.03.16
 */
@RestController
public class BasketController
{
    public static final ResponseEntity NOT_FOUND = new ResponseEntity(HttpStatus.NOT_FOUND);

    public static final ResponseEntity FORBIDDEN = new ResponseEntity(HttpStatus.FORBIDDEN);

    protected BasketService basketService;

    protected BuyerRepository buyerRepository;

    protected TicketSetRepository ticketSetRepository;

    protected IdentityAuthorizer identityAuthorizer;

    protected ResourceURI resURI;

    @Autowired
    public BasketController(BasketService basketService, BuyerRepository buyerRepository,
                            TicketSetRepository ticketSetRepository, IdentityAuthorizer identityAuthorizer,
                            ResourceURI resURI)
    {
        this.basketService = basketService;
        this.buyerRepository = buyerRepository;
        this.ticketSetRepository = ticketSetRepository;
        this.identityAuthorizer = identityAuthorizer;
        this.resURI = resURI;
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
            return FORBIDDEN;

        basket = buyer.getBasket();

        if (basket == null) {
            basket = basketService.save(new Basket(buyer));
        }

        return new ResponseEntity(new BasketResponse(basket, resURI), status);
    }


    /**
     * Add new a item to the basket
     */
    @RequestMapping(value = BasketURI.ITEMS_URI, method = RequestMethod.POST)
    public ResponseEntity addItem(@PathVariable UUID buyerId, @Valid @RequestBody(required = false) BasketItemForm basketItemForm, BindingResult bindingResult)
    {
        Buyer buyer = buyerRepository.findById(buyerId);

        if (buyer == null)
            return new ResponseEntity(HttpStatus.NOT_FOUND);

        if (!identityAuthorizer.authorize(buyer.getIdentity()))
            return FORBIDDEN;

        if (basketItemForm == null || bindingResult.hasFieldErrors())
            return new ResponseEntity(new ErrorResponse(ErrorResponse.VALIDATION_ERROR), HttpStatus.BAD_REQUEST);

        TicketSet ticketSet = ticketSetRepository.findById(basketItemForm.getTicketSetId());
        if (ticketSet == null)
            return new ResponseEntity(HttpStatus.BAD_REQUEST);

        Basket basket = buyer.getBasket();

        // if basket doesn't existed yet, then create new one
        if (basket == null)
            basket = new Basket(buyer);

        BasketItem item = basketService.addItemToBasket(basket, ticketSet, basketItemForm.getQuantity());

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(URI.create(resURI.getBasketURI().basketItemURL(buyerId, item.getId())));

        return new ResponseEntity(headers, HttpStatus.CREATED);
    }

    @RequestMapping(value = BasketURI.ITEM_URI, method = RequestMethod.GET)
    public ResponseEntity showItem(@PathVariable UUID buyerId, @PathVariable Long itemId)
    {
        Buyer buyer = buyerRepository.findById(buyerId);

        if (buyer == null)
            return NOT_FOUND;

        if (!identityAuthorizer.authorize(buyer.getIdentity()))
            return FORBIDDEN;

        Basket basket = buyer.getBasket();

        if (basket == null)
            return NOT_FOUND;

        BasketItem item = basket.getItemFor(itemId);

        if (item == null)
            return NOT_FOUND;

        return new ResponseEntity(new BasketItemResponse(basket, item, resURI), HttpStatus.OK);
    }

    /**
     * Remove an item from the basket
     */
    @RequestMapping(value = BasketURI.ITEM_URI, method = RequestMethod.DELETE)
    public ResponseEntity deleteItem(@PathVariable UUID buyerId, @PathVariable Long itemId)
    {
        Buyer buyer = buyerRepository.findById(buyerId);

        if (buyer == null)
            return NOT_FOUND;

        if (!identityAuthorizer.authorize(buyer.getIdentity()))
            return FORBIDDEN;

        Basket basket = buyer.getBasket();

        if (basket == null)
            return NOT_FOUND;

        BasketItem item = basket.getItemFor(itemId);
        if (item == null)
            return NOT_FOUND;

        basketService.removeItem(basket, item);

        return new ResponseEntity(HttpStatus.OK);
    }

    /**
     * Update basket item's quantity
     */
    @RequestMapping(value = BasketURI.ITEM_URI, method = RequestMethod.PUT)
    public ResponseEntity updateItem(@PathVariable UUID buyerId, @PathVariable Long itemId,
                                     @Valid @RequestBody(required = false) BasketItemUpdateForm basketItemUpdateForm,
                                     BindingResult bindingResult)
    {
        Buyer buyer = buyerRepository.findById(buyerId);

        if (buyer == null)
            return NOT_FOUND;

        if (!identityAuthorizer.authorize(buyer.getIdentity()))
            return FORBIDDEN;

        if (basketItemUpdateForm == null || bindingResult.hasFieldErrors())
            return new ResponseEntity(new ErrorResponse(ErrorResponse.VALIDATION_ERROR), HttpStatus.BAD_REQUEST);

        Basket basket = buyer.getBasket();

        BasketItem basketItem = basket.getItemFor(itemId);

        if (basket == null || basketItem == null)
            return NOT_FOUND;

        basketService.updateItemQuantity(basketItem, basketItemUpdateForm.quantity);

        return new ResponseEntity(HttpStatus.OK);
    }
}
