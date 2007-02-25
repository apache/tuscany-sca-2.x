package org.apache.tuscany.persistence.datasource;

import javax.sql.DataSource;

import org.apache.tuscany.spi.implementation.java.JavaMappedProperty;
import org.apache.tuscany.spi.model.ComponentType;

import junit.framework.TestCase;

/**
 * @version $Rev$ $Date$
 */
public class DSComponentTypeLoaderTestCase extends TestCase {

    public void testIntrospection() throws Exception {
        DSComponentTypeLoader loader = new DSComponentTypeLoader(null);
        DataSourceImplementation implementation = new DataSourceImplementation();
        implementation.setProviderName(Foo.class.getName());
        implementation.setClassLoader(getClass().getClassLoader());
        loader.load(implementation, null);
        ComponentType<?, ?, ?> type = implementation.getComponentType();
        assertEquals(2, type.getProperties().size());
        assertNull(type.getProperties().get("object"));
        Object bar = type.getProperties().get("bar");
        assertEquals(String.class, ((JavaMappedProperty) bar).getJavaType());
        Object baz = type.getProperties().get("baz");
        assertEquals(Integer.TYPE, ((JavaMappedProperty) baz).getJavaType());
        assertEquals(1, type.getServices().size());
        assertEquals(DataSource.class, type.getServices().get("DataSource").getServiceContract().getInterfaceClass());
    }


    public void testOverloadedMethod() throws Exception {
        DSComponentTypeLoader loader = new DSComponentTypeLoader(null);
        DataSourceImplementation implementation = new DataSourceImplementation();
        implementation.setProviderName(BadFoo.class.getName());
        implementation.setClassLoader(getClass().getClassLoader());
        try {
            loader.load(implementation, null);
            fail();
        } catch (AmbiguousPropertyException e) {
            // expected
        }
    }

    public class Foo {

        private String bar;

        private int baz;

        private Foo object;

        public String getBar() {
            return bar;
        }

        public void setBar(String bar) {
            this.bar = bar;
        }

        public int getBaz() {
            return baz;
        }

        public void setBaz(int baz) {
            this.baz = baz;
        }

        public Foo getObject() {
            return object;
        }

        public void setObject(Foo object) {
            this.object = object;
        }
    }

    public class BadFoo {

        public void setBar(String bar) {
        }

        public void setBar(Object bar) {
        }
    }

}
