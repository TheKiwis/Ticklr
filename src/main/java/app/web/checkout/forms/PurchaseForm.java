package app.web.checkout.forms;

import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

/**
 * @author ngnmhieu
 * @since 12.06.16
 */
public class PurchaseForm
{
    public static class TicketInfo
    {
        @NotEmpty
        public Long ticketSetId;

        @NotEmpty
        public String firstName;

        @NotEmpty
        public String lastName;

        protected TicketInfo() {};

        public TicketInfo(Long ticketSetId, String firstName, String lastName)
        {
            this.ticketSetId = ticketSetId;
            this.firstName = firstName;
            this.lastName = lastName;
        }
    }

    public static class PaypalPaymentInfo
    {
        @NotEmpty
        public String payerId;

        protected PaypalPaymentInfo() {};

        public PaypalPaymentInfo(String payerId)
        {
            this.payerId = payerId;
        }
    }

    @NotEmpty
    public List<TicketInfo> ticketInfos;

    @NotEmpty
    public String paymentMethod;

    @NotNull
    public PaypalPaymentInfo paypal;

    public PurchaseForm()
    {
    }

    public PurchaseForm(PaypalPaymentInfo paypal, String paymentMethod, List<TicketInfo> ticketInfos)
    {
        this.paypal = paypal;
        this.paymentMethod = paymentMethod;
        this.ticketInfos = ticketInfos;
    }

    public PurchaseForm addTicketInfo(Long ticketSetId, String firstName, String lastName)
    {
        TicketInfo info = new TicketInfo(ticketSetId, firstName, lastName);
        if (ticketInfos == null)
            ticketInfos = new ArrayList<>();
        ticketInfos.add(info);
        return this;
    }

    public PurchaseForm setPaymentMethod(String paymentMethod)
    {
        this.paymentMethod = paymentMethod;
        return this;
    }

    public PurchaseForm setPaypalPayer(String payerId)
    {
        this.paypal = new PaypalPaymentInfo(payerId );
        return this;
    }
}
