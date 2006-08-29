package org.apache.tuscany.container.spring.integration;

import org.osoa.sca.CompositeContext;
import org.osoa.sca.CurrentCompositeContext;

import org.apache.tuscany.spi.component.Service;

import org.apache.tuscany.container.spring.impl.SpringCompositeComponent;
import org.apache.tuscany.container.spring.mock.TestBean;
import org.apache.tuscany.test.Bootstrapper;

/**
 * Bootstraps a simple scenario where a service can invoke a Spring bean. This test case is intended to be temporary and
 * replaced when the SPI test harness is finished.
 * <p/>
 * <bold>PLEASE DO NOT EMULATE</bold>
 *
 * @version $Rev$ $Date$
 */
public class BootstrapTestCase extends Bootstrapper {

    private CompositeContext context;

    public void testDemoBoot() {
        SpringCompositeComponent comp = (SpringCompositeComponent) component.getChild("Spring");
        Service service = (Service) comp.getChild("fooService");
        TestBean bean = (TestBean) service.getServiceInstance();
        bean.echo("foo");
        bean.getBean().echo("foo");
    }

    protected void setUp() throws Exception {
        addExtension("spring.extension", getClass().getClassLoader().getResource("META-INF/sca/spring.system.scdl"));
        super.setUp();
        context = CurrentCompositeContext.getContext();
    }


}
