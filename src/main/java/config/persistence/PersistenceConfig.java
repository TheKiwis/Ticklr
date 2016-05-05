package config.persistence;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * @author ngnmhieu
 */
@Configuration
@Import({HibernateConfig.class, TransactionConfig.class})
public class PersistenceConfig
{
}

