package org.apache.tuscany.container.spring;

import java.net.URI;
import java.net.URISyntaxException;

import org.apache.tuscany.container.spring.mock.TestBean;
import org.apache.tuscany.container.spring.mock.TestBeanImpl;
import org.apache.tuscany.container.spring.mock.VMBinding;
import org.apache.tuscany.spi.builder.Connector;
import org.apache.tuscany.spi.builder.BuilderRegistry;
import org.apache.tuscany.spi.component.CompositeComponent;
import org.apache.tuscany.spi.component.Service;
import org.apache.tuscany.spi.extension.ServiceExtension;
import org.apache.tuscany.spi.model.BoundServiceDefinition;
import org.apache.tuscany.spi.model.ComponentDefinition;
import org.apache.tuscany.spi.model.CompositeComponentType;
import org.apache.tuscany.spi.model.ServiceDefinition;
import org.apache.tuscany.spi.wire.InboundWire;
import org.apache.tuscany.spi.wire.OutboundWire;
import org.apache.tuscany.spi.wire.WireService;
import org.apache.tuscany.test.ArtifactFactory;
import org.jmock.Mock;
import org.jmock.MockObjectTestCase;
import org.jmock.core.Constraint;
import org.jmock.core.Formatting;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.support.StaticApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * @version $$Rev$$ $$Date$$
 */
public class SpringCompositeBuilderTestCase extends MockObjectTestCase {

    public void testBuildImplicit() throws Exception {

        // Create an assembly model consisting of a component implemented by Spring
        SpringImplementation impl = new SpringImplementation(createComponentType());
        impl.setApplicationContext(createImplicitSpringContext());
        ComponentDefinition<SpringImplementation> componentDefinition = new ComponentDefinition<SpringImplementation>("spring", impl);

        // Create a service instance that the mock builder registry will return
        WireService wireService = ArtifactFactory.createWireService();
        ServiceExtension<TestBean> serviceContext = new ServiceExtension<TestBean>("fooService", null, wireService);
        InboundWire<TestBean> inboundWire = ArtifactFactory.createInboundWire("fooService", TestBean.class);
        OutboundWire<TestBean> outboundWire = ArtifactFactory.createOutboundWire("fooService", TestBean.class);
        // REVIEW: this call appears to be unnecessary right now, is this a bug?
        // outboundWire.setTargetName(new QualifiedName("fooBean"));
        ArtifactFactory.terminateWire(outboundWire);
        serviceContext.setInboundWire(inboundWire);
        serviceContext.setOutboundWire(outboundWire);
        Connector connector = ArtifactFactory.createConnector();
        connector.connect(inboundWire, outboundWire, true);

        // Configure the mock builder registry
        Mock mock = mock(BuilderRegistry.class);
        mock.expects(atLeastOnce()).method("build").with(ANYTHING, serviceIsNamed("fooService") , ANYTHING)
                                                   .will(returnValue(serviceContext));

        // Test the SpringCompositeBuilder
        SpringCompositeBuilder builder = new SpringCompositeBuilder();
        builder.setWireService(wireService);
        builder.setBuilderRegistry((BuilderRegistry) mock.proxy());
        CompositeComponent component = (CompositeComponent) builder.build(null, componentDefinition, null);
        Service service = component.getService("fooService");
        TestBean bean = (TestBean) service.getServiceInstance();
        assertEquals("call foo", bean.echo("call foo"));
    }

    /** Return a Spring context w/ a single bean named "fooBean", implemented by TestBeanImpl */
    private ConfigurableApplicationContext createImplicitSpringContext() {
        StaticApplicationContext beanFactory = new StaticApplicationContext();
        BeanDefinition definition = new RootBeanDefinition(TestBeanImpl.class);
        beanFactory.registerBeanDefinition("fooBean", definition);
        return beanFactory;
    }

    private CompositeComponentType createComponentType() {
        CompositeComponentType componentType = new CompositeComponentType();
        BoundServiceDefinition<VMBinding> serviceDefinition = new BoundServiceDefinition<VMBinding>();
        serviceDefinition.setName("fooService");
        serviceDefinition.setBinding(new VMBinding());
        try {
            serviceDefinition.setTarget(new URI("fooBean"));
        } catch (URISyntaxException e) {
            throw new AssertionError();
        }
        componentType.add(serviceDefinition);
        return componentType;
    }

    /** JMock constraint class to test ServiceDefinition name */
    private class ServiceIsNamed implements Constraint {
        private String name;

        public ServiceIsNamed( String name ) {
            this.name = name;
        }

        public boolean eval( Object o ) {
            return o instanceof ServiceDefinition && ((ServiceDefinition)o).getName().equals(name);
        }

        public StringBuffer describeTo( StringBuffer buffer ) {
            return buffer.append("a service named ")
                         .append(Formatting.toReadableString(name));
        }
    }

    /** JMock factory method for ServiceIsNamed constraint */
    private Constraint serviceIsNamed( String name ) {
        return new ServiceIsNamed(name);
    }
}
