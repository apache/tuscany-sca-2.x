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
package org.apache.tuscany.core.loader.assembly;

import java.util.List;
import javax.xml.namespace.QName;
import static javax.xml.stream.XMLStreamConstants.END_ELEMENT;
import static javax.xml.stream.XMLStreamConstants.START_ELEMENT;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.apache.tuscany.common.resource.ResourceLoader;
import org.apache.tuscany.core.builder.ObjectFactory;
import org.apache.tuscany.core.config.ConfigurationLoadException;
import org.apache.tuscany.core.loader.InvalidPropertyFactoryException;
import org.apache.tuscany.core.loader.LoaderContext;
import org.apache.tuscany.core.loader.StAXPropertyFactory;
import org.apache.tuscany.core.loader.StAXUtil;
import static org.apache.tuscany.core.loader.assembly.AssemblyConstants.*;
import org.apache.tuscany.core.system.annotation.Autowire;
import org.apache.tuscany.model.assembly.AssemblyObject;
import org.apache.tuscany.model.assembly.Component;
import org.apache.tuscany.model.assembly.ComponentType;
import org.apache.tuscany.model.assembly.ConfiguredProperty;
import org.apache.tuscany.model.assembly.ConfiguredReference;
import org.apache.tuscany.model.assembly.Implementation;
import org.apache.tuscany.model.assembly.OverrideOption;
import org.apache.tuscany.model.assembly.Property;
import org.osoa.sca.annotations.Scope;

/**
 * @version $Rev$ $Date$
 */
@Scope("MODULE")
public class ComponentLoader extends AbstractLoader {
    private StAXPropertyFactory<?> defaultPropertyFactory;

    @Autowire
    public void setDefaultPropertyFactory(StAXPropertyFactory<?> defaultPropertyFactory) {
        this.defaultPropertyFactory = defaultPropertyFactory;
    }

    public QName getXMLType() {
        return COMPONENT;
    }

    public Component load(XMLStreamReader reader, LoaderContext loaderContext) throws XMLStreamException, ConfigurationLoadException {
        assert COMPONENT.equals(reader.getName());

        Component component = factory.createSimpleComponent();
        component.setName(reader.getAttributeValue(null, "name"));

        while (true) {
            switch (reader.next()) {
                case START_ELEMENT:
                    QName name = reader.getName();
                    if (PROPERTIES.equals(name)) {
                        loadProperties(reader, loaderContext.getResourceLoader(), component);
                    } else if (REFERENCES.equals(name)) {
                        loadReferences(reader, component);
                    } else {
                        AssemblyObject o = registry.load(reader, loaderContext);
                        if (o instanceof Implementation) {
                            Implementation impl = (Implementation) o;
                            impl.initialize(registry.getContext());
                            component.setImplementation(impl);
                        }
                    }
                    reader.next();
                    break;
                case END_ELEMENT:
                    List<Property> props = component.getImplementation().getComponentType().getProperties();
                    for (Property property : props) {
                        if (property.isRequired()) {
                            if (component.getConfiguredProperty(property.getName()) == null) {
                                ConfigurationLoadException e = new ConfigurationLoadException("Required property not configured");
                                e.setIdentifier(property.getName());
                                throw e;
                            }
                        }
                    }
                    return component;
            }
        }
    }

    protected void loadProperties(XMLStreamReader reader, ResourceLoader resourceLoader, Component<?> component) throws XMLStreamException, ConfigurationLoadException {
        ComponentType componentType = component.getImplementation().getComponentType();
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
}
