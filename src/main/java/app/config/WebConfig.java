package app.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * @author ngnmhieu
 */
@Configuration
@ComponentScan(basePackages = "app.web") // search for beans in package app.web
@EnableWebMvc // use spring-mvc default configurations
public class WebConfig extends WebMvcConfigurerAdapter
{
}
