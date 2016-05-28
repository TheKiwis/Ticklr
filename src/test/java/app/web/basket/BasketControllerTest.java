package app.web.basket;

import app.data.*;
import app.services.BasketRepository;
import app.services.TicketSetRepository;
import app.services.BuyerRepository;
import app.web.authorization.IdentityAuthorizer;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;

import java.math.BigDecimal;
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
    private BasketRepository basketRepository;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private TicketSetRepository ticketSetRepository;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private IdentityAuthorizer identityAuthorizer;

    BasketController basketController;

    @Mock
    Basket mockBasket;


    Long ticketSetId = 456l;
    Long basketId = 1l;
    UUID buyerId = UUID.fromString("4eab8080-0f0e-11e6-9f74-0002a5d5c51b");
    long itemId = 5l;

    BasketItemForm basketItemForm = new BasketItemForm(10, ticketSetId);

    BasketItemUpdateForm basketItemUpdateForm = new BasketItemUpdateForm(30);
    @Mock
    BindingResult bindingResult;

    @Before
    public void setUp() throws Exception
    {
        basketController = new BasketController(basketRepository, buyerRepository, ticketSetRepository, identityAuthorizer, new BasketURI("http://localhost"));

        when(mockBasket.getId()).thenReturn(basketId);

        when(identityAuthorizer.authorize(any())).thenReturn(true);
    }

    @Test
    public void show_shouldBasketForBuyer()
    {
        when(buyerRepository.findById(buyerId)).thenReturn(mock(Buyer.class));
        when(basketRepository.findByBuyerId(buyerId)).thenReturn(mockBasket);

        ResponseEntity responseEntity = basketController.show(buyerId);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(mockBasket, responseEntity.getBody());
    }

    @Test
    public void show_shouldCreateNewBasketWhenNotFound()
    {
        when(buyerRepository.findById(buyerId)).thenReturn(mock(Buyer.class));
        when(basketRepository.findByBuyerId(buyerId)).thenReturn(null);
        when(basketRepository.save(any())).thenReturn(mockBasket);

        ResponseEntity responseEntity = basketController.show(buyerId);

        verify(basketRepository, atLeastOnce()).save(any());

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(mockBasket, responseEntity.getBody());
    }

    @Test
    public void show_shouldReturnHTTPStatusNotFound()
    {
        when(buyerRepository.findById(buyerId)).thenReturn(null);

        ResponseEntity responseEntity = basketController.show(buyerId);

        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
    }


    @Test
    public void addItem_shouldReturnHttpStatusCreated()
    {
        when(basketRepository.findByBuyerId(buyerId)).thenReturn(mockBasket);

        when(basketRepository.findItemByBasketIdAndTicketSetId(basketId, ticketSetId)).thenReturn(null);

        when(ticketSetRepository.findById(ticketSetId).getPrice()).thenReturn(new BigDecimal(25.00));

        when(buyerRepository.findById(buyerId)).thenReturn(mock(Buyer.class));

        ResponseEntity responseEntity = basketController.addItem(buyerId, basketItemForm, bindingResult);

        verify(basketRepository, times(1)).saveOrUpdate(mockBasket);
        verify(mockBasket, times(1)).addItem(any());
        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
    }

    @Test
    public void addItem_shouldCreateBasketIfNotExist()
    {
        when(basketRepository.findByBuyerId(buyerId)).thenReturn(null);
        when(basketRepository.findItemByBasketIdAndTicketSetId(null, ticketSetId)).thenReturn(null);

        when(ticketSetRepository.findById(ticketSetId).getPrice()).thenReturn(new BigDecimal(25.00));
        when(buyerRepository.findById(buyerId)).thenReturn(mock(Buyer.class));

        basketController.addItem(buyerId, basketItemForm, bindingResult);

        verify(basketRepository, times(1)).saveOrUpdate(any());
    }

    @Test
    public void addItem_shouldReturnHttpStatusBadRequestIfBuyerNotFound()
    {
        when(buyerRepository.findById(buyerId)).thenReturn(null);

        ResponseEntity responseEntity = basketController.addItem(buyerId, basketItemForm, bindingResult);

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
    }

    @Test
    public void addItem_shouldReturnHttpStatusBadRequestIfTicketSetNotFound()
    {
        when(buyerRepository.findById(buyerId)).thenReturn(mock(Buyer.class));
        when(ticketSetRepository.findById(ticketSetId)).thenReturn(null);

        ResponseEntity responseEntity = basketController.addItem(buyerId, basketItemForm, bindingResult);

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
    }

    @Test
    public void addItem_shouldIncrementBasketItemIfBasketItemWithTheSameTicketSetID() throws Exception
    {
        int itemQuantity = 2;
        BasketItem mockItem = mock(BasketItem.class);
        when(mockItem.getQuantity()).thenReturn(itemQuantity);

        when(buyerRepository.findById(buyerId)).thenReturn(mock(Buyer.class));
        when(basketRepository.findByBuyerId(buyerId).getId()).thenReturn(basketId);
        when(basketRepository.findItemByBasketIdAndTicketSetId(basketId, ticketSetId)).thenReturn(mockItem);


        basketController.addItem(buyerId, basketItemForm, bindingResult);
        verify(mockItem, times(1)).setQuantity(itemQuantity + basketItemForm.getQuantity());
        verify(mockBasket, never()).addItem(any());
        verify(basketRepository, times(1)).updateItem(mockItem);
    }

    @Test
    public void deleteItem_shouldDeleteItem() throws Exception
    {
        BasketItem mockItem = mock(BasketItem.class);
        when(buyerRepository.findById(buyerId)).thenReturn(mock(Buyer.class));
        when(basketRepository.findItemById(itemId)).thenReturn(mockItem);
        when(basketRepository.findByBuyerId(buyerId).getItems().contains(mockItem)).thenReturn(true);

        ResponseEntity response = basketController.deleteItem(buyerId, itemId);

        verify(basketRepository, times(1)).deleteItem(mockItem);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void deleteItem_shouldReturnNotFoundIfNoBasketItemFound() throws Exception
    {
        when(basketRepository.findItemById(itemId)).thenReturn(null);

        ResponseEntity response = basketController.deleteItem(buyerId, itemId);

        verify(basketRepository, never()).deleteItem(any());

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void deleteItem_shouldReturnNotFoundIfItemDoesNotBelongToBasket() throws Exception
    {
        BasketItem mockItem = mock(BasketItem.class);

        when(basketRepository.findItemById(itemId)).thenReturn(mockItem);

        when(basketRepository.findByBuyerId(buyerId).getItems().contains(mockItem)).thenReturn(false);

        ResponseEntity response = basketController.deleteItem(buyerId, itemId);

        verify(basketRepository, never()).deleteItem(any());

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void deleteItem_shouldReturnNotFoundIfBasketDoesNotExist() throws Exception
    {
        when(basketRepository.findByBuyerId(buyerId)).thenReturn(null);

        ResponseEntity response = basketController.deleteItem(buyerId, itemId);

        verify(basketRepository, never()).deleteItem(any());

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void updateItem_shouldUpdateItem() throws Exception
    {

        BasketItem mockItem = mock(BasketItem.class);

        when(buyerRepository.findById(buyerId)).thenReturn(mock(Buyer.class));
        when(basketRepository.findItemById(itemId)).thenReturn(mockItem);

        ResponseEntity response = basketController.updateItem(buyerId, itemId, basketItemUpdateForm, bindingResult);

        verify(basketRepository, times(1)).updateItem(any());

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void updateItem_shouldReturnNotFoundIfBuyerNotExist()
    {

        when(buyerRepository.findById(buyerId)).thenReturn(null);

        ResponseEntity response = basketController.updateItem(buyerId, itemId, basketItemUpdateForm, bindingResult);

        verify(basketRepository, never()).updateItem(any());
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }


    @Test
    public void updateItem_shouldReturnNotFoundIfBasketItemNotFound()
    {

        when(buyerRepository.findById(buyerId)).thenReturn(mock(Buyer.class));
        when(basketRepository.findItemById(anyLong())).thenReturn(null);

        ResponseEntity response = basketController.updateItem(buyerId, itemId, basketItemUpdateForm, bindingResult);

        verify(basketRepository, never()).updateItem(any());
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

}

