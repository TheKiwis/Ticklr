package app.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import java.util.List;

/**
 * @author ngnmhieu
 */
@Configuration
@EnableWebMvc // use spring-mvc default configurations
@ComponentScan(basePackages = "app") // search for beans in package app.web
public class WebConfig extends WebMvcConfigurerAdapter
{
    @Override
    public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer)
    {
        configurer.enable();
    }

    @Override
    public void extendMessageConverters(List<HttpMessageConverter<?>> converters)
    {
        super.extendMessageConverters(converters);
    }
}
