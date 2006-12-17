package org.apache.tuscany.service.persistence.common;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;
import javax.transaction.TransactionManager;

import org.osoa.sca.annotations.Resource;

import org.apache.tuscany.spi.annotation.Autowire;

public class TestService1 {
    
    @Resource
    protected TransactionManager tx;
    
    @PersistenceUnit(unitName="test")
    protected EntityManagerFactory emf;
    
    public void testMethod() throws Exception {
        
        tx.begin();
        EntityManager em = emf.createEntityManager();
        tx.commit();
        
    }

}
