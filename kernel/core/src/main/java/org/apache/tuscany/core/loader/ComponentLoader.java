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
package org.apache.tuscany.core.loader;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;
import java.lang.reflect.Type;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;
import static javax.xml.stream.XMLStreamConstants.END_ELEMENT;
import static javax.xml.stream.XMLStreamConstants.START_ELEMENT;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.w3c.dom.Document;
import static org.osoa.sca.Version.XML_NAMESPACE_1_0;
import org.osoa.sca.annotations.Constructor;

import org.apache.tuscany.spi.ObjectFactory;
import org.apache.tuscany.spi.annotation.Autowire;
import org.apache.tuscany.spi.component.CompositeComponent;
import org.apache.tuscany.spi.databinding.extension.DOMHelper;
import org.apache.tuscany.spi.deployer.DeploymentContext;
import org.apache.tuscany.spi.extension.LoaderExtension;
import org.apache.tuscany.spi.loader.InvalidReferenceException;
import org.apache.tuscany.spi.loader.InvalidValueException;
import org.apache.tuscany.spi.loader.LoaderException;
import org.apache.tuscany.spi.loader.LoaderRegistry;
import org.apache.tuscany.spi.loader.LoaderUtil;
import org.apache.tuscany.spi.loader.MissingImplementationException;
import org.apache.tuscany.spi.loader.MissingMustOverridePropertyException;
import org.apache.tuscany.spi.loader.NotOverridablePropertyException;
import org.apache.tuscany.spi.loader.PropertyObjectFactory;
import org.apache.tuscany.spi.loader.UndefinedPropertyException;
import org.apache.tuscany.spi.model.ComponentDefinition;
import org.apache.tuscany.spi.model.ComponentType;
import org.apache.tuscany.spi.model.Implementation;
import org.apache.tuscany.spi.model.ModelObject;
import org.apache.tuscany.spi.model.OverrideOptions;
import org.apache.tuscany.spi.model.Property;
import org.apache.tuscany.spi.model.PropertyValue;
import org.apache.tuscany.spi.model.ReferenceTarget;

import org.apache.tuscany.core.implementation.system.model.SystemImplementation;
import org.apache.tuscany.core.property.SimplePropertyObjectFactory;

/**
 * Loads a component definition from an XML-based assembly file
 *
 * @version $Rev$ $Date$
 */
public class ComponentLoader extends LoaderExtension<ComponentDefinition<?>> {
    private static final QName COMPONENT = new QName(XML_NAMESPACE_1_0, "component");
    private static final QName PROPERTY = new QName(XML_NAMESPACE_1_0, "property");
    private static final QName REFERENCE = new QName(XML_NAMESPACE_1_0, "reference");

    private static final String PROPERTY_FILE_ATTR = "file";
    private static final String PROPERTY_NAME_ATTR = "name";
    private static final String PROPERTY_SOURCE_ATTR = "source";

    private PropertyObjectFactory propertyFactory;

    @Constructor({"registry", "propertyFactory"})
    public ComponentLoader(@Autowire LoaderRegistry registry, @Autowire PropertyObjectFactory propertyFactory) {
        super(registry);
        this.propertyFactory = propertyFactory;
    }

    @SuppressWarnings("unchecked")
    private void populatePropertyValues(ComponentDefinition<Implementation<?>> componentDefinition)
        throws MissingMustOverridePropertyException {
        ComponentType componentType = componentDefinition.getImplementation().getComponentType();
        if (componentType != null) {
            Map<String, Property<?>> properties = componentType.getProperties();
            Map<String, PropertyValue<?>> propertyValues = componentDefinition.getPropertyValues();

            for (Property<?> aProperty : properties.values()) {
                if (propertyValues.get(aProperty.getName()) == null) {
                    if (aProperty.getOverride() == OverrideOptions.MUST) {
                        throw new MissingMustOverridePropertyException(aProperty.getName());
                    } else {
                        PropertyValue propertyValue = new PropertyValue();
                        propertyValue.setName(aProperty.getName());
                        propertyValue.setValue(aProperty.getDefaultValue());
                        // propertyValue.setValueFactory(aProperty.getDefaultValueFactory());
                        propertyValue.setValueFactory(new SimplePropertyObjectFactory(aProperty,
                            propertyValue
                                .getValue()));
                        propertyValues.put(aProperty.getName(), propertyValue);
                    }
                }
            }
        }
    }

    public QName getXMLType() {
        return COMPONENT;
    }

    public ComponentDefinition<?> load(CompositeComponent parent,
                                       XMLStreamReader reader,
                                       DeploymentContext deploymentContext) throws XMLStreamException,
                                                                                   LoaderException {
        assert COMPONENT.equals(reader.getName());
        String name = reader.getAttributeValue(null, "name");
        String initLevel = reader.getAttributeValue(null, "initLevel");

        try {
            Implementation<?> impl = loadImplementation(parent, reader, deploymentContext);
            registry.loadComponentType(parent, impl, deploymentContext);

            ComponentDefinition<Implementation<?>> componentDefinition =
                new ComponentDefinition<Implementation<?>>(name, impl);

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
                        if (reader.getName().equals(COMPONENT)) {
                            // hack to leave alone SystemImplementation
                            if (!((Implementation) componentDefinition
                                .getImplementation() instanceof SystemImplementation)) {
                                populatePropertyValues(componentDefinition);
                            }

                            return componentDefinition;
                        }
                        break;
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

    @SuppressWarnings("unchecked")
    protected void loadProperty(XMLStreamReader reader,
                                DeploymentContext deploymentContext,
                                ComponentDefinition<?> componentDefinition) throws XMLStreamException,
                                                                                   LoaderException {
        String name = reader.getAttributeValue(null, PROPERTY_NAME_ATTR);
        Implementation<?> implementation = componentDefinition.getImplementation();
        ComponentType<?, ?, ?> componentType = implementation.getComponentType();
        Property<Type> property = (Property<Type>)componentType.getProperties().get(name);
        if (property == null) {
            throw new UndefinedPropertyException(name);
        } else if (OverrideOptions.NO.equals(property.getOverride())) {
            throw new NotOverridablePropertyException(name);
        }
        PropertyValue<Type> propertyValue;
        String source = reader.getAttributeValue(null, PROPERTY_SOURCE_ATTR);
        String file = reader.getAttributeValue(null, PROPERTY_FILE_ATTR);
        if (source != null || file != null) {
            propertyValue = new PropertyValue<Type>(name, source, file);
            LoaderUtil.skipToEndElement(reader);
        } else {
            try {
                DocumentBuilder documentBuilder = DOMHelper.newDocumentBuilder();
                Document value = StAXUtil.createPropertyValue(reader, property.getXmlType(), documentBuilder);
                propertyValue = new PropertyValue<Type>(name, value);
            } catch (ParserConfigurationException e) {
                throw new LoaderException(e);
            }
        }
        ObjectFactory<Type> objectFactory = propertyFactory.createObjectFactory(property, propertyValue);
        // propertyValue.setValueFactory(new SimplePropertyObjectFactory(property, propertyValue.getValue()));
        propertyValue.setValueFactory(objectFactory);
        componentDefinition.add(propertyValue);
    }

    protected void loadReference(XMLStreamReader reader,
                                 DeploymentContext deploymentContext,
                                 ComponentDefinition<?> componentDefinition) throws XMLStreamException,
                                                                                    LoaderException {
        String name = reader.getAttributeValue(null, "name");
        String text = reader.getElementText();
        String target = text != null ? text.trim() : null;

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

}
