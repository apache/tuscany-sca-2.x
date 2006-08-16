package org.apache.tuscany.container.spring.integration;

import org.osoa.sca.CompositeContext;
import org.osoa.sca.CurrentCompositeContext;

import org.apache.tuscany.spi.component.Service;

import org.apache.tuscany.container.spring.impl.SpringCompositeComponent;
import org.apache.tuscany.container.spring.mock.TestBean;
import org.apache.tuscany.test.SCATestCase;

/**
 * @version $Rev$ $Date$
 */
public class BootstrapTestCase extends SCATestCase {

    private CompositeContext context;

    public void testDemoBoot() {
        SpringCompositeComponent comp = (SpringCompositeComponent) component.getChild("Spring");
        Service service = (Service) comp.getChild("fooService");
        TestBean bean = (TestBean) service.getServiceInstance();
        bean.echo("foo");
    }

    protected void setUp() throws Exception {
        addExtension("test.binding.extension",
            getClass().getClassLoader().getResource("META-INF/sca/test.binding.system.scdl"));
        addExtension("spring.extension", getClass().getClassLoader().getResource("META-INF/sca/spring.system.scdl"));
        super.setUp();
        context = CurrentCompositeContext.getContext();
    }


}
