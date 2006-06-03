package org.apache.tuscany.container.spring;

import junit.framework.TestCase;
import org.apache.tuscany.container.spring.mock.TestBean;
import org.apache.tuscany.container.spring.mock.TestBeanImpl;
import org.apache.tuscany.core.builder.Connector;
import org.apache.tuscany.spi.component.Service;
import org.apache.tuscany.spi.extension.ServiceExtension;
import org.apache.tuscany.spi.wire.InboundInvocationChain;
import org.apache.tuscany.spi.wire.InboundWire;
import org.apache.tuscany.spi.wire.OutboundWire;
import org.apache.tuscany.test.ArtifactFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.context.support.StaticApplicationContext;

/**
 * Tests a simple invocation through a service to a Spring bean
 *
 * @version $$Rev$$ $$Date$$
 */
public class ServiceInvocationTestCase extends TestCase {

    public void testInvocation() {
        SpringCompositeComponent context = new SpringCompositeComponent("parent", createSpringContext(), null,ArtifactFactory.createWireService());
        context.start();
        InboundWire<TestBean> inboundWire = ArtifactFactory.createInboundWire("fooService", TestBean.class);
        OutboundWire<TestBean> outboundWire = ArtifactFactory.createOutboundWire("fooService", TestBean.class);
        ArtifactFactory.terminateWire(outboundWire);
        Service<TestBean> service = new ServiceExtension<TestBean>("fooService", context, ArtifactFactory.createWireService());
        service.setInboundWire(inboundWire);
        service.setOutboundWire(outboundWire);
        Connector conntector = ArtifactFactory.createConnector();
        conntector.connect(inboundWire, outboundWire, true);
        // TODO fix below
        for (InboundInvocationChain chain : inboundWire.getInvocationChains().values()) {
            chain.setTargetInvoker(context.createTargetInvoker("foo", chain.getMethod()));
        }
        context.register(service);
        assertEquals("bar", ((TestBean) context.getChild("fooService").getServiceInstance()).echo("bar"));
    }


    private GenericApplicationContext createSpringContext() {
        StaticApplicationContext beanFactory = new StaticApplicationContext();
        BeanDefinition definition = new RootBeanDefinition(TestBeanImpl.class);
        beanFactory.registerBeanDefinition("foo", definition);
        return beanFactory;
    }

}
