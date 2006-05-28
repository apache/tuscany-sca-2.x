package org.apache.tuscany.container.spring;

import org.apache.tuscany.container.spring.mock.TestBean;
import org.apache.tuscany.container.spring.mock.TestBeanImpl;
import org.apache.tuscany.spi.context.ReferenceContext;
import org.jmock.Mock;
import org.jmock.MockObjectTestCase;
import org.springframework.beans.PropertyValue;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.context.support.StaticApplicationContext;

/**
 * Verifies wiring from a Spring bean to an SCA composite reference
 *
 * @version $$Rev$$ $$Date$$
 */
public class ReferenceInvocationTestCase extends MockObjectTestCase {

    public void testInvocation() throws Exception {
        GenericApplicationContext ctx = createSpringContext();
        SpringCompositeContext parent = new SpringCompositeContext("spring", ctx, null);
        parent.start();
        TestBean referenceTarget = new TestBeanImpl();
        Mock mock = mock(ReferenceContext.class);
        mock.stubs().method("getName").will(returnValue("bar"));
        mock.stubs().method("getInterface").will(returnValue(TestBean.class));
        mock.expects(atLeastOnce()).method("getService").will(returnValue(referenceTarget));
        ReferenceContext referenceContext = (ReferenceContext) mock.proxy();
        parent.registerContext(referenceContext);
        ctx.getBean("foo");
    }

    private GenericApplicationContext createSpringContext() {
        StaticApplicationContext beanFactory = new StaticApplicationContext();
        BeanDefinition definition = new RootBeanDefinition(TestBeanImpl.class);
        RuntimeBeanReference ref = new RuntimeBeanReference("bar");
        PropertyValue val = new PropertyValue("bean", ref);
        definition.getPropertyValues().addPropertyValue(val);
        beanFactory.registerBeanDefinition("foo", definition);
        return beanFactory;
    }
}
