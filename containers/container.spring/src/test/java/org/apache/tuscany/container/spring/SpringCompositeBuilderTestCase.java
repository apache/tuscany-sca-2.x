package org.apache.tuscany.container.spring;

import java.net.URI;
import java.net.URISyntaxException;

import org.apache.tuscany.container.spring.mock.TestBean;
import org.apache.tuscany.container.spring.mock.TestBeanImpl;
import org.apache.tuscany.container.spring.mock.VMBinding;
import org.apache.tuscany.core.builder.Connector;
import org.apache.tuscany.spi.QualifiedName;
import org.apache.tuscany.spi.builder.BuilderRegistry;
import org.apache.tuscany.spi.component.CompositeComponent;
import org.apache.tuscany.spi.component.Service;
import org.apache.tuscany.spi.extension.ServiceExtension;
import org.apache.tuscany.spi.model.BoundServiceDefinition;
import org.apache.tuscany.spi.model.ComponentDefinition;
import org.apache.tuscany.spi.model.CompositeComponentType;
import org.apache.tuscany.spi.wire.InboundWire;
import org.apache.tuscany.spi.wire.OutboundWire;
import org.apache.tuscany.spi.wire.WireService;
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
        WireService wireService = ArtifactFactory.createWireService();
        SpringCompositeBuilder builder = new SpringCompositeBuilder();
        builder.setWireService(wireService);
        SpringImplementation impl = new SpringImplementation();
        impl.setComponentType(createComponentType());
        impl.setApplicationContext(createSpringContext());
        ComponentDefinition<SpringImplementation> componentDefinition = new ComponentDefinition<SpringImplementation>("spring", impl);
        Mock mock = mock(BuilderRegistry.class);
        ServiceExtension<TestBean> serviceContext = new ServiceExtension<TestBean>("fooService", null, wireService);
        InboundWire<TestBean> inboundWire = ArtifactFactory.createInboundWire("fooSerice", TestBean.class);
        OutboundWire<TestBean> outboundwire = ArtifactFactory.createOutboundWire("fooService", TestBean.class);
        outboundwire.setTargetName(new QualifiedName("foo"));
        serviceContext.setInboundWire(inboundWire);
        serviceContext.setOutboundWire(outboundwire);
        Connector connector = ArtifactFactory.createConnector();
        connector.connect(inboundWire, outboundwire, true);
        ArtifactFactory.terminateWire(inboundWire);
        mock.expects(atLeastOnce()).method("build").will(returnValue(serviceContext));
        builder.setBuilderRegistry((BuilderRegistry) mock.proxy());
        CompositeComponent component = (CompositeComponent) builder.build(null, componentDefinition, null);
        Service service = (Service) component.getChild("fooService");
        TestBean bean = (TestBean) service.getServiceInstance();
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
        BoundServiceDefinition<VMBinding> serviceDefinition = new BoundServiceDefinition<VMBinding>();
        serviceDefinition.setName("FooService");
        serviceDefinition.setBinding(new VMBinding());
        try {
            serviceDefinition.setTarget(new URI("foo"));
        } catch (URISyntaxException e) {
            throw new AssertionError();
        }
        componentType.add(serviceDefinition);
        return componentType;
    }


}
