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

import org.apache.tuscany.spi.ObjectFactory;
import org.apache.tuscany.model.Component;
import org.apache.tuscany.model.ComponentType;
import org.apache.tuscany.model.Implementation;
import org.apache.tuscany.model.ModelObject;
import org.apache.tuscany.model.Property;
import org.apache.tuscany.model.PropertyValue;
import org.apache.tuscany.model.ReferenceTarget;
import org.apache.tuscany.spi.annotation.Autowire;
import org.apache.tuscany.spi.loader.InvalidReferenceException;
import org.apache.tuscany.spi.loader.LoaderContext;
import org.apache.tuscany.spi.loader.LoaderException;
import org.apache.tuscany.spi.extension.LoaderExtension;
import org.apache.tuscany.spi.loader.MissingImplementationException;
import org.apache.tuscany.spi.loader.StAXPropertyFactory;
import org.osoa.sca.annotations.Scope;

/**
 * @version $Rev$ $Date$
 */
@Scope("MODULE")
public class ComponentLoader extends LoaderExtension {
    private StAXPropertyFactory defaultPropertyFactory;

    @Autowire
    public void setDefaultPropertyFactory(StAXPropertyFactory defaultPropertyFactory) {
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
        Component<?> component = new Component<Implementation<?>>(impl);
        component.setName(name);

        while (true) {
            switch (reader.next()) {
                case START_ELEMENT:
                    QName qname = reader.getName();
                    if (AssemblyConstants.PROPERTY.equals(qname)) {
                        loadProperty(reader, loaderContext, component);
                    } else if (AssemblyConstants.REFERENCE.equals(qname)) {
                        loadReference(reader, loaderContext, component);
                    }
                    reader.next();
                    break;
                case END_ELEMENT:
                    return component;
            }
        }
    }

    protected <T> void loadProperty(XMLStreamReader reader, LoaderContext loaderContext, Component<?> component) throws XMLStreamException, LoaderException {
        String name = reader.getAttributeValue(null, "name");
        Implementation<?> implementation = component.getImplementation();
        ComponentType componentType = implementation.getComponentType();
        Property<T> property = (Property<T>) componentType.getProperties().get(name);
        // todo allow property to specify the factory to use
        ObjectFactory<T> factory = defaultPropertyFactory.createObjectFactory(reader, property);
        PropertyValue<T> value = new PropertyValue<T>();
        value.setName(name);
        value.setValueFactory(factory);
        component.add(value);
    }

    protected void loadReference(XMLStreamReader reader, LoaderContext loaderContext, Component<?> component) throws XMLStreamException, LoaderException {
        String name = reader.getAttributeValue(null, "name");
        String target = reader.getAttributeValue(null, "target");
        ReferenceTarget referenceTarget = new ReferenceTarget();
        referenceTarget.setReferenceName(name);
        try {
            referenceTarget.addTarget(new URI(target));
        } catch (URISyntaxException e) {
            InvalidReferenceException le = new InvalidReferenceException(e);
            le.setIdentifier(target);
            throw le;
        }
        component.add(referenceTarget);
    }

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
}
