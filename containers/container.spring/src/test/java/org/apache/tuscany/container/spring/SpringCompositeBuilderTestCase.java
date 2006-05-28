package org.apache.tuscany.container.spring;

import java.net.URI;
import java.net.URISyntaxException;

import org.apache.tuscany.container.spring.mock.TestBean;
import org.apache.tuscany.container.spring.mock.TestBeanImpl;
import org.apache.tuscany.container.spring.mock.VMBinding;
import org.apache.tuscany.spi.builder.BuilderRegistry;
import org.apache.tuscany.spi.context.CompositeContext;
import org.apache.tuscany.spi.context.ServiceContext;
import org.apache.tuscany.spi.extension.ServiceContextExtension;
import org.apache.tuscany.spi.model.BoundService;
import org.apache.tuscany.spi.model.Component;
import org.apache.tuscany.spi.model.CompositeComponentType;
import org.apache.tuscany.spi.wire.TargetWire;
import org.apache.tuscany.test.ArtifactFactory;
import org.jmock.Mock;
import org.jmock.MockObjectTestCase;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.context.support.StaticApplicationContext;

/**
 * @version $$Rev$$ $$Date$$
 */
public class SpringCompositeBuilderTestCase extends MockObjectTestCase {

    public void testBuild() throws Exception {
        SpringCompositeBuilder builder = new SpringCompositeBuilder();
        SpringImplementation impl = new SpringImplementation();
        impl.setComponentType(createComponentType());
        impl.setApplicationContext(createSpringContext());
        Component<SpringImplementation> component = new Component<SpringImplementation>("spring", impl);
        Mock mock = mock(BuilderRegistry.class);
        ServiceContextExtension<TestBean> serviceContext = new ServiceContextExtension<TestBean>("fooService", null, null);
        TargetWire<TestBean> wire = ArtifactFactory.createTargetWire("foo", TestBean.class);
        wire.setServiceName("foo");
        serviceContext.setTargetWire(wire);
        mock.expects(atLeastOnce()).method("build").will(returnValue(serviceContext));
        builder.setBuilderRegistry((BuilderRegistry) mock.proxy());
        CompositeContext context = (CompositeContext) builder.build(null, component, null);
        ServiceContext service = (ServiceContext) context.getContext("fooService");
        TestBean bean = (TestBean) service.getService();
        assertEquals("foo", bean.echo("foo"));
    }

    private GenericApplicationContext createSpringContext() {
        StaticApplicationContext beanFactory = new StaticApplicationContext();
        BeanDefinition definition = new RootBeanDefinition(TestBeanImpl.class);
        beanFactory.registerBeanDefinition("foo", definition);
        return beanFactory;
    }

    private CompositeComponentType createComponentType() {
        CompositeComponentType componentType = new CompositeComponentType();
        BoundService<VMBinding> service = new BoundService<VMBinding>();
        service.setName("FooService");
        service.setBinding(new VMBinding());
        try {
            service.setTarget(new URI("foo"));
        } catch (URISyntaxException e) {
            throw new AssertionError();
        }
        componentType.add(service);
        return componentType;
    }


}
