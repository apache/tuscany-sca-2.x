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

import java.lang.reflect.Type;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;
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
import org.apache.tuscany.spi.QualifiedName;
import org.apache.tuscany.spi.annotation.Autowire;
import org.apache.tuscany.spi.component.CompositeComponent;
import org.apache.tuscany.spi.databinding.extension.DOMHelper;
import org.apache.tuscany.spi.deployer.DeploymentContext;
import org.apache.tuscany.spi.extension.LoaderExtension;
import org.apache.tuscany.spi.loader.IllegalSCDLNameException;
import org.apache.tuscany.spi.loader.InvalidReferenceException;
import org.apache.tuscany.spi.loader.InvalidValueException;
import org.apache.tuscany.spi.loader.LoaderException;
import org.apache.tuscany.spi.loader.LoaderRegistry;
import org.apache.tuscany.spi.loader.LoaderUtil;
import org.apache.tuscany.spi.loader.MissingImplementationException;
import org.apache.tuscany.spi.loader.MissingMustOverridePropertyException;
import org.apache.tuscany.spi.loader.MissingReferenceException;
import org.apache.tuscany.spi.loader.NotOverridablePropertyException;
import org.apache.tuscany.spi.loader.PropertyObjectFactory;
import org.apache.tuscany.spi.loader.ReferenceMultiplicityViolationException;
import org.apache.tuscany.spi.loader.UndefinedPropertyException;
import org.apache.tuscany.spi.loader.UndefinedReferenceException;
import org.apache.tuscany.spi.loader.UnrecognizedElementException;
import org.apache.tuscany.spi.model.BindingDefinition;
import org.apache.tuscany.spi.model.ComponentDefinition;
import org.apache.tuscany.spi.model.ComponentType;
import org.apache.tuscany.spi.model.CompositeComponentType;
import org.apache.tuscany.spi.model.Implementation;
import org.apache.tuscany.spi.model.ModelObject;
import org.apache.tuscany.spi.model.Multiplicity;
import org.apache.tuscany.spi.model.OverrideOptions;
import org.apache.tuscany.spi.model.Property;
import org.apache.tuscany.spi.model.PropertyValue;
import org.apache.tuscany.spi.model.ReferenceDefinition;
import org.apache.tuscany.spi.model.ReferenceTarget;
import org.apache.tuscany.spi.model.ServiceDefinition;
import org.apache.tuscany.spi.util.stax.StaxUtil;

import org.apache.tuscany.core.binding.local.LocalBindingDefinition;
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

    @Constructor
    public ComponentLoader(@Autowire LoaderRegistry registry, @Autowire PropertyObjectFactory propertyFactory) {
        super(registry);
        this.propertyFactory = propertyFactory;
    }

    public QName getXMLType() {
        return COMPONENT;
    }

    @SuppressWarnings("unchecked")
    public ComponentDefinition<?> load(CompositeComponent parent,
                                       ModelObject object,
                                       XMLStreamReader reader,
                                       DeploymentContext deploymentContext) throws XMLStreamException, LoaderException {
        assert COMPONENT.equals(reader.getName());
        String name = reader.getAttributeValue(null, "name");
        String initLevel = reader.getAttributeValue(null, "initLevel");

        try {
            Implementation<?> impl = loadImplementation(parent, reader, deploymentContext);
            deploymentContext.getPathNames().add(name);
            registry.loadComponentType(parent, impl, deploymentContext);
            deploymentContext.getPathNames().remove(deploymentContext.getPathNames().size() - 1);

            URI uri;
            try {
                StringBuilder buf = new StringBuilder();
                for (String path : deploymentContext.getPathNames()) {
                    buf.append(path);
                    if (path.charAt(path.length() - 1) != '/') {
                        buf.append('/');
                    }
                }
                uri = new URI(buf + name);
            } catch (URISyntaxException e) {
                throw new IllegalSCDLNameException(e);
            }
            ComponentDefinition<Implementation<?>> componentDefinition =
                new ComponentDefinition<Implementation<?>>(uri, impl);

            if (initLevel != null) {
                if (initLevel.length() == 0) {
                    componentDefinition.setInitLevel(0);
                } else {
                    try {
                        componentDefinition.setInitLevel(Integer.valueOf(initLevel));
                    } catch (NumberFormatException e) {
                        throw new InvalidValueException(initLevel, "initValue", e);
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
                        } else {
                            throw new UnrecognizedElementException(qname);
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
                            ComponentType<ServiceDefinition, ReferenceDefinition, Property<?>> type =
                                (ComponentType<ServiceDefinition, ReferenceDefinition, Property<?>>) componentDefinition
                                    .getImplementation().getComponentType();
                            for (ReferenceDefinition ref : type.getReferences().values()) {
                                if (ref.isAutowire()) {
                                    ReferenceTarget referenceTarget = new ReferenceTarget();
                                    String compName = componentDefinition.getUri().toString();
                                    URI refName = URI.create(compName + ref.getUri().toString());
                                    referenceTarget.setReferenceName(refName);
                                    componentDefinition.add(referenceTarget);
                                }
                            }
                            validate(componentDefinition);
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
        ModelObject o = registry.load(parent, null, reader, deploymentContext);
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
        Property<Type> property = (Property<Type>) componentType.getProperties().get(name);
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
            propertyValue.setValue(property.getDefaultValue());
            LoaderUtil.skipToEndElement(reader);
        } else {
            try {
                DocumentBuilder documentBuilder = DOMHelper.newDocumentBuilder();
                Document value = StaxUtil.createPropertyValue(reader, property.getXmlType(), documentBuilder);
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
        if (name == null) {
            throw new InvalidReferenceException("No name specified");
        } else if (target == null) {
            throw new InvalidReferenceException("No target specified", name);
        }
        URI targetURI;
        QualifiedName qName = new QualifiedName(target);
        List<String> names = deploymentContext.getPathNames();
        String path;
        if (names.size() == 0) {
            path = "/";
        } else {
            StringBuilder buf = new StringBuilder();
            for (int i = 0; i < names.size() - 1; i++) {
                buf.append(names.get(i)).append("/");
            }
            buf.append(names.get(names.size() - 1));
            if (buf.charAt(buf.length() - 1) != '/') {
                buf.append('/');
            }
            path = buf.toString();
        }
        try {
            URI uri = new URI(path);
            targetURI = uri.resolve(qName.getPartName());
            if (qName.getPortName() != null) {
                targetURI = targetURI.resolve('#' + qName.getPortName());
            }
        } catch (URISyntaxException e) {
            throw new InvalidReferenceException("Illegal URI", name, e);
        }
        Implementation<?> impl = componentDefinition.getImplementation();
        ComponentType<?, ?, ?> componentType = impl.getComponentType();
        if (!componentType.getReferences().containsKey(name)) {
            throw new UndefinedReferenceException(name);
        }
        if (componentType instanceof CompositeComponentType) {
            ReferenceDefinition definition = componentType.getReferences().get(name);
            if (definition.getBindings().isEmpty()) {
                // TODO JFM allow selection of a default binding
                LocalBindingDefinition binding = new LocalBindingDefinition(targetURI);
                definition.addBinding(binding);
            } else {
                for (BindingDefinition binding : definition.getBindings()) {
                    // FIXME this is bad - clarify in the spec how URIs are overriden
                    binding.setTargetUri(targetURI);
                }
            }
        } else {
            ReferenceTarget referenceTarget = componentDefinition.getReferenceTargets().get(name);
            if (referenceTarget == null) {
                referenceTarget = new ReferenceTarget();
                try {
                    referenceTarget.setReferenceName(new URI(path + "#" + name));
                } catch (URISyntaxException e) {
                    throw new IllegalSCDLNameException(e);
                }
                componentDefinition.add(referenceTarget);
            }
            referenceTarget.addTarget(targetURI);
        }
    }

    @SuppressWarnings("unchecked")
    protected void populatePropertyValues(ComponentDefinition<Implementation<?>> componentDefinition)
        throws MissingMustOverridePropertyException {
        ComponentType componentType = componentDefinition.getImplementation().getComponentType();
        if (componentType != null) {
            Map<String, Property<?>> properties = componentType.getProperties();
            Map<String, PropertyValue<?>> propertyValues = componentDefinition.getPropertyValues();

            for (Property<?> aProperty : properties.values()) {
                if (propertyValues.get(aProperty.getName()) == null) {
                    if (aProperty.getOverride() == OverrideOptions.MUST) {
                        throw new MissingMustOverridePropertyException(aProperty.getName());
                    } else if (aProperty.getDefaultValue() != null) {
                        PropertyValue propertyValue = new PropertyValue();
                        propertyValue.setName(aProperty.getName());
                        propertyValue.setValue(aProperty.getDefaultValue());
                        propertyValue.setValueFactory(new SimplePropertyObjectFactory(aProperty,
                            propertyValue.getValue()));
                        propertyValues.put(aProperty.getName(), propertyValue);
                    }
                }
            }
        }
    }

    /**
     * Validates a component definition, ensuring all component type configuration elements are satisfied
     */
    protected void validate(ComponentDefinition<Implementation<?>> definition) throws LoaderException {
        // validate refererences
        Implementation<?> implementation = definition.getImplementation();
        ComponentType<?, ?, ?> type = implementation.getComponentType();
        if (type == null) {
            return;
        }
        for (ReferenceDefinition referenceDef : type.getReferences().values()) {
            if (referenceDef.isAutowire() || !referenceDef.isRequired()) {
                continue;
            }
            String name = referenceDef.getUri().getFragment();
            ReferenceTarget target = definition.getReferenceTargets().get(name);
            if (target == null) {
                throw new MissingReferenceException(name);
            }
            int count = target.getTargets().size();
            Multiplicity multiplicity = referenceDef.getMultiplicity();
            switch (multiplicity) {
                case ZERO_N:
                    break;
                case ZERO_ONE:
                    if (count > 1) {
                        throw new ReferenceMultiplicityViolationException(name, multiplicity, count);
                    }
                    break;
                case ONE_ONE:
                    if (count != 1) {
                        throw new ReferenceMultiplicityViolationException(name, multiplicity, count);
                    }
                    break;
                case ONE_N:
                    if (count < 1) {
                        throw new ReferenceMultiplicityViolationException(name, multiplicity, count);
                    }
                    break;
            }

        }
    }
}
