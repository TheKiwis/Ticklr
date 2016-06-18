package app.web.common.response;

/**
 * @author ngnmhieu
 * @since 31.05.16
 */
public class ErrorResponse
{
    public String errorCode;

    public String message;

    public Object details;

    public ErrorResponse(ErrorCode errorCode)
    {
        this.errorCode = errorCode.getCode();
        this.message = errorCode.getDefaultMessage();
    }

    public ErrorResponse(ErrorCode errorCode, String message)
    {
        this.errorCode = errorCode.getCode();
        this.message = message;
    }

    public ErrorResponse(ErrorCode errorCode, String message, Object details)
    {
        this(errorCode, message);
        this.details = details;
    }
}
