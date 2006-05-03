/**
 *
 * Copyright 2005 The Apache Software Foundation
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
package org.apache.tuscany.core.loader;

import javax.xml.namespace.QName;
import static javax.xml.stream.XMLStreamConstants.END_ELEMENT;
import static javax.xml.stream.XMLStreamConstants.START_ELEMENT;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.apache.tuscany.model.Component;
import org.apache.tuscany.model.Implementation;
import org.apache.tuscany.model.ModelObject;
import org.apache.tuscany.spi.annotation.Autowire;
import org.apache.tuscany.spi.loader.LoaderContext;
import org.apache.tuscany.spi.loader.LoaderException;
import org.apache.tuscany.spi.loader.LoaderSupport;
import org.apache.tuscany.spi.loader.MissingImplementationException;
import org.apache.tuscany.spi.loader.StAXPropertyFactory;
import org.osoa.sca.annotations.Scope;

/**
 * @version $Rev$ $Date$
 */
@Scope("MODULE")
public class ComponentLoader extends LoaderSupport {
    private StAXPropertyFactory<?> defaultPropertyFactory;

    @Autowire
    public void setDefaultPropertyFactory(StAXPropertyFactory<?> defaultPropertyFactory) {
        this.defaultPropertyFactory = defaultPropertyFactory;
    }

    public QName getXMLType() {
        return AssemblyConstants.COMPONENT;
    }

    public Component<?> load(XMLStreamReader reader, LoaderContext loaderContext) throws XMLStreamException, LoaderException {
        assert AssemblyConstants.COMPONENT.equals(reader.getName());
        String name = reader.getAttributeValue(null, "name");
        reader.nextTag();
        ModelObject o = registry.load(reader, loaderContext);
        if (!(o instanceof Implementation)) {
            MissingImplementationException e = new MissingImplementationException();
            e.setIdentifier(name);
            throw e;
        }
        Implementation<?> impl = (Implementation<?>) o;
        Component<?> component = impl.newComponent();
        component.setName(name);

        while (true) {
            switch (reader.next()) {
                case START_ELEMENT:
                    QName qname = reader.getName();
/*
                    if (AssemblyConstants.PROPERTIES.equals(qname)) {
                    loadProperties(reader, loaderContext.getResourceLoader(), component);
                    } else if (AssemblyConstants.REFERENCES.equals(qname)) {
                    loadReferences(reader, component);
                    }
*/
                    reader.next();
                    break;
                case END_ELEMENT:
                    return component;
            }
        }
    }

/*
    protected void loadProperties(XMLStreamReader reader, ResourceLoader resourceLoader, Component<?> component) throws XMLStreamException, ConfigurationLoadException {
        ComponentInfo componentType = component.getImplementation().getComponentInfo();
        List<ConfiguredProperty> configuredProperties = component.getConfiguredProperties();

        while (true) {
            switch (reader.next()) {
            case START_ELEMENT:
                String name = reader.getLocalName();
                Property property = componentType.getProperty(name);
                if (property == null) {
                    throw new ConfigurationLoadException(name);
                }
                OverrideOption override = StAXUtil.overrideOption(reader.getAttributeValue(null, "override"), OverrideOption.NO);

                // get a factory for the property
                StAXPropertyFactory<?> propertyFactory;
                String factoryName = reader.getAttributeValue(null, "factory");
                if (factoryName == null) {
                    propertyFactory = defaultPropertyFactory;
                } else {
                    propertyFactory = getPropertyFactory(factoryName, resourceLoader);
                }

                // create the property value
                // FIXME to support complex types we probably should store the factory in the ConfiguredProperty
                // FIXME instead of the value as the value may be mutable and should not be shared between instances
                ObjectFactory<?> objectFactory = propertyFactory.createObjectFactory(reader, property);
                Object value = objectFactory.getInstance();

                // create the configured property definition
                ConfiguredProperty configuredProperty = factory.createConfiguredProperty();
                configuredProperty.setName(name);
                configuredProperty.setValue(value);
                configuredProperty.setOverrideOption(override);
                configuredProperties.add(configuredProperty);
                break;
            case END_ELEMENT:
                return;
            }
        }
    }
*/

/*
    protected StAXPropertyFactory<?> getPropertyFactory(String factoryName, ResourceLoader resourceLoader) throws InvalidPropertyFactoryException {
        Class<?> impl;
        try {
            // try to load factory from application classloader
            impl = resourceLoader.loadClass(factoryName);
        } catch (ClassNotFoundException e) {
            try {
                // try to load factory from container classloader
                impl = Class.forName(factoryName);
            } catch (ClassNotFoundException e1) {
                throw new InvalidPropertyFactoryException(factoryName, e);
            }
        }
        try {
            return (StAXPropertyFactory<?>) impl.newInstance();
        } catch (InstantiationException e) {
            throw new InvalidPropertyFactoryException(factoryName, e);
        } catch (IllegalAccessException e) {
            throw new InvalidPropertyFactoryException(factoryName, e);
        } catch (ClassCastException e) {
            throw new InvalidPropertyFactoryException(factoryName, e);
        }
    }
*/

/*
    protected void loadReferences(XMLStreamReader reader, Component<?> component) throws XMLStreamException {
        List<ConfiguredReference> configuredReferences = component.getConfiguredReferences();
        while (true) {
            switch (reader.next()) {
            case START_ELEMENT:
                String name = reader.getLocalName();
                String uri = reader.getElementText();

                ConfiguredReference configuredReference = component.getConfiguredReference(name);
                if (configuredReference == null) {
                    configuredReference = factory.createConfiguredReference();
                    configuredReference.setName(name);
                    configuredReferences.add(configuredReference);
                }

                configuredReference.getTargets().add(uri);
                break;
            case END_ELEMENT:
                return;
            }
        }
    }
*/
}
