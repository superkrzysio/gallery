package kw.tools.gallery;

import kw.tools.gallery.processing.Thumbnailing;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Beans
{
    @Value("${thumbnailing.strategy}")
    private String thumbnailingStrategyBean;

    @Autowired
    private ApplicationContext applicationContext;

    @Bean
    public Thumbnailing thumbnailing()
    {
         return applicationContext.getBean(thumbnailingStrategyBean, Thumbnailing.class);
    }
}
