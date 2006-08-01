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

import java.net.URI;
import java.net.URISyntaxException;
import javax.xml.namespace.QName;
import static javax.xml.stream.XMLStreamConstants.END_ELEMENT;
import static javax.xml.stream.XMLStreamConstants.START_ELEMENT;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import static org.osoa.sca.Version.XML_NAMESPACE_1_0;
import org.osoa.sca.annotations.Constructor;

import org.apache.tuscany.spi.ObjectFactory;
import org.apache.tuscany.spi.annotation.Autowire;
import org.apache.tuscany.spi.component.CompositeComponent;
import org.apache.tuscany.spi.deployer.DeploymentContext;
import org.apache.tuscany.spi.extension.LoaderExtension;
import org.apache.tuscany.spi.loader.InvalidReferenceException;
import org.apache.tuscany.spi.loader.InvalidValueException;
import org.apache.tuscany.spi.loader.LoaderException;
import org.apache.tuscany.spi.loader.LoaderRegistry;
import org.apache.tuscany.spi.loader.MissingImplementationException;
import org.apache.tuscany.spi.loader.StAXPropertyFactory;
import org.apache.tuscany.spi.loader.UndefinedPropertyException;
import org.apache.tuscany.spi.model.ComponentDefinition;
import org.apache.tuscany.spi.model.ComponentType;
import org.apache.tuscany.spi.model.Implementation;
import org.apache.tuscany.spi.model.ModelObject;
import org.apache.tuscany.spi.model.Property;
import org.apache.tuscany.spi.model.PropertyValue;
import org.apache.tuscany.spi.model.ReferenceTarget;

/**
 * Loads a component definition from an XML-based assembly file
 *
 * @version $Rev$ $Date$
 */
public class ComponentLoader extends LoaderExtension<ComponentDefinition<?>> {
    private static final QName COMPONENT = new QName(XML_NAMESPACE_1_0, "component");
    private static final QName PROPERTY = new QName(XML_NAMESPACE_1_0, "property");
    private static final QName REFERENCE = new QName(XML_NAMESPACE_1_0, "reference");

    private StAXPropertyFactory defaultPropertyFactory;

    @Constructor({"registry", "defaultPropertyFactory"})
    public ComponentLoader(@Autowire LoaderRegistry registry,
                           @Autowire StAXPropertyFactory propertyFactory) {
        super(registry);
        this.defaultPropertyFactory = propertyFactory;
    }

    public QName getXMLType() {
        return COMPONENT;
    }

    public ComponentDefinition<?> load(CompositeComponent parent,
                                       XMLStreamReader reader,
                                       DeploymentContext deploymentContext)
        throws XMLStreamException, LoaderException {
        assert COMPONENT.equals(reader.getName());
        String name = reader.getAttributeValue(null, "name");
        String initLevel = reader.getAttributeValue(null, "initLevel");

        try {
            Implementation<?> impl = loadImplementation(parent, reader, deploymentContext);
            registry.loadComponentType(parent, impl, deploymentContext);

            ComponentDefinition<?> componentDefinition = new ComponentDefinition<Implementation<?>>(name, impl);

            if (initLevel != null) {
                if (initLevel.length() == 0) {
                    componentDefinition.setInitLevel(0);
                } else {
                    try {
                        componentDefinition.setInitLevel(Integer.valueOf(initLevel));
                    } catch (NumberFormatException e) {
                        InvalidValueException ive = new InvalidValueException(initLevel, e);
                        ive.setIdentifier("initValue");
                        ive.addContextName(name);
                        throw ive;
                    }
                }
            }

            while (true) {
                switch (reader.next()) {
                    case START_ELEMENT:
                        QName qname = reader.getName();
                        if (PROPERTY.equals(qname)) {
                            loadProperty(reader, deploymentContext, componentDefinition);
                        } else if (REFERENCE.equals(qname)) {
                            loadReference(reader, deploymentContext, componentDefinition);
                        }
                        reader.next();
                        break;
                    case END_ELEMENT:
                        return componentDefinition;
                }
            }
        } catch (LoaderException e) {
            e.addContextName(name);
            throw e;
        }
    }

    protected Implementation<?> loadImplementation(CompositeComponent parent,
                                                   XMLStreamReader reader,
                                                   DeploymentContext deploymentContext)
        throws XMLStreamException, LoaderException {
        reader.nextTag();
        ModelObject o = registry.load(parent, reader, deploymentContext);
        if (!(o instanceof Implementation)) {
            throw new MissingImplementationException();
        }
        return (Implementation<?>) o;
    }

    protected void loadProperty(XMLStreamReader reader, DeploymentContext deploymentContext,
                                ComponentDefinition<?> componentDefinition)
        throws XMLStreamException, LoaderException {
        String name = reader.getAttributeValue(null, "name");
        Implementation<?> implementation = componentDefinition.getImplementation();
        ComponentType<?, ?, ?> componentType = implementation.getComponentType();
        Property<?> property = componentType.getProperties().get(name);
        if (property == null) {
            throw new UndefinedPropertyException(name);
        }
        componentDefinition.add(createPropertyValue(reader, property, name));
    }

    private <T> PropertyValue<T> createPropertyValue(XMLStreamReader reader,
                                                     Property<T> property,
                                                     String name) throws XMLStreamException, LoaderException {
        // todo allow property to specify the factory to use
        ObjectFactory<T> factory = defaultPropertyFactory.createObjectFactory(reader, property);
        return new PropertyValue<T>(name, factory);
    }

    protected void loadReference(XMLStreamReader reader,
                                 DeploymentContext deploymentContext,
                                 ComponentDefinition<?> componentDefinition)
        throws XMLStreamException, LoaderException {
        String name = reader.getAttributeValue(null, "name");
        String target = reader.getAttributeValue(null, "target");

        if (name == null || target == null) {
            InvalidReferenceException le = new InvalidReferenceException();
            le.setIdentifier(target);
            throw le;
        }

        ReferenceTarget referenceTarget = new ReferenceTarget();
        referenceTarget.setReferenceName(name);
        try {
            referenceTarget.addTarget(new URI(target));
        } catch (URISyntaxException e) {
            InvalidReferenceException le = new InvalidReferenceException(e);
            le.setIdentifier(target);
            throw le;
        }
        componentDefinition.add(referenceTarget);
    }

/*
    protected StAXPropertyFactory<?> getPropertyFactory(String factoryName,
        ResourceLoader resourceLoader) throws InvalidPropertyFactoryException {
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
}
