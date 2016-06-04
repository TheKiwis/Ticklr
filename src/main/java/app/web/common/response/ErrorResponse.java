package app.web.common.response;

/**
 * @author ngnmhieu
 * @since 31.05.16
 */
public class ErrorResponse
{
    public static final String VALIDATION_ERROR = "VALIDATION_ERROR";

    public String errorCode;

    public ErrorResponse(String errorCode)
    {
        this.errorCode = errorCode;
    }
}
