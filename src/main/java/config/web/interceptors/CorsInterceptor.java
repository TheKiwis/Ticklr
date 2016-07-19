package config.web.interceptors;

import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Enables CORS-Requests
 *
 * @author ngnmhieu
 * @since 07.07.16
 */
public class CorsInterceptor extends HandlerInterceptorAdapter
{
    // CORS enabled for every hosts by default
    private String origin = "*";

    // Allowed methods
    private String methods = "GET, POST, HEAD, OPTIONS";

    // Max amount of seconds browsers are allowed to cache pre-flight request
    private String maxAge = "3600";

    // Default allowed headers
    private String allowedHeaders = "Origin, Accept, X-Requested-With, Content-Type, Access-Control-Request-Method, Access-Control-Request-Headers";

    // No headers are exposed by default
    private String exposedHeaders = "";

    private CorsInterceptor()
    {
    }

    /**
     * Factory method for CorsInterceptor
     * @return a new instance of CorsInterceptor
     */
    public static CorsInterceptor get()
    {
        return new CorsInterceptor();
    }

    public CorsInterceptor withOrigin(String origin)
    {
        this.origin = origin;
        return this;
    }

    public CorsInterceptor withMethods(String methods)
    {
        this.methods = methods;
        return this;
    }

    public CorsInterceptor withMaxAge(String maxAge)
    {
        this.maxAge = maxAge;
        return this;
    }

    public CorsInterceptor withAllowHeaders(String headers)
    {
        this.allowedHeaders = headers;
        return this;
    }

    public CorsInterceptor withExposeHeaders(String exposedHeaders)
    {
        this.exposedHeaders = exposedHeaders;
        return this;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception
    {
        response.setHeader("Access-Control-Allow-Origin", origin);
        response.setHeader("Access-Control-Allow-Methods", methods);
        response.setHeader("Access-Control-Max-Age", maxAge);
        response.setHeader("Access-Control-Allow-Headers", allowedHeaders);
        response.setHeader("Access-Control-Expose-Headers", exposedHeaders);

        return true;
    }
}
