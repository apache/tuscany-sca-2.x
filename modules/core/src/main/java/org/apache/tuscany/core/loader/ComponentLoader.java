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

import static javax.xml.stream.XMLStreamConstants.END_ELEMENT;
import static javax.xml.stream.XMLStreamConstants.START_ELEMENT;
import static org.osoa.sca.Constants.SCA_NS;

import java.lang.reflect.Type;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.apache.tuscany.core.binding.local.LocalBindingDefinition;
import org.apache.tuscany.core.deployer.ChildDeploymentContext;
import org.apache.tuscany.spi.ObjectFactory;
import org.apache.tuscany.spi.QualifiedName;
import org.apache.tuscany.spi.deployer.DeploymentContext;
import org.apache.tuscany.spi.extension.LoaderExtension;
import org.apache.tuscany.spi.loader.InvalidReferenceException;
import org.apache.tuscany.spi.loader.InvalidValueException;
import org.apache.tuscany.spi.loader.LoaderException;
import org.apache.tuscany.spi.loader.LoaderRegistry;
import org.apache.tuscany.spi.loader.LoaderUtil;
import org.apache.tuscany.spi.loader.MissingImplementationException;
import org.apache.tuscany.spi.loader.MissingReferenceException;
import org.apache.tuscany.spi.loader.MissingRequiredPropertyException;
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
import org.apache.tuscany.spi.model.Property;
import org.apache.tuscany.spi.model.PropertyValue;
import org.apache.tuscany.spi.model.ReferenceDefinition;
import org.apache.tuscany.spi.model.ReferenceTarget;
import org.apache.tuscany.spi.model.ServiceDefinition;
import org.osoa.sca.annotations.Constructor;
import org.osoa.sca.annotations.Reference;
import org.w3c.dom.Document;

/**
 * Loads a component definition from an XML-based assembly file
 *
 * @version $Rev$ $Date$
 */
public class ComponentLoader extends LoaderExtension<ComponentDefinition<?>> {
    private static final QName COMPONENT = new QName(SCA_NS, "component");
    private static final QName PROPERTY = new QName(SCA_NS, "property");
    private static final QName REFERENCE = new QName(SCA_NS, "reference");

    private static final String PROPERTY_FILE_ATTR = "file";
    private static final String PROPERTY_NAME_ATTR = "name";
    private static final String PROPERTY_SOURCE_ATTR = "source";

    private PropertyObjectFactory propertyFactory;

    @Constructor
    public ComponentLoader(@Reference LoaderRegistry registry, @Reference PropertyObjectFactory propertyFactory) {
        super(registry);
        this.propertyFactory = propertyFactory;
    }

    public QName getXMLType() {
        return COMPONENT;
    }

    @SuppressWarnings("unchecked")
    public ComponentDefinition<?> load(ModelObject object, XMLStreamReader reader, DeploymentContext context)
        throws XMLStreamException, LoaderException {
        assert COMPONENT.equals(reader.getName());
        String name = reader.getAttributeValue(null, "name");
        String initLevel = reader.getAttributeValue(null, "initLevel");
        String autowireAttr = reader.getAttributeValue(null, "autowire");
        boolean autowire;
        if (autowireAttr == null) {
            autowire = context.isAutowire();
        } else {
            autowire = Boolean.parseBoolean(autowireAttr);
        }
        String runtimeAttr = reader.getAttributeValue(null, "runtimeId");
        URI runtimeId;
        if (runtimeAttr != null) {
            try {
                runtimeId = new URI(runtimeAttr);
            } catch (URISyntaxException e) {
                throw new InvalidValueException(runtimeAttr, "runtimeId", e);
            }
        } else {
            runtimeId = null;
        }

        URI componentId = URI.create(context.getComponentId() + "/").resolve(name);
        ClassLoader loader = context.getClassLoader();
        URL location = context.getScdlLocation();
        // xcv test
        DeploymentContext childContext = new ChildDeploymentContext(context, loader, location, componentId, autowire);
        Implementation<?> impl = loadImplementation(reader, childContext);
        registry.loadComponentType(impl, childContext);

        ComponentDefinition<Implementation<?>> componentDefinition =
            new ComponentDefinition<Implementation<?>>(componentId, impl);
        componentDefinition.setAutowire(autowire);
        componentDefinition.setRuntimeId(runtimeId);
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
                        loadProperty(reader, componentDefinition, childContext);
                    } else if (REFERENCE.equals(qname)) {
                        loadReference(reader, componentDefinition, childContext);
                    } else {
                        throw new UnrecognizedElementException(qname);
                    }
                    reader.next();
                    break;
                case END_ELEMENT:
                    if (reader.getName().equals(COMPONENT)) {
                        populatePropertyValues(componentDefinition);
                        ComponentType<ServiceDefinition, ReferenceDefinition, Property<?>> type =
                            (ComponentType<ServiceDefinition, ReferenceDefinition, Property<?>>) componentDefinition
                                .getImplementation().getComponentType();

                        for (ReferenceDefinition ref : type.getReferences().values()) {
                            // add reference target definitions if autowire is enabled for references that are not
                            // explicitly configured with autowire by the component
                            if (!componentDefinition.getReferenceTargets().containsKey(ref.getUri().getFragment())) {
                                if (autowire) {
                                    ReferenceTarget referenceTarget = new ReferenceTarget();
                                    String compName = componentDefinition.getUri().toString();
                                    URI refName = URI.create(compName + ref.getUri().toString());
                                    referenceTarget.setReferenceName(refName);
                                    referenceTarget.setAutowire(autowire);
                                    componentDefinition.add(referenceTarget);
                                }
                            }
                        }
                        validate(componentDefinition);
                        return componentDefinition;
                    }
                    break;
            }
        }
    }

    protected Implementation<?> loadImplementation(XMLStreamReader reader, DeploymentContext context)
        throws XMLStreamException, LoaderException {
        reader.nextTag();
        ModelObject o = registry.load(null, reader, context);
        if (!(o instanceof Implementation)) {
            throw new MissingImplementationException();
        }
        return (Implementation<?>) o;
    }

    @SuppressWarnings("unchecked")
    protected void loadProperty(XMLStreamReader reader, ComponentDefinition<?> definition, DeploymentContext context)
        throws XMLStreamException, LoaderException {
        String name = reader.getAttributeValue(null, PROPERTY_NAME_ATTR);
        Implementation<?> implementation = definition.getImplementation();
        ComponentType<?, ?, ?> componentType = implementation.getComponentType();
        Property<Type> property = (Property<Type>) componentType.getProperties().get(name);
        if (property == null) {
            throw new UndefinedPropertyException(name);
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
                DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
                Document value = PropertyUtils.createPropertyValue(reader, property.getXmlType(), documentBuilder);
                propertyValue = new PropertyValue<Type>(name, value);
            } catch (ParserConfigurationException e) {
                throw new LoaderException(e);
            }
        }
        ObjectFactory<Type> objectFactory = propertyFactory.createObjectFactory(property, propertyValue);
        propertyValue.setValueFactory(objectFactory);
        definition.add(propertyValue);
    }

    protected void loadReference(XMLStreamReader reader,
                                 ComponentDefinition<?> componentDefinition,
                                 DeploymentContext context) throws XMLStreamException, LoaderException {
        String name = reader.getAttributeValue(null, "name");
        if (name == null) {
            throw new InvalidReferenceException("No name specified");
        }
        String target = reader.getAttributeValue(null, "target");
        boolean autowire = Boolean.parseBoolean(reader.getAttributeValue(null, "autowire"));
        URI componentId = context.getComponentId();
        List<URI> uris = new ArrayList<URI>();
        if (target != null) {
            StringTokenizer tokenizer = new StringTokenizer(target);
            while (tokenizer.hasMoreTokens()) {
                String token = tokenizer.nextToken();
                QualifiedName qName = new QualifiedName(token);
                uris.add(componentId.resolve(qName.getFragment()));
            }
        }

        Implementation<?> impl = componentDefinition.getImplementation();
        ComponentType<?, ?, ?> componentType = impl.getComponentType();
        if (!componentType.getReferences().containsKey(name)) {
            throw new UndefinedReferenceException(name);
        }
        if (componentType instanceof CompositeComponentType) {
            if (uris.size() != 1) {
                // FIXME not yet implemented
                throw new UnsupportedOperationException();
            }
            ReferenceDefinition definition = componentType.getReferences().get(name);
            if (definition.getBindings().isEmpty()) {
                // TODO JFM allow selection of a default binding
                LocalBindingDefinition binding = new LocalBindingDefinition(uris.get(0));
                definition.addBinding(binding);
            } else {
                for (BindingDefinition binding : definition.getBindings()) {
                    binding.setTargetUri(uris.get(0));
                }
            }
        } else {
            ReferenceTarget referenceTarget = componentDefinition.getReferenceTargets().get(name);
            if (referenceTarget == null) {
                referenceTarget = new ReferenceTarget();
                referenceTarget.setReferenceName(componentId.resolve('#' + name));
                referenceTarget.setAutowire(autowire);
                componentDefinition.add(referenceTarget);
            }
            for (URI uri : uris) {
                referenceTarget.addTarget(uri);
            }
        }
    }

    @SuppressWarnings("unchecked")
    protected void populatePropertyValues(ComponentDefinition<Implementation<?>> componentDefinition)
        throws MissingRequiredPropertyException {
        ComponentType componentType = componentDefinition.getImplementation().getComponentType();
        if (componentType != null) {
            Map<String, Property<?>> properties = componentType.getProperties();
            Map<String, PropertyValue<?>> propertyValues = componentDefinition.getPropertyValues();

            for (Property<?> aProperty : properties.values()) {
                if (propertyValues.get(aProperty.getName()) == null) {
                    if (aProperty.isRequired()) {
                        throw new MissingRequiredPropertyException(aProperty.getName());
                    } else if (aProperty.getDefaultValue() != null) {
                        PropertyValue propertyValue = new PropertyValue();
                        propertyValue.setName(aProperty.getName());
                        propertyValue.setValue(aProperty.getDefaultValue());
//                        propertyValue.setValueFactory(new SimplePropertyObjectFactory(aProperty,
//                            propertyValue.getValue()));
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
            if (!referenceDef.isRequired()) {
                continue;
            }
            String name = referenceDef.getUri().getFragment();
            ReferenceTarget target = definition.getReferenceTargets().get(name);
            if (target == null) {
                throw new MissingReferenceException(name);
            }
            if (target.isAutowire()) {
                // autowire targets are not set yet
                continue;
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
