package integration;

import org.junit.Test;
import org.springframework.http.MediaType;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * @author ngnmhieu
 */
public class ConfigurationIT extends CommonIntegrationTest
{
    @Test
    public void shouldServeResourceAsXML() throws Exception
    {
        mockMvc.perform(
                get("/users/profile").accept(MediaType.APPLICATION_XML))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_XML));
    }

    @Test
    public void shouldServeResourceAsJSON() throws Exception
    {
        mockMvc.perform(
                get("/users/profile").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
    }

    @Test
    public void testServeResourceAsJsonByDefault() throws Exception
    {
        mockMvc.perform(
                get("/users/profile").accept(MediaType.ALL))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
    }

    // todo disable tomcat 4xx 5xx html page on production
}
