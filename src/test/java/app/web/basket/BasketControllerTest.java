package app.web.basket;

import app.data.*;
import app.services.BasketService;
import app.services.TicketSetRepository;
import app.services.BuyerRepository;
import app.web.ResourceURI;
import app.web.authorization.IdentityAuthorizer;
import app.web.basket.responses.BasketResponse;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;

import java.util.ArrayList;
import java.util.UUID;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

/**
 * Created by DucNguyenMinh on 08.03.16.
 */
@RunWith(MockitoJUnitRunner.class)
public class BasketControllerTest
{
    @Mock
    private BuyerRepository buyerRepository;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private BasketService basketService;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private TicketSetRepository ticketSetRepository;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private IdentityAuthorizer identityAuthorizer;

    BasketController basketController;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    Basket mockBasket;


    Long ticketSetId = 456l;
    Long basketId = 1l;
    UUID buyerId = UUID.fromString("4eab8080-0f0e-11e6-9f74-0002a5d5c51b");
    long itemId = 5l;

    BasketItemForm basketItemForm = new BasketItemForm(10, ticketSetId);

    BasketItemUpdateForm basketItemUpdateForm = new BasketItemUpdateForm(30);

    @Mock
    BindingResult bindingResult;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    ResourceURI resURI;

    @Before
    public void setUp() throws Exception
    {
        basketController = new BasketController(basketService, buyerRepository, ticketSetRepository, identityAuthorizer, resURI);

        when(mockBasket.getId()).thenReturn(basketId);
        when(mockBasket.getItems()).thenReturn(new ArrayList<>());

        when(identityAuthorizer.authorize(any())).thenReturn(true);
    }

    @Test
    public void show_shouldBasketForBuyer()
    {
        when(buyerRepository.findById(buyerId)).thenReturn(mock(Buyer.class));
        when(basketService.findByBuyerId(buyerId)).thenReturn(mockBasket);

        ResponseEntity responseEntity = basketController.show(buyerId);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertTrue(responseEntity.getBody() instanceof BasketResponse);
    }

    @Test
    public void show_shouldCreateNewBasketWhenNotFound()
    {
        when(buyerRepository.findById(buyerId)).thenReturn(mock(Buyer.class));
        when(basketService.findByBuyerId(buyerId)).thenReturn(null);
        when(basketService.save(any())).thenReturn(mockBasket);

        ResponseEntity responseEntity = basketController.show(buyerId);

        verify(basketService, atLeastOnce()).save(any());

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }

    @Test
    public void show_shouldReturnHTTPStatusNotFound()
    {
        when(buyerRepository.findById(buyerId)).thenReturn(null);

        ResponseEntity responseEntity = basketController.show(buyerId);

        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
    }

    @Test
    public void updateItem_shouldUpdateItem() throws Exception
    {

        BasketItem mockItem = mock(BasketItem.class);

        when(buyerRepository.findById(buyerId)).thenReturn(mock(Buyer.class));
        when(basketService.findItemById(itemId)).thenReturn(mockItem);

        ResponseEntity response = basketController.updateItem(buyerId, itemId, basketItemUpdateForm, bindingResult);

        verify(basketService, times(1)).updateItem(any());

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void updateItem_shouldReturnNotFoundIfBuyerNotExist()
    {

        when(buyerRepository.findById(buyerId)).thenReturn(null);

        ResponseEntity response = basketController.updateItem(buyerId, itemId, basketItemUpdateForm, bindingResult);

        verify(basketService, never()).updateItem(any());
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }


    @Test
    public void updateItem_shouldReturnNotFoundIfBasketItemNotFound()
    {

        when(buyerRepository.findById(buyerId)).thenReturn(mock(Buyer.class));
        when(basketService.findItemById(anyLong())).thenReturn(null);

        ResponseEntity response = basketController.updateItem(buyerId, itemId, basketItemUpdateForm, bindingResult);

        verify(basketService, never()).updateItem(any());
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

}

