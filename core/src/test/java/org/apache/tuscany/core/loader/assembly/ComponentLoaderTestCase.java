/**
 *
 * Copyright 2006 The Apache Software Foundation
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.apache.tuscany.core.loader.assembly;

import java.util.List;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.apache.tuscany.spi.ObjectFactory;
import org.apache.tuscany.core.config.ComponentTypeIntrospector;
import org.apache.tuscany.core.config.ConfigurationException;
import org.apache.tuscany.core.config.ConfigurationLoadException;
import org.apache.tuscany.core.extension.config.ImplementationProcessor;
import org.apache.tuscany.core.config.impl.Java5ComponentTypeIntrospector;
import org.apache.tuscany.core.config.processor.ProcessorUtils;
import org.apache.tuscany.core.injection.SingletonObjectFactory;
import org.apache.tuscany.core.loader.StAXPropertyFactory;
import org.apache.tuscany.core.loader.impl.StringParserPropertyFactory;
import org.apache.tuscany.core.system.assembly.SystemImplementation;
import org.apache.tuscany.model.assembly.Component;
import org.apache.tuscany.model.assembly.ConfiguredProperty;
import org.apache.tuscany.model.assembly.Property;
import org.apache.tuscany.model.assembly.AtomicComponent;

/**
 * @version $Rev$ $Date$
 */
public class ComponentLoaderTestCase extends LoaderTestSupport {
    private ComponentLoader loader;
    private ComponentTypeIntrospector introspector;

    public void testStringProperty() throws XMLStreamException, ConfigurationLoadException {
        String xml = "<properties><propString>HelloWorld</propString></properties>";
        Component component = createFooComponent();
        loadProperties(xml, component);
        ConfiguredProperty prop = component.getConfiguredProperty("propString");
        assertEquals("HelloWorld", prop.getValue());
    }

    public void testIntProperty() throws XMLStreamException, ConfigurationLoadException {
        String xml = "<properties><propInt>1234</propInt></properties>";
        Component component = createFooComponent();
        loadProperties(xml, component);
        ConfiguredProperty prop = component.getConfiguredProperty("propInt");
        assertEquals(1234, prop.getValue());
    }

    public void testIntegerProperty() throws XMLStreamException, ConfigurationLoadException {
        String xml = "<properties><propInteger>1234</propInteger></properties>";
        Component component = createFooComponent();
        loadProperties(xml, component);
        ConfiguredProperty prop = component.getConfiguredProperty("propInteger");
        assertEquals(Integer.valueOf(1234), prop.getValue());
    }

    public void testCustomProperty() throws XMLStreamException, ConfigurationLoadException {
        String xml = "<properties><propFoo factory='" + FooFactory.class.getName() + "'><name>Hello</name></propFoo></properties>";
        Component component = createFooComponent();
        loadProperties(xml, component);
        ConfiguredProperty prop = component.getConfiguredProperty("propFoo");
        Foo instance = (Foo) prop.getValue();
        assertEquals("Hello", instance.name);
    }

    private void loadProperties(String xml, Component component) throws XMLStreamException, ConfigurationLoadException {
        XMLStreamReader reader = getReader(xml);
        loader.loadProperties(reader, resourceLoader, component);
        component.initialize(modelContext);
    }

    private Component createFooComponent() {
        SystemImplementation impl = assemblyFactory.createSystemImplementation();
        impl.setImplementationClass(ServiceImpl.class);
        try {
            impl.setComponentInfo(introspector.introspect(ServiceImpl.class));
        } catch (ConfigurationException e) {
            throw new AssertionError();
        }
        impl.initialize(null);
        AtomicComponent component = assemblyFactory.createSimpleComponent();
        component.setImplementation(impl);
        return component;
    }

    protected void setUp() throws Exception {
        super.setUp();
        loader = new ComponentLoader();
        loader.setFactory(assemblyFactory);
        loader.setDefaultPropertyFactory(new StringParserPropertyFactory());
        introspector = new Java5ComponentTypeIntrospector(assemblyFactory);
        //FIXME JFM HACK
        List<ImplementationProcessor> processors = ProcessorUtils.createCoreProcessors(assemblyFactory);
        for (ImplementationProcessor processor : processors) {
            introspector.registerProcessor(processor);
        }
        // END hack

    }

    public static interface Service {
    }

    public static class ServiceImpl implements Service {
        public String propString;
        public int propInt;
        public Integer propInteger;
        public Foo propFoo;
    }

    public static class Foo {
        public Foo() {
        }

        private String name;
        private Foo foo;

        public void setName(String val) {
            name = val;
        }

        public void setFoo(Foo val) {
            foo = val;
        }
/*

        private MyJaxBThing jaxBThing;

        public void setMyJaxBThing(MyJaxBThing thing) {
            jaxBthing = thing;
        }
*/
    }

    public static class FooFactory implements StAXPropertyFactory<Foo> {
        public ObjectFactory<Foo> createObjectFactory(XMLStreamReader reader, Property property) throws XMLStreamException, ConfigurationLoadException {
            reader.nextTag();
            String name = reader.getElementText();
            reader.next();
            Foo foo = new Foo();
            foo.setName(name);
            return new SingletonObjectFactory<Foo>(foo);
        }
    }
}
