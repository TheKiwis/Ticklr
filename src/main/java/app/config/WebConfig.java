package app.config;

import com.fasterxml.jackson.databind.SerializationFeature;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.*;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * @author ngnmhieu
 */
@Configuration
@EnableWebMvc // use spring-mvc default configurations
@ComponentScan(basePackages = "app") // search for beans in package app.web
public class WebConfig extends WebMvcConfigurerAdapter
{
    @Bean
    public ViewResolver viewResolver()
    {
        InternalResourceViewResolver resolver = new InternalResourceViewResolver();
        resolver.setPrefix("/WEB-INF/views/");
        resolver.setSuffix(".jsp");
        return resolver;
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
