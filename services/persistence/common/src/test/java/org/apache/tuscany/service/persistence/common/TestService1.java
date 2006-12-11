package org.apache.tuscany.service.persistence.common;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;
import javax.transaction.TransactionManager;

import org.apache.tuscany.spi.annotation.Autowire;

public class TestService1 {
    
    @Autowire 
    protected TransactionManager tx;
    
    @PersistenceUnit(unitName="test")
    private EntityManagerFactory emf;
    
    public void testMethod() throws Exception {
        
        tx.begin();
        EntityManager em = emf.createEntityManager();
        em.persist(new Employee());
        tx.commit();
        
    }

}
