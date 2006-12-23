package org.apache.tuscany.container.spring.integration;

import org.osoa.sca.CompositeContext;
import org.osoa.sca.CurrentCompositeContext;

import org.apache.tuscany.spi.component.Service;
import org.apache.tuscany.spi.wire.WireService;

import org.apache.tuscany.container.spring.impl.SpringCompositeComponent;
import org.apache.tuscany.container.spring.mock.TestBean;
import org.apache.tuscany.test.SCATestCase;
import org.apache.tuscany.test.ArtifactFactory;

/**
 * Bootstraps a simple scenario where a service can invoke a Spring bean. This test case is intended to be temporary and
 * replaced when the SPI test harness is finished.
 * <p/>
 * <bold>PLEASE DO NOT EMULATE</bold>
 *
 * @version $Rev$ $Date$
 */
public class BootstrapTestCase extends SCATestCase {
    private CompositeContext context;
    private WireService wireService;

    public void testDemoBoot() throws Exception {
        SpringCompositeComponent comp = (SpringCompositeComponent) component.getChild("Spring");
        Service service = (Service) comp.getChild("fooService");
        TestBean bean = wireService.createProxy(TestBean.class, service.getInboundWire());
        bean.echo("foo");
        bean.getBean().echo("foo");
    }

    protected void setUp() throws Exception {
        wireService = ArtifactFactory.createWireService();
        addExtension("spring.extension", getClass().getClassLoader().getResource("META-INF/sca/spring.system.scdl"));
        setApplicationSCDL(getClass().getClassLoader().getResource("META-INF/sca/default.scdl"));
        super.setUp();
        context = CurrentCompositeContext.getContext();
    }


}
