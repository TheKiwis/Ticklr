package config.web;

import com.fasterxml.jackson.databind.SerializationFeature;
import org.springframework.context.annotation.*;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.*;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import java.util.List;

/**
 * @author ngnmhieu
 */
@EnableWebMvc
@ComponentScan(basePackages = "app.web")
public class WebConfig extends WebMvcConfigurerAdapter
{
    @Override
    public void addViewControllers(ViewControllerRegistry registry)
    {
        registry.addViewController("/").setViewName("/index.html");
    }

    @Override
    public void extendMessageConverters(List<HttpMessageConverter<?>> converters)
    {
        converters.stream()
                .filter(converter -> converter instanceof MappingJackson2HttpMessageConverter)
                .forEach(converter -> {
                    MappingJackson2HttpMessageConverter jacksonMapper = ((MappingJackson2HttpMessageConverter) converter);
                    jacksonMapper.getObjectMapper().disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
                });
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry)
    {
        boolean cacheResources = false;

        registry.addResourceHandler("/**")
                .addResourceLocations("/public/")
                .resourceChain(cacheResources);
    }

    @Override
    public void configureContentNegotiation(ContentNegotiationConfigurer configurer)
    {
        configurer.defaultContentType(MediaType.APPLICATION_JSON);
    }
}
