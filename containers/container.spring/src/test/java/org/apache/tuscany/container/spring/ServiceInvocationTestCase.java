package org.apache.tuscany.container.spring;

import junit.framework.TestCase;
import org.apache.tuscany.container.spring.mock.TestBean;
import org.apache.tuscany.container.spring.mock.TestBeanImpl;
import org.apache.tuscany.spi.context.ServiceContext;
import org.apache.tuscany.spi.extension.ServiceContextExtension;
import org.apache.tuscany.spi.wire.InboundInvocationChain;
import org.apache.tuscany.spi.wire.InboundWire;
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
        SpringCompositeContext context = new SpringCompositeContext("parent", createSpringContext(), null);
        context.start();
        InboundWire<TestBean> wire = ArtifactFactory.createTargetWire("fooService", TestBean.class);
        for (InboundInvocationChain chain : wire.getInvocationChains().values()) {
            chain.setTargetInvoker(context.createTargetInvoker("foo", chain.getMethod()));
        }
        ServiceContext<TestBean> serviceContext = new ServiceContextExtension<TestBean>("fooService", wire, context);
        serviceContext.setInboundWire(wire);
        context.registerContext(serviceContext);
        assertEquals("bar", ((TestBean) context.getContext("fooService").getService()).echo("bar"));
    }


    private GenericApplicationContext createSpringContext() {
        StaticApplicationContext beanFactory = new StaticApplicationContext();
        BeanDefinition definition = new RootBeanDefinition(TestBeanImpl.class);
        beanFactory.registerBeanDefinition("foo", definition);
        return beanFactory;
    }

}
