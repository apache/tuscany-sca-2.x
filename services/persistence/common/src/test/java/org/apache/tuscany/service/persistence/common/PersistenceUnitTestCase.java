package org.apache.tuscany.service.persistence.common;

import static org.apache.tuscany.spi.bootstrap.ComponentNames.TUSCANY_SYSTEM;

import org.apache.tuscany.core.implementation.java.JavaAtomicComponent;
import org.apache.tuscany.spi.bootstrap.RuntimeComponent;
import org.apache.tuscany.spi.component.CompositeComponent;
import org.apache.tuscany.test.SCATestCase;

public class PersistenceUnitTestCase extends SCATestCase {

    protected void setUp() throws Exception {
        addExtension("tuscany.jpa", getClass().getClassLoader().getResource("META-INF/sca/jpa.scdl"));
        addExtension("geronimo.jta", getClass().getClassLoader().getResource("META-INF/sca/geronimo.jta.scdl"));
        setApplicationSCDL(getClass().getClassLoader().getResource("META-INF/sca/test1.scdl"));
        super.setUp();
        RuntimeComponent runtime = (RuntimeComponent) component.getParent().getParent();
        CompositeComponent systemComposite = runtime.getSystemComponent();
        CompositeComponent topLevelComposite = (CompositeComponent) systemComposite.getSystemChild(TUSCANY_SYSTEM);
        
        JavaAtomicComponent cmp = (JavaAtomicComponent)component.getChild("TestService1");
        TestService1 testService1 = (TestService1) cmp.getServiceInstance();
//        testService1.testMethod();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }
    
    public void testGetComponent() {
    }

}
