package app.integration;
import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import app.config.RootConfig;
import app.config.WebConfig;
import org.dbunit.DBTestCase;
import org.dbunit.DataSourceBasedDBTestCase;
import org.dbunit.assertion.DbUnitAssert;
import org.dbunit.dataset.DefaultDataSet;
import org.dbunit.dataset.IDataSet;
import org.flywaydb.test.annotation.FlywayTest;
import org.flywaydb.test.junit.FlywayTestExecutionListener;
import org.junit.Before;
import org.junit.Test;
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
import javax.persistence.Query;
import javax.sql.DataSource;
import javax.validation.groups.Default;
import javax.xml.crypto.Data;

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
public class UserManipulationIT extends DataSourceBasedDBTestCase
{
    @Autowired
    DataSource dataSource;

    // todo integration test with database fixture
    @PersistenceContext
    EntityManager em;

    @Autowired
    WebApplicationContext wac;

    private MockMvc mockMvc;

    @Before
    public void setup()
    {
        mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
    }

    @Test
    public void testSaveUser() throws Exception
    {
        Query query;

        query = em.createQuery("SELECT COUNT(u) FROM User u");
        assertEquals(0l, query.getSingleResult());

        mockMvc.perform(
                post("/users/")
                        .accept("application/json")
                        .param("email", "email@gmail.com")
                        .param("password", "123456789"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$").isEmpty());

        query = em.createQuery("SELECT COUNT(u) FROM User u");
        assertEquals(1l, query.getSingleResult());
    }

    @Override
    protected DataSource getDataSource()
    {
        return dataSource;
    }

    @Override
    protected IDataSet getDataSet() throws Exception
    {
        return new DefaultDataSet();
    }
}
