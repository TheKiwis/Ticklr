package config.web;

import config.environment.Profiles;
import org.springframework.context.annotation.Configuration;

/**
 * @author ngnmhieu
 * @since 04.05.16
 */
@Configuration
@org.springframework.context.annotation.Profile(Profiles.TEST)
public class TestWebConfig extends BaseWebConfig
{
}
