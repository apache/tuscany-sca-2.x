package org.apache.tuscany.container.spring;

import org.apache.tuscany.container.spring.mock.TestBean;
import org.apache.tuscany.container.spring.mock.TestBeanImpl;
import org.jmock.MockObjectTestCase;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.StaticApplicationContext;

/**
 * Verifies a simple invocation on a Spring bean
 * 
 * @version $$Rev$$ $$Date$$
 */
public class SpringInvocationTestCase extends MockObjectTestCase {

    public void testSpringInvocation() throws Exception {
        ConfigurableApplicationContext ctx = createSpringContext();
        SpringInvoker invoker = new SpringInvoker("foo", TestBean.class.getMethod("echo", String.class), ctx);
        assertEquals("foo", invoker.invokeTarget("foo"));
    }

    private ConfigurableApplicationContext createSpringContext() {
        StaticApplicationContext beanFactory = new StaticApplicationContext();
        BeanDefinition definition = new RootBeanDefinition(TestBeanImpl.class);
        beanFactory.registerBeanDefinition("foo", definition);
        beanFactory.registerBeanDefinition("bar", definition);
        return beanFactory;
    }
}
