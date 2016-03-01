package app.integration;

import app.config.RootConfig;
import app.config.WebConfig;
import org.flywaydb.test.junit.FlywayTestExecutionListener;
import org.junit.runner.RunWith;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.web.WebAppConfiguration;

/**
 * @author ngnmhieu
 */
public class ConfigurationIT extends CommonIntegrationTest
{
}
