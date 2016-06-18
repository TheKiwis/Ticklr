package app.services.checkout;

import app.data.basket.Basket;
import app.data.checkout.PaypalPayment;
import app.services.basket.BasketService;
import com.paypal.api.payments.*;
import com.paypal.base.rest.APIContext;
import com.paypal.base.rest.OAuthTokenCredential;
import com.paypal.base.rest.PayPalRESTException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.stream.Collectors;

/**
 * @author ngnmhieu
 * @since 11.06.16
 */
@Service
public class PaypalService
{
    // contains configurations for Paypal API
    private PaypalConfiguration paypalConfig;

    // manages persistent payment objects
    private PaypalPaymentRepository paymentRepository;

    // manages the basket
    private BasketService basketService;

    private static final String CURRENCY = "EUR";

    /**
     * Construct an instance of EventRepository
     *
     * @param basketService manges basket
     */
    @Autowired
    public PaypalService(PaypalPaymentRepository paymentRepository, BasketService basketService, PaypalConfiguration paypalConfig)
    {
        Assert.notNull(basketService);
        Assert.notNull(paypalConfig);

        this.basketService = basketService;
        this.paypalConfig = paypalConfig;
        this.paymentRepository = paymentRepository;

        registerObservers();
    }

    private void registerObservers()
    {
        // removes paypal payment on changes on the basket
        basketService.addObserver(new Observer()
        {
            @Override
            public void update(Observable o, Object arg)
            {
                if (arg instanceof Basket) {
                    Basket basket = (Basket) arg;
                    paymentRepository.remove(basket.getId());
                }
            }
        });
    }

    /**
     * @param returnUrl
     * @param cancelUrl
     * @return Paypal Approval URL, where buyer confirms the transaction
     * @throws IllegalArgumentException if basket.isEmpty()
     */
    public String createPaypalPayment(Basket basket, String returnUrl, String cancelUrl) throws PayPalRESTException
    {
        if (basket.isEmpty())
            throw new IllegalArgumentException("Basket should not be empty.");

        APIContext context = null;

        context = createAPIContext();

        String approvalUrl = null;
        Payment payment = createPayment();

        Payer payer = new Payer();
        payer.setPaymentMethod("paypal");

        // ## payment intent and payer
        payment.setIntent("sale");
        payment.setPayer(payer);

        // ## client Redirect URLs
        RedirectUrls redirectUrls = new RedirectUrls();
        redirectUrls.setReturnUrl(returnUrl);
        redirectUrls.setCancelUrl(cancelUrl);
        payment.setRedirectUrls(redirectUrls);

        // ## Transaction
        Transaction transaction = new Transaction();
        transaction.setDescription("Thank you for purchasing tickets at Ticklr.com!");

        // Amount
        Details details = new Details();
        details.setSubtotal(formattedNumber(basket.getTotalPrice()));
        Amount amount = new Amount(CURRENCY, formattedNumber(basket.getTotalPrice()));
        amount.setDetails(details);
        transaction.setAmount(amount);

        // collect items from basket
        List<Item> items = basket.getItems().stream()
                .map(item -> new Item(item.getTicketSet().getTitle(), item.getQuantity().toString(), formattedNumber(item.getUnitPrice()), CURRENCY))
                .collect(Collectors.toList());

        ItemList itemList = new ItemList();
        itemList.setItems(items);
        transaction.setItemList(itemList);
        payment.setTransactions(Collections.singletonList(transaction));

        Payment createdPayment = payment.create(context);

        List<Links> links = createdPayment.getLinks();
        for (Links link : links) {
            if (link.getRel().equalsIgnoreCase("approval_url")) {
                approvalUrl = link.getHref();
                break;
            }
        }

        paymentRepository.save(new PaypalPayment(createdPayment.getId(), basket));

        return approvalUrl;
    }

    /**
     * @param basket
     * @param payerId returned by Paypal after the buyer has approved the payment
     * @throws NoPaymentException if no payment associated with the basket found
     * @throws PayPalRESTException when something goes wrong with the communication with Paypal
     */
    public Payment executePayment(Basket basket, String payerId) throws PayPalRESTException
    {
        PaypalPayment p = paymentRepository.find(basket.getId());

        if (p == null)
            throw new NoPaymentException();

        Payment payment = new Payment();
        payment.setId(p.getPaymentId());

        PaymentExecution paymentExecution = new PaymentExecution();
        paymentExecution.setPayerId(payerId);

        Payment createdPayment = payment.execute(createAPIContext(), paymentExecution);

        return createdPayment;
    }

    /**
     * @return formatted BigDecimal number. E.g: 210.50
     */
    private String formattedNumber(BigDecimal num)
    {
        return num.setScale(2).toPlainString();
    }

    /**
     * Factory method creating Paypal Payment object
     *
     * @return a new Payment object
     */
    protected APIContext createAPIContext() throws PayPalRESTException
    {
        APIContext context = new APIContext(new OAuthTokenCredential(paypalConfig.getClientID(), paypalConfig.getClientSecret(), paypalConfig).getAccessToken());
        context.setConfigurationMap(paypalConfig);
        return context;
    }

    /**
     * Factory method creating Payment object
     *
     * @return a new Payment object
     */
    protected Payment createPayment()
    {
        return new Payment();
    }

    /**
     * No Paypal payment was created before a payment is executed
     */
    public static class NoPaymentException extends RuntimeException
    {

    }
}