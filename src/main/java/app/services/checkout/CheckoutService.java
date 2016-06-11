package app.services.checkout;

import app.data.basket.Basket;
import app.services.BasketService;
import com.paypal.api.payments.*;
import com.paypal.base.rest.APIContext;
import com.paypal.base.rest.OAuthTokenCredential;
import com.paypal.base.rest.PayPalRESTException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

/**
 * @author ngnmhieu
 * @since 11.06.16
 */
@Repository
@Transactional
public class CheckoutService
{
    @PersistenceContext
    private EntityManager em;

    // manages buyer's basket
    // TODO: currently not used
    private BasketService basketService;

    // contains configurations for Paypal API
    private PaypalConfiguration paypalConfig;

    private static final String CURRENCY = "EUR";

    /**
     * Construct an instance of EventRepository
     *
     * @param basketService manges basket
     */
    @Autowired
    public CheckoutService(BasketService basketService, PaypalConfiguration paypalConfig)
    {
        Assert.notNull(basketService);
        Assert.notNull(paypalConfig);

        this.basketService = basketService;
        this.paypalConfig = paypalConfig;
    }

    /**
     * Setter for EntityManager
     *
     * @param em
     */
    public void setEntityManager(EntityManager em)
    {
        this.em = em;
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

        return approvalUrl;
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
}