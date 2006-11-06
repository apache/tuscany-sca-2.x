package org.apache.tuscany.service.persistence.common;

import junit.framework.TestCase;

public class DefaultPersistenceUnitBuilderTestCase extends TestCase {

    public void testNewEntityManagerFactory() {
        new DefaultPersistenceUnitBuilder().newEntityManagerFactory("test", getClass().getClassLoader());
    }

}
