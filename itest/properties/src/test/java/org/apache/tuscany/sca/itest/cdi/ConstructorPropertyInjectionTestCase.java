package org.apache.tuscany.sca.itest.cdi;

import junit.framework.TestCase;

import org.apache.tuscany.sca.host.embedded.SCADomain;

public class ConstructorPropertyInjectionTestCase extends TestCase {

    public void testFoo1() throws Exception {
        SCADomain sca = SCADomain.newInstance("ConstructorPropertyInjection.composite");
        Bar foo = sca.getService(Bar.class, "Foo1Component");
        assertEquals("fubar", foo.getBar());
    }

    public void testFoo2() throws Exception {
        SCADomain sca = SCADomain.newInstance("ConstructorPropertyInjection.composite");
        Bar foo = sca.getService(Bar.class, "Foo2Component");
        assertEquals("fubar", foo.getBar());
    }

    public void testFoo3() throws Exception {
        SCADomain sca = SCADomain.newInstance("ConstructorPropertyInjection.composite");
        Bar foo = sca.getService(Bar.class, "Foo3Component");
        assertEquals("fubar", foo.getBar());
    }
}
