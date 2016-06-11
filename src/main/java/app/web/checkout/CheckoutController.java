package app.web.checkout;

import app.data.basket.Basket;
import app.data.user.Buyer;
import app.services.BuyerService;
import app.services.checkout.CheckoutService;
import app.web.authorization.IdentityAuthorizer;
import app.web.checkout.forms.PaypalInitForm;
import app.web.common.response.ErrorResponse;
import com.paypal.base.rest.PayPalRESTException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @author ngnmhieu
 * @since 11.06.16
 */
@RestController
public class CheckoutController
{
    private static final Logger log = LoggerFactory.getLogger(CheckoutController.class);

    // performs Checkout related use cases
    private CheckoutService checkoutService;

    // manages buyer's basket
    private final BuyerService buyerService;

    private static final String BASKET_IS_EMPTY = "BASKET_IS_EMPTY";

    private  static final ResponseEntity NOT_FOUND = new ResponseEntity(HttpStatus.NOT_FOUND);

    private static final ResponseEntity FORBIDDEN = new ResponseEntity(HttpStatus.FORBIDDEN);

    private IdentityAuthorizer identityAuthorizer;

    @Autowired
    public CheckoutController(CheckoutService checkoutService, BuyerService buyerService, IdentityAuthorizer identityAuthorizer)
    {
        this.checkoutService = checkoutService;
        this.buyerService = buyerService;
        this.identityAuthorizer = identityAuthorizer;
    }

    @RequestMapping(value = CheckoutURI.PAYPAL_INIT, method = RequestMethod.POST)
    public ResponseEntity initPaypalPayment(@PathVariable UUID buyerId,
                                            @Valid @RequestBody(required = false) PaypalInitForm paypalInitForm,
                                            BindingResult bindingResult)
    {
        Buyer buyer = buyerService.findById(buyerId);

        if (buyer == null)
            return NOT_FOUND;

        if (!identityAuthorizer.authorize(buyer.getIdentity()))
            return FORBIDDEN;

        if (bindingResult.hasErrors())
            return new ResponseEntity(new ErrorResponse(ErrorResponse.VALIDATION_ERROR), HttpStatus.BAD_REQUEST);

        Basket basket = buyer.getBasket();

        if (basket.isEmpty())
            return new ResponseEntity(new ErrorResponse(BASKET_IS_EMPTY), HttpStatus.BAD_REQUEST);

        String approvalUrl = null;
        try {
            approvalUrl = checkoutService.createPaypalPayment(basket, paypalInitForm.returnUrl, paypalInitForm.cancelUrl);
        } catch (PayPalRESTException e) {
            log.error("Paypal error: " + e.getMessage(), e);
            return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        Map<String, String> response = new HashMap<>();
        response.put("approvalUrl", approvalUrl);

        return new ResponseEntity(response, HttpStatus.CREATED);
    }
}
