/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.    
 */
package org.apache.tuscany.container.spring;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.MalformedURLException;
import java.io.File;
import java.io.IOException;
import java.io.FileWriter;

import org.apache.tuscany.spi.builder.BuilderRegistry;
import org.apache.tuscany.spi.builder.Connector;
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

import org.apache.tuscany.container.spring.mock.TestBean;
import org.apache.tuscany.container.spring.mock.TestBeanImpl;
import org.apache.tuscany.container.spring.mock.VMBinding;
import org.apache.tuscany.test.ArtifactFactory;
import org.jmock.Mock;
import org.jmock.MockObjectTestCase;
import org.jmock.core.Constraint;
import org.jmock.core.Formatting;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.StaticApplicationContext;

/**
 * @version $$Rev$$ $$Date$$
 */
public class SpringCompositeBuilderTestCase extends MockObjectTestCase {
    private final String appXml =
        "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
        "<beans xmlns=\"http://www.springframework.org/schema/beans\"\n" +
        "       xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
        "       xsi:schemaLocation=\"\n" +
        "http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd\n" +
        "\">\n" +
        "\n" +
        "\n" +
        "<bean id=\"fooBean\" class=\"org.apache.tuscany.container.spring.mock.TestBeanImpl\">\n" +
        "</bean>\n" +
        "\n" +
        "</beans>\n";

    public void testBuildImplicit() throws Exception {

        // Create an assembly model consisting of a component implemented by Spring
        SpringImplementation impl = new SpringImplementation(createComponentType());
        File tempAppXmlFile = createTempApplicationXml();
        tempAppXmlFile.deleteOnExit();
        impl.setApplicationXml(tempAppXmlFile.toURL());
        ComponentDefinition<SpringImplementation> componentDefinition =
            new ComponentDefinition<SpringImplementation>("spring", impl);

        // Create a service instance that the mock builder registry will return
        WireService wireService = ArtifactFactory.createWireService();
        ServiceExtension<TestBean> serviceContext =
            new ServiceExtension<TestBean>("fooService", TestBean.class, null, wireService);
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
        mock.expects(atLeastOnce()).method("build").with(ANYTHING, serviceIsNamed("fooService"), ANYTHING)
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

    private File createTempApplicationXml() throws IOException, MalformedURLException {
        File tempAppXml = File.createTempFile("SpringCompositeBuilderTestCase", ".xml");
        FileWriter fw = new FileWriter(tempAppXml);
        fw.write(appXml);
        fw.flush();
        return tempAppXml;
    }

    /**
     * JMock constraint class to test ServiceDefinition name
     */
    private class ServiceIsNamed implements Constraint {
        private String name;

        public ServiceIsNamed(String name) {
            this.name = name;
        }

        public boolean eval(Object o) {
            return o instanceof ServiceDefinition && ((ServiceDefinition) o).getName().equals(name);
        }

        public StringBuffer describeTo(StringBuffer buffer) {
            return buffer.append("a service named ")
                .append(Formatting.toReadableString(name));
        }
    }

    /**
     * JMock factory method for ServiceIsNamed constraint
     */
    private Constraint serviceIsNamed(String name) {
        return new ServiceIsNamed(name);
    }
}
