package app.web;

import app.data.*;
import app.web.forms.BasketItemForm;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;

import java.math.BigDecimal;

import static  org.junit.Assert.*;
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

    @Mock
    private BasketRepository basketRepository;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private TicketSetRepository ticketSetRepository;

    BasketController basketController;

    @Before
    public void setUp() throws Exception
    {
        basketController = new BasketController(basketRepository, userRepository, ticketSetRepository);
    }

    @Test
    public void show_shouldBasketForUser()
    {
        Basket basket = mock(Basket.class);
        when(userRepository.findById(123l)).thenReturn(mock(User.class));
        when(basketRepository.findByUserId(123l)).thenReturn(basket);

        ResponseEntity responseEntity = basketController.show(123l);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(basket, responseEntity.getBody());
    }

    @Test
    public void show_shouldCreateNewBasketWhenNotFound()
    {
        Basket basket = mock(Basket.class);
        when(userRepository.findById(123l)).thenReturn(mock(User.class));
        when(basketRepository.findByUserId(123l)).thenReturn(null);
        when(basketRepository.save( any())).thenReturn(basket);

        ResponseEntity responseEntity = basketController.show(123l);

        verify(basketRepository,atLeastOnce()).save(any());

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(basket, responseEntity.getBody());
    }

    @Test
    public void show_shouldReturnHTTPStatusNotFound()
    {
        when(userRepository.findById(123l)).thenReturn(null);

        ResponseEntity responseEntity = basketController.show(123l);

        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
    }


    @Test
    public void addItem_shouldReturnHttpStatusCreated()
    {
        Long userId = 123l;
        Long ticketSetId = 456l;
        BasketItemForm basketItemForm = new BasketItemForm(10, ticketSetId);
        BindingResult bindingResult = mock(BindingResult.class);
        Basket mockBasket = mock(Basket.class);

        when(basketRepository.findByUserId(userId)).thenReturn(mockBasket);
        when(ticketSetRepository.findById(ticketSetId).getPrice()).thenReturn(new BigDecimal(25.00));
        when(userRepository.findById(userId)).thenReturn(mock(User.class));

        ResponseEntity responseEntity = basketController.addItem(123l, basketItemForm, bindingResult);

        verify(basketRepository, times(1)).save(mockBasket);
        verify(mockBasket, times(1)).addItem(any());
        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
    }

    @Test
    public void addItem_shouldCreateBasketIfNotExist()
    {
        Long userId = 123l;
        Long ticketSetId = 456l;
        BasketItemForm basketItemForm = new BasketItemForm(10, ticketSetId);
        BindingResult bindingResult = mock(BindingResult.class);

        when(basketRepository.findByUserId(userId)).thenReturn(null);
        when(ticketSetRepository.findById(ticketSetId).getPrice()).thenReturn(new BigDecimal(25.00));
        when(userRepository.findById(userId)).thenReturn(mock(User.class));

        basketController.addItem(123l, basketItemForm, bindingResult);

        verify(basketRepository, times(1)).save(any());
    }

    @Test
    public void addItem_shouldReturnHttpStatusBadRequestIfUserNotFound()
    {
        Long userId = 123l;
        Long ticketSetId = 456l;
        BasketItemForm basketItemForm = new BasketItemForm(10, ticketSetId);
        BindingResult bindingResult = mock(BindingResult.class);

        when(userRepository.findById(userId)).thenReturn(null);

        ResponseEntity responseEntity = basketController.addItem(123l, basketItemForm, bindingResult);

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
    }

    @Test
    public void addItem_shouldReturnHttpStatusBadRequestIfTicketSetNotFound()
    {

        Long userId = 123l;
        Long ticketSetId = 456l;
        BasketItemForm basketItemForm = new BasketItemForm(10, ticketSetId);
        BindingResult bindingResult = mock(BindingResult.class);

        when(userRepository.findById(userId)).thenReturn(mock(User.class));
        when(ticketSetRepository.findById(ticketSetId)).thenReturn(null);

        ResponseEntity responseEntity = basketController.addItem(123l, basketItemForm, bindingResult);

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());

    }
}

