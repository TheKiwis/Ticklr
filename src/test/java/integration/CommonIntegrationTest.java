package integration;

import app.config.RootConfig;
import app.config.web.WebConfig;
import org.dbunit.DataSourceBasedDBTestCase;
import org.dbunit.dataset.DefaultDataSet;
import org.dbunit.dataset.IDataSet;
import org.flywaydb.test.annotation.FlywayTest;
import org.flywaydb.test.junit.FlywayTestExecutionListener;
import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.sql.DataSource;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;

/**
 * @author ngnmhieu
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {RootConfig.class, WebConfig.class})
@WebAppConfiguration("src/main/java/")
@TestExecutionListeners({
        DependencyInjectionTestExecutionListener.class,
        FlywayTestExecutionListener.class
})
@ActiveProfiles("test")
@FlywayTest
abstract public class CommonIntegrationTest extends DataSourceBasedDBTestCase
{
    @Autowired
    protected DataSource dataSource;

    @PersistenceContext
    protected EntityManager em;

    protected MockMvc mockMvc;

    @Autowired
    protected WebApplicationContext wac;

    @Override
    protected DataSource getDataSource()
    {
        return dataSource;
    }

    @Before
    @Override
    public void setUp() throws Exception
    {
        super.setUp();
        mockMvc = MockMvcBuilders.webAppContextSetup(wac).apply(springSecurity()).build();
    }

    @Override
    @After
    public void tearDown() throws Exception
    {
        super.tearDown();
    }

    /**
     * Override this to return custom dataset
     *
     * @return
     * @throws Exception
     */
    @Override
    protected IDataSet getDataSet() throws Exception
    {
        return new DefaultDataSet();
    }
}
