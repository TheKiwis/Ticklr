package app.web;

import app.data.*;
import app.web.forms.BasketItemForm;
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

/**
 * Created by DucNguyenMinh on 08.03.16.
 */
@RestController
@RequestMapping("/users/{userId}/basket")
public class BasketController
{
    protected BasketRepository basketRepository;

    protected UserRepository userRepository;

    protected TicketSetRepository ticketSetRepository;

    @Autowired
    public BasketController(BasketRepository basketRepository, UserRepository userRepository, TicketSetRepository ticketSetRepository)
    {
        this.basketRepository = basketRepository;
        this.userRepository = userRepository;
        this.ticketSetRepository = ticketSetRepository;
    }

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity show(@PathVariable Long userId)
    {
        Basket basket = null;

        HttpStatus status = HttpStatus.OK;

        User user = userRepository.findById(userId);

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


    // todo validation
    // todo do not create new item if there's already an item with the same ticket_set_id in basket
    // todo use saveOrUpdate to save/update basket (persist is only for new entity) more info read: https://dzone.com/articles/saving_detatched_entities
    @RequestMapping(value = "/items", method = RequestMethod.POST)
    public ResponseEntity addItem(@PathVariable Long userId, BasketItemForm basketItemForm, BindingResult bindingResult)
    {
        User user = userRepository.findById(userId);
        if (user == null) {
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }

        TicketSet ticketSet = ticketSetRepository.findById(basketItemForm.getTicketSetId());

        if (ticketSet == null) {
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }

        HttpStatus status = HttpStatus.CREATED;

        Basket basket = basketRepository.findByUserId(userId);

        BasketItem item = new BasketItem(basket, ticketSet, basketItemForm.getQuantity(), ticketSet.getPrice());

        if (basket == null) { // if basket doesn't existed yet, then create new one
            basket = new Basket(user);
        }

        basket.addItem(item);

        basketRepository.saveOrUpdate(basket);

        return new ResponseEntity(null, status);
    }
}
