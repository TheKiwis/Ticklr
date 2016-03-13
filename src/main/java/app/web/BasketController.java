package app.web;

import app.data.*;
import app.web.authorization.UserAuthorizer;
import app.web.forms.BasketItemForm;
import app.web.forms.BasketItemUpdateForm;
import javafx.geometry.Pos;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * @author DucNguyenMinh
 * @since 08.03.16
 */
@RestController
@RequestMapping("/users/{userId}/basket")
public class BasketController
{
    protected BasketRepository basketRepository;

    protected UserRepository userRepository;

    protected TicketSetRepository ticketSetRepository;

    protected UserAuthorizer userAuthorizer;

    @Autowired
    public BasketController(BasketRepository basketRepository, UserRepository userRepository, TicketSetRepository ticketSetRepository, UserAuthorizer userAuthorizer)
    {
        this.basketRepository = basketRepository;
        this.userRepository = userRepository;
        this.ticketSetRepository = ticketSetRepository;
        this.userAuthorizer = userAuthorizer;
    }

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity show(@PathVariable Long userId)
    {
        Basket basket = null;

        HttpStatus status = HttpStatus.OK;

        User user = userRepository.findById(userId);

        if( !userAuthorizer.authorize(user)) return new ResponseEntity(HttpStatus.FORBIDDEN);

        if (user != null) {
            basket = basketRepository.findByUserId(userId);

            if (basket == null) {
                basket = basketRepository.save(new Basket(user));
            }
        } else {
            status = HttpStatus.NOT_FOUND;
        }

        return new ResponseEntity(basket, status);
    }


    @RequestMapping(value = "/items", method = RequestMethod.POST)
    public ResponseEntity addItem(@PathVariable Long userId, @Valid BasketItemForm basketItemForm, BindingResult bindingResult)
    {


        User user = userRepository.findById(userId);
        if (user == null) {
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }

        if( !userAuthorizer.authorize(user)) return new ResponseEntity(HttpStatus.FORBIDDEN);

        if(bindingResult.hasFieldErrors()){
            return new ResponseEntity(bindingResult.getFieldErrors(), HttpStatus.BAD_REQUEST );
        }

        TicketSet ticketSet = ticketSetRepository.findById(basketItemForm.getTicketSetId());

        if (ticketSet == null) {
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }


        HttpStatus status = HttpStatus.CREATED;

        Basket basket = basketRepository.findByUserId(userId);

        if (basket == null) { // if basket doesn't existed yet, then create new one
            basket = new Basket(user);
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

    @RequestMapping(value = "/items/{itemId}", method = RequestMethod.DELETE)
    public ResponseEntity deleteItem(@PathVariable Long userId, @PathVariable long itemId)
    {

        User user = userRepository.findById(userId);
        if( !userAuthorizer.authorize(user)) return new ResponseEntity(HttpStatus.FORBIDDEN);


        Basket basket = basketRepository.findByUserId(userId);

        BasketItem item = basketRepository.findItemById(itemId);

        if (item == null || basket == null || !basket.getBasketItems().contains(item))
            return new ResponseEntity(HttpStatus.NOT_FOUND);

        basketRepository.deleteItem(item);

        return new ResponseEntity(HttpStatus.OK);
    }

    @RequestMapping(value = "/items/{itemId}", method = RequestMethod.PUT)
    public ResponseEntity updateItem(@ PathVariable Long userId, @PathVariable Long itemId, BasketItemUpdateForm basketItemUpdateForm, BindingResult bindingResult)
    {

        User user = userRepository.findById(userId);
        if(user == null)
            return new ResponseEntity(HttpStatus.NOT_FOUND);

        if( !userAuthorizer.authorize(user)) return new ResponseEntity(HttpStatus.FORBIDDEN);

        if(bindingResult.hasFieldErrors()){
            return new ResponseEntity(bindingResult.getFieldErrors(), HttpStatus.BAD_REQUEST);
        }

        Basket basket = basketRepository.findByUserId(userId);

        BasketItem basketItem = basketRepository.findItemById(itemId);

        if (user == null ||basket == null || basketItem == null ) {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }

        basketItem.setQuantity(basketItemUpdateForm.getQuantity());

        basketRepository.updateItem(basketItem);

        return new ResponseEntity(HttpStatus.OK);
    }
}
