package app.web.basket;

import app.data.*;
import app.services.BasketRepository;
import app.services.TicketSetRepository;
import app.services.UserRepository;
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
    private UserRepository userRepository;

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
    UUID userId = UUID.fromString("4eab8080-0f0e-11e6-9f74-0002a5d5c51b");
    long itemId = 5l;

    BasketItemForm basketItemForm = new BasketItemForm(10, ticketSetId);

    BasketItemUpdateForm basketItemUpdateForm = new BasketItemUpdateForm(30);
    @Mock
    BindingResult bindingResult;

    @Before
    public void setUp() throws Exception
    {
        basketController = new BasketController(basketRepository, userRepository, ticketSetRepository, identityAuthorizer, new BasketURI("http://localhost"));

        when(mockBasket.getId()).thenReturn(basketId);

        when(identityAuthorizer.authorize(any())).thenReturn(true);
    }

    @Test
    public void show_shouldBasketForUser()
    {
        when(userRepository.findById(userId)).thenReturn(mock(User.class));
        when(basketRepository.findByUserId(userId)).thenReturn(mockBasket);

        ResponseEntity responseEntity = basketController.show(userId);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(mockBasket, responseEntity.getBody());
    }

    @Test
    public void show_shouldCreateNewBasketWhenNotFound()
    {
        when(userRepository.findById(userId)).thenReturn(mock(User.class));
        when(basketRepository.findByUserId(userId)).thenReturn(null);
        when(basketRepository.save(any())).thenReturn(mockBasket);

        ResponseEntity responseEntity = basketController.show(userId);

        verify(basketRepository, atLeastOnce()).save(any());

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(mockBasket, responseEntity.getBody());
    }

    @Test
    public void show_shouldReturnHTTPStatusNotFound()
    {
        when(userRepository.findById(userId)).thenReturn(null);

        ResponseEntity responseEntity = basketController.show(userId);

        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
    }


    @Test
    public void addItem_shouldReturnHttpStatusCreated()
    {
        when(basketRepository.findByUserId(userId)).thenReturn(mockBasket);

        when(basketRepository.findItemByBasketIdAndTicketSetId(basketId, ticketSetId)).thenReturn(null);

        when(ticketSetRepository.findById(ticketSetId).getPrice()).thenReturn(new BigDecimal(25.00));

        when(userRepository.findById(userId)).thenReturn(mock(User.class));

        ResponseEntity responseEntity = basketController.addItem(userId, basketItemForm, bindingResult);

        verify(basketRepository, times(1)).saveOrUpdate(mockBasket);
        verify(mockBasket, times(1)).addItem(any());
        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
    }

    @Test
    public void addItem_shouldCreateBasketIfNotExist()
    {
        when(basketRepository.findByUserId(userId)).thenReturn(null);
        when(basketRepository.findItemByBasketIdAndTicketSetId(null, ticketSetId)).thenReturn(null);

        when(ticketSetRepository.findById(ticketSetId).getPrice()).thenReturn(new BigDecimal(25.00));
        when(userRepository.findById(userId)).thenReturn(mock(User.class));

        basketController.addItem(userId, basketItemForm, bindingResult);

        verify(basketRepository, times(1)).saveOrUpdate(any());
    }

    @Test
    public void addItem_shouldReturnHttpStatusBadRequestIfUserNotFound()
    {
        when(userRepository.findById(userId)).thenReturn(null);

        ResponseEntity responseEntity = basketController.addItem(userId, basketItemForm, bindingResult);

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
    }

    @Test
    public void addItem_shouldReturnHttpStatusBadRequestIfTicketSetNotFound()
    {
        when(userRepository.findById(userId)).thenReturn(mock(User.class));
        when(ticketSetRepository.findById(ticketSetId)).thenReturn(null);

        ResponseEntity responseEntity = basketController.addItem(userId, basketItemForm, bindingResult);

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
    }

    @Test
    public void addItem_shouldIncrementBasketItemIfBasketItemWithTheSameTicketSetID() throws Exception
    {
        int itemQuantity = 2;
        BasketItem mockItem = mock(BasketItem.class);
        when(mockItem.getQuantity()).thenReturn(itemQuantity);

        when(userRepository.findById(userId)).thenReturn(mock(User.class));
        when(basketRepository.findByUserId(userId).getId()).thenReturn(basketId);
        when(basketRepository.findItemByBasketIdAndTicketSetId(basketId, ticketSetId)).thenReturn(mockItem);


        basketController.addItem(userId, basketItemForm, bindingResult);
        verify(mockItem, times(1)).setQuantity(itemQuantity + basketItemForm.getQuantity());
        verify(mockBasket, never()).addItem(any());
        verify(basketRepository, times(1)).updateItem(mockItem);
    }

    @Test
    public void deleteItem_shouldDeleteItem() throws Exception
    {
        BasketItem mockItem = mock(BasketItem.class);
        when(userRepository.findById(userId)).thenReturn(mock(User.class));
        when(basketRepository.findItemById(itemId)).thenReturn(mockItem);
        when(basketRepository.findByUserId(userId).getBasketItems().contains(mockItem)).thenReturn(true);

        ResponseEntity response = basketController.deleteItem(userId, itemId);

        verify(basketRepository, times(1)).deleteItem(mockItem);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void deleteItem_shouldReturnNotFoundIfNoBasketItemFound() throws Exception
    {
        when(basketRepository.findItemById(itemId)).thenReturn(null);

        ResponseEntity response = basketController.deleteItem(userId, itemId);

        verify(basketRepository, never()).deleteItem(any());

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void deleteItem_shouldReturnNotFoundIfItemDoesNotBelongToBasket() throws Exception
    {
        BasketItem mockItem = mock(BasketItem.class);

        when(basketRepository.findItemById(itemId)).thenReturn(mockItem);

        when(basketRepository.findByUserId(userId).getBasketItems().contains(mockItem)).thenReturn(false);

        ResponseEntity response = basketController.deleteItem(userId, itemId);

        verify(basketRepository, never()).deleteItem(any());

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void deleteItem_shouldReturnNotFoundIfBasketDoesNotExist() throws Exception
    {
        when(basketRepository.findByUserId(userId)).thenReturn(null);

        ResponseEntity response = basketController.deleteItem(userId, itemId);

        verify(basketRepository, never()).deleteItem(any());

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void updateItem_shouldUpdateItem() throws Exception
    {

        BasketItem mockItem = mock(BasketItem.class);

        when(userRepository.findById(userId)).thenReturn(mock(User.class));
        when(basketRepository.findItemById(itemId)).thenReturn(mockItem);

        ResponseEntity response = basketController.updateItem(userId, itemId, basketItemUpdateForm, bindingResult);

        verify(basketRepository, times(1)).updateItem(any());

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void updateItem_shouldReturnNotFoundIfUserNotExist()
    {

        when(userRepository.findById(userId)).thenReturn(null);

        ResponseEntity response = basketController.updateItem(userId, itemId, basketItemUpdateForm, bindingResult);

        verify(basketRepository, never()).updateItem(any());
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }


    @Test
    public void updateItem_shouldReturnNotFoundIfBasketItemNotFound()
    {

        when(userRepository.findById(userId)).thenReturn(mock(User.class));
        when(basketRepository.findItemById(anyLong())).thenReturn(null);

        ResponseEntity response = basketController.updateItem(userId, itemId, basketItemUpdateForm, bindingResult);

        verify(basketRepository, never()).updateItem(any());
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

}

