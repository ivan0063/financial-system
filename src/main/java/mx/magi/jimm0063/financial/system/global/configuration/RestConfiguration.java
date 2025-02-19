package mx.magi.jimm0063.financial.system.global.configuration;

import mx.magi.jimm0063.financial.system.financial.catalog.domain.entity.*;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurer;
import org.springframework.web.servlet.config.annotation.CorsRegistry;

@Configuration
public class RestConfiguration implements RepositoryRestConfigurer {

    @Override
    public void configureRepositoryRestConfiguration(
            RepositoryRestConfiguration config, CorsRegistry cors) {
        config.exposeIdsFor(Bank.class, Card.class, Debt.class, CardPayment.class, DebtPayment.class);
    }
}
