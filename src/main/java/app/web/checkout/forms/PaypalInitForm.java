package app.web.checkout.forms;

import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;

/**
 * @author ngnmhieu
 * @since 11.06.16
 */
public class PaypalInitForm
{
    @NotNull
    @NotEmpty
    public String returnUrl;

    @NotNull
    @NotEmpty
    public String cancelUrl;

    public PaypalInitForm()
    {
    }

    public PaypalInitForm(String returnUrl, String cancelUrl)
    {
        this.returnUrl = returnUrl;
        this.cancelUrl = cancelUrl;
    }
}
