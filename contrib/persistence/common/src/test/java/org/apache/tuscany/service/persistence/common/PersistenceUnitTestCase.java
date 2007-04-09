package org.apache.tuscany.service.persistence.common;

import junit.framework.TestCase;

public class PersistenceUnitTestCase extends TestCase {

    protected void setUp() throws Exception {
/*
        addExtension("tuscany.jpa", getClass().getClassLoader().getResource("META-INF/sca/jpa.scdl"));
        addExtension("geronimo.jta", getClass().getClassLoader().getResource("META-INF/sca/geronimo.jta.scdl"));
        setApplicationSCDL(getClass().getClassLoader().getResource("META-INF/sca/test1.scdl"));
        super.setUp();
        RuntimeComponent runtime = (RuntimeComponent) component.getParent().getParent();
        CompositeComponent systemComposite = runtime.getSystemComponent();
        CompositeComponent topLevelComposite = (CompositeComponent) systemComposite.getSystemChild(TUSCANY_SYSTEM);

        JavaAtomicComponent cmp = (JavaAtomicComponent) component.getChild("TestService1");
        TestService1 testService1 = (TestService1) cmp.getTargetInstance();
        testService1.testMethod();
*/
    }

    protected void tearDown() throws Exception {
/*
        super.tearDown();
*/
    }

    public void testGetComponent() {
    }

}
