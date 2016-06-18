package app.web.common.response;

/**
 * @author ngnmhieu
 * @since 17.06.16
 */
public enum ErrorCode
{
    /**************************
     * Common error codes
     **************************/

    VALIDATION_ERROR("VALIDATION_ERROR", ""),

    /**************************
     * Checkout
     **************************/

    BASKET_IS_EMPTY("BASKET_IS_EMPTY", ""),

    PAYPAL_ERROR("PAYPAL_ERROR", ""),

    PURCHASE_NO_PAYMENT("PURCHASE_NO_PAYMENT"),

    TICKET_SET_OUT_OF_STOCK("TICKET_SET_OUT_OF_STOCK");

    private String code;

    private String defaultMessage;

    ErrorCode(String code, String defaultMessage)
    {
        this.code = code;
        this.defaultMessage = defaultMessage == null ? "" : defaultMessage;
    }

    ErrorCode(String code)
    {
        this(code, "");
    }

    public String getDefaultMessage()
    {
        return defaultMessage;
    }

    public String getCode()
    {
        return code;
    }


    @Override
    public String toString()
    {
        return getCode();
    }
}
