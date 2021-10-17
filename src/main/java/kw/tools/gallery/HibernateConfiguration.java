package kw.tools.gallery;

import org.hibernate.SessionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class HibernateConfiguration
{
    @Bean
    public SessionFactory sessionFactory()
    {
        // todo: use the features or Hibernate & Spring integration
        return new org.hibernate.cfg.Configuration().configure("hibernate.cfg.xml").buildSessionFactory();
    }
}
