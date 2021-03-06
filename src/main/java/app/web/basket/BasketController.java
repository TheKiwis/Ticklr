package app.web.basket;

import app.data.basket.Basket;
import app.data.basket.BasketItem;
import app.data.event.TicketSet;
import app.data.user.Buyer;
import app.services.basket.BasketRepository;
import app.services.basket.BasketService;
import app.services.TicketSetRepository;
import app.services.BuyerService;
import app.web.ResourceURI;
import app.web.authorization.IdentityAuthorizer;
import app.web.basket.responses.BasketItemResponse;
import app.web.basket.responses.BasketResponse;
import app.web.common.response.ErrorCode;
import app.web.common.response.ErrorResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
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

    protected BasketRepository basketRepository;

    protected BasketService basketService;

    protected BuyerService buyerService;

    protected TicketSetRepository ticketSetRepository;

    protected IdentityAuthorizer identityAuthorizer;

    protected ResourceURI resURI;

    @Autowired
    public BasketController(BasketService basketService, BuyerService buyerService,
                            TicketSetRepository ticketSetRepository, IdentityAuthorizer identityAuthorizer,
                            ResourceURI resURI, BasketRepository basketRepository)
    {
        this.basketService = basketService;
        this.basketRepository = basketRepository;
        this.buyerService = buyerService;
        this.ticketSetRepository = ticketSetRepository;
        this.identityAuthorizer = identityAuthorizer;
        this.resURI = resURI;
    }

    @RequestMapping(value = BasketURI.BASKET_URI, method = RequestMethod.GET)
    public ResponseEntity showBasket(@PathVariable UUID buyerId)
    {
        Basket basket = null;

        HttpStatus status = HttpStatus.OK;

        Buyer buyer = buyerService.findById(buyerId);

        if (buyer == null)
            return NOT_FOUND;

        if (!identityAuthorizer.authorize(buyer.getIdentity()))
            return FORBIDDEN;

        basket = buyer.getBasket();

        if (basket == null) {
            basket = basketRepository.save(new Basket(buyer));
        }

        return new ResponseEntity(new BasketResponse(basket, resURI), status);
    }

    /**
     * Add new a item to the basket
     */
    @RequestMapping(value = BasketURI.ITEMS_URI, method = RequestMethod.POST)
    public ResponseEntity addItem(@PathVariable UUID buyerId, @Valid @RequestBody(required = false) BasketItemForm basketItemForm, BindingResult bindingResult)
    {
        Buyer buyer = buyerService.findById(buyerId);

        if (buyer == null)
            return new ResponseEntity(HttpStatus.NOT_FOUND);

        if (!identityAuthorizer.authorize(buyer.getIdentity()))
            return FORBIDDEN;

        if (basketItemForm == null || bindingResult.hasFieldErrors())
            return new ResponseEntity(new ErrorResponse(ErrorCode.VALIDATION_ERROR), HttpStatus.BAD_REQUEST);

        TicketSet ticketSet = ticketSetRepository.findById(basketItemForm.ticketSetId);
        if (ticketSet == null)
            return new ResponseEntity(HttpStatus.BAD_REQUEST);

        Basket basket = buyer.getBasket();

        // if basket doesn't existed yet, then create new one
        if (basket == null)
            basket = new Basket(buyer);

        BasketItem item = null;
        try {
            item = basketService.addItemToBasket(basket, ticketSet, basketItemForm.quantity);
        } catch (BasketService.TicketOutOfStockException e) {
            return new ResponseEntity(new ErrorResponse(ErrorCode.TICKET_SET_OUT_OF_STOCK), HttpStatus.BAD_REQUEST);
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(URI.create(resURI.getBasketURI().basketItemURL(buyerId, item.getId())));

        return new ResponseEntity(headers, HttpStatus.CREATED);
    }

    @RequestMapping(value = BasketURI.ITEM_URI, method = RequestMethod.GET)
    public ResponseEntity showItem(@PathVariable UUID buyerId, @PathVariable Long itemId)
    {
        Buyer buyer = buyerService.findById(buyerId);

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
        Buyer buyer = buyerService.findById(buyerId);

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
        Buyer buyer = buyerService.findById(buyerId);

        if (buyer == null)
            return NOT_FOUND;

        if (!identityAuthorizer.authorize(buyer.getIdentity()))
            return FORBIDDEN;

        if (basketItemUpdateForm == null || bindingResult.hasFieldErrors())
            return new ResponseEntity(new ErrorResponse(ErrorCode.VALIDATION_ERROR), HttpStatus.BAD_REQUEST);

        Basket basket = buyer.getBasket();

        BasketItem basketItem = basket.getItemFor(itemId);

        if (basket == null || basketItem == null)
            return NOT_FOUND;

        try {
            basketService.updateItemQuantity(basketItem, basketItemUpdateForm.quantity);
        } catch (BasketService.TicketOutOfStockException e) {
            return new ResponseEntity(new ErrorResponse(ErrorCode.TICKET_SET_OUT_OF_STOCK), HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity(HttpStatus.OK);
    }
}
