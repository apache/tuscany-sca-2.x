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

package org.apache.tuscany.scdl.stax.impl;

import static javax.xml.stream.XMLStreamConstants.END_ELEMENT;
import static javax.xml.stream.XMLStreamConstants.START_ELEMENT;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.apache.tuscany.assembly.model.AssemblyFactory;
import org.apache.tuscany.assembly.model.Callback;
import org.apache.tuscany.assembly.model.Component;
import org.apache.tuscany.assembly.model.ComponentProperty;
import org.apache.tuscany.assembly.model.ComponentReference;
import org.apache.tuscany.assembly.model.ComponentService;
import org.apache.tuscany.assembly.model.Composite;
import org.apache.tuscany.assembly.model.CompositeReference;
import org.apache.tuscany.assembly.model.CompositeService;
import org.apache.tuscany.assembly.model.Contract;
import org.apache.tuscany.assembly.model.Property;
import org.apache.tuscany.assembly.model.Wire;
import org.apache.tuscany.policy.model.PolicyFactory;
import org.apache.tuscany.scdl.stax.Constants;
import org.apache.tuscany.scdl.stax.LoaderRegistry;

/**
 * A composite content handler.
 * 
 * @version $Rev$ $Date$
 */
public class CompositeLoader extends BaseLoader implements Constants {

    public CompositeLoader(AssemblyFactory factory, PolicyFactory policyFactory, LoaderRegistry registry) {
        super(factory, policyFactory, registry);
    }

    public Composite load(XMLStreamReader reader) throws XMLStreamException {
        Composite composite = null;
        Composite include = null;
        Component component = null;
        Property property = null;
        ComponentService componentService = null;
        ComponentReference componentReference = null;
        ComponentProperty componentProperty = null;
        CompositeService compositeService = null;
        CompositeReference compositeReference = null;
        Contract contract = null;
        Wire wire = null;
        Callback callback = null;
        QName name = null;
        while (reader.hasNext()) {
            int event = reader.getEventType();
            switch (event) {
                case START_ELEMENT:
                    name = reader.getName();
                    if (COMPOSITE_QNAME.equals(name)) {
                        composite = factory.createComposite();
                        composite.setName(getQName(reader, NAME));
                        composite.setAutowire(getBoolean(reader, AUTOWIRE));
                        composite.setLocal(getBoolean(reader, LOCAL));
                        composite.setConstrainingType(getConstrainingType(reader));
                        readRequiredIntents(composite, reader);
                        readPolicySets(composite, reader);
                    } else if (INCLUDE_QNAME.equals(name)) {
                        include = factory.createComposite();
                        include.setUnresolved(true);
                        composite.getIncludes().add(include);
                    } else if (SERVICE_QNAME.equals(name)) {
                        if (component != null) {
                            componentService = factory.createComponentService();
                            contract = componentService;
                            componentService.setName(getString(reader, NAME));
                            readRequiredIntents(componentService, reader);
                            readPolicySets(componentService, reader);
                            component.getServices().add(componentService);
                            readRequiredIntents(contract, reader);
                            readPolicySets(contract, reader);
                            if (nextChildElement(reader)) {
                                registry.load(contract, reader);
                            }
                        } else {
                            compositeService = factory.createCompositeService();
                            contract = compositeService;
                            compositeService.setName(getString(reader, NAME));

                            ComponentService promoted = factory.createComponentService();
                            promoted.setUnresolved(true);
                            promoted.setName(getString(reader, PROMOTE));
                            compositeService.setPromotedService(promoted);

                            composite.getServices().add(compositeService);
                            readRequiredIntents(contract, reader);
                            readPolicySets(contract, reader);
                        }
                    } else if (REFERENCE_QNAME.equals(name)) {
                        if (component != null) {
                            componentReference = factory.createComponentReference();
                            contract = componentReference;
                            componentReference.setName(getString(reader, NAME));

                            // TODO support multivalued attribute
                            ComponentService target = factory.createComponentService();
                            target.setUnresolved(true);
                            target.setName(getString(reader, TARGET));
                            componentReference.getTargets().add(target);

                            component.getReferences().add(componentReference);
                            readRequiredIntents(contract, reader);
                            readPolicySets(contract, reader);
                            if (nextChildElement(reader)) {
                                registry.load(contract, reader);
                            }
                        } else {
                            compositeReference = factory.createCompositeReference();
                            contract = compositeReference;
                            compositeReference.setName(getString(reader, NAME));

                            // TODO support multivalued attribute
                            ComponentReference promoted = factory.createComponentReference();
                            promoted.setUnresolved(true);
                            promoted.setName(getString(reader, PROMOTE));
                            compositeReference.getPromotedReferences().add(promoted);

                            composite.getReferences().add(compositeReference);
                            readRequiredIntents(contract, reader);
                            readPolicySets(contract, reader);
                        }
                    } else if (PROPERTY_QNAME.equals(name)) {
                        if (component != null) {
                            componentProperty = factory.createComponentProperty();
                            property = componentProperty;
                            readProperty(componentProperty, reader);
                            component.getProperties().add(componentProperty);
                        } else {
                            property = factory.createProperty();
                            readProperty(property, reader);
                            composite.getProperties().add(property);
                        }
                        readRequiredIntents(property, reader);
                        readPolicySets(property, reader);
                    } else if (COMPONENT_QNAME.equals(name)) {
                        component = factory.createComponent();
                        component.setName(getString(reader, NAME));
                        component.setConstrainingType(getConstrainingType(reader));
                        composite.getComponents().add(component);
                        readRequiredIntents(component, reader);
                        readPolicySets(component, reader);
                    } else if (WIRE_QNAME.equals(name)) {
                        wire = factory.createWire();
                        ComponentReference source = factory.createComponentReference();
                        source.setUnresolved(true);
                        source.setName(getString(reader, SOURCE));
                        wire.setSource(source);

                        ComponentService target = factory.createComponentService();
                        target.setUnresolved(true);
                        target.setName(getString(reader, TARGET));
                        wire.setTarget(target);

                        composite.getWires().add(wire);
                        readRequiredIntents(wire, reader);
                        readPolicySets(wire, reader);
                    } else if (CALLBACK_QNAME.equals(name)) {
                        callback = factory.createCallback();
                        contract.setCallback(callback);
                        readRequiredIntents(callback, reader);
                        readPolicySets(callback, reader);

                    }
                    break;
                case XMLStreamConstants.CHARACTERS:
                    if (include != null && INCLUDE_QNAME.equals(name)) {
                        include.setName(getQNameValue(reader, reader.getText().trim()));
                        // include = null;
                    }
                    if (property != null && PROPERTY_QNAME.equals(name)) {
                        property.setDefaultValue(reader.getText().trim());
                        // property = null;
                    }

                    break;
                case END_ELEMENT:
                    name = reader.getName();
                    if (SERVICE_QNAME.equals(name)) {
                        componentService = null;
                        compositeService = null;
                        contract = null;
                    } else if (INCLUDE_QNAME.equals(name)) {
                        include = null;
                    } else if (REFERENCE_QNAME.equals(name)) {
                        componentReference = null;
                        compositeReference = null;
                        contract = null;
                    } else if (PROPERTY_QNAME.equals(name)) {
                        componentProperty = null;
                        property = null;
                    } else if (COMPONENT_QNAME.equals(name)) {
                        component = null;
                    } else if (WIRE_QNAME.equals(name)) {
                        wire = null;
                    } else if (CALLBACK_QNAME.equals(name)) {
                        callback = null;
                    }
                    break;
            }
            if (reader.hasNext()) {
                reader.next();
            }
        }
        return composite;
    }

}
