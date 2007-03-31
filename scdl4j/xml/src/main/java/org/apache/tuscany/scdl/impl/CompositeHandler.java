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

package org.apache.tuscany.scdl.impl;

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
import org.apache.tuscany.scdl.BindingHandler;
import org.apache.tuscany.scdl.Constants;
import org.apache.tuscany.scdl.HandlerRegistry;
import org.apache.tuscany.scdl.ImplementationHandler;
import org.apache.tuscany.scdl.InterfaceHandler;
import org.apache.tuscany.scdl.util.BaseHandler;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

/**
 * A composite content handler.
 * 
 * @version $Rev$ $Date$
 */
public class CompositeHandler extends BaseHandler implements ContentHandler {

    private Composite composite;
    private Composite include;
    private Component component;
    private Property property;
    private ComponentService componentService;
    private ComponentReference componentReference;
    private ComponentProperty componentProperty;
    private CompositeService compositeService;
    private CompositeReference compositeReference;
    private Contract contract;
    private Wire wire;
    private Callback callback;
	private AssemblyFactory factory;
	private InterfaceHandler interfaceHandler;
	private ImplementationHandler implementationHandler;
	private BindingHandler bindingHandler;

    public CompositeHandler(AssemblyFactory factory, PolicyFactory policyFactory,
    		HandlerRegistry<InterfaceHandler> interfaceHandlers,
    		HandlerRegistry<ImplementationHandler> implementationHandlers,
    		HandlerRegistry<BindingHandler> bindingHandlers) {
        super(factory, policyFactory, interfaceHandlers, implementationHandlers, bindingHandlers);
        this.factory = factory;
    }
    
    public void startDocument() throws SAXException {
    	composite = null;
    }

    public void startElement(String uri, String name, String qname, Attributes attr) throws SAXException {
        if (Constants.SCA10_NS.equals(uri)) {

            if (Constants.COMPOSITE.equals(name)) {
                composite = factory.createComposite();
                composite.setName(getQName(attr, Constants.NAME));
                composite.setAutowire(getBoolean(attr, Constants.AUTOWIRE));
                composite.setLocal(getBoolean(attr, Constants.LOCAL));
                composite.setConstrainingType(getConstrainingType(attr));
                readRequiredIntents(composite, attr);
                readPolicySets(composite, attr);
                return;

            } else if (Constants.INCLUDE.equals(name)) {
            	include = factory.createComposite();
            	include.setUnresolved(true);
            	composite.getIncludes().add(include);
            	return;
            	
            } else if (Constants.SERVICE.equals(name)) {
                if (component != null) {
                    componentService = factory.createComponentService();
                    contract = componentService;
                    componentService.setName(getString(attr, Constants.NAME));
                    readRequiredIntents(componentService, attr);
                    readPolicySets(componentService, attr);
                    component.getServices().add(componentService);
                } else {
                    compositeService = factory.createCompositeService();
                    contract = compositeService;
                    compositeService.setName(getString(attr, Constants.NAME));

                    ComponentService promoted = factory.createComponentService();
                	promoted.setUnresolved(true);
                	promoted.setName(getString(attr, Constants.PROMOTE));
                	compositeService.setPromotedService(promoted);

                	composite.getServices().add(compositeService);
                }
                readRequiredIntents(contract, attr);
                readPolicySets(contract, attr);
                return;

            } else if (Constants.REFERENCE.equals(name)) {
                if (component != null) {
                    componentReference = factory.createComponentReference();
                    contract = componentReference;
                    componentReference.setName(getString(attr, Constants.NAME));

                    //TODO support multivalued attribute
                	ComponentService target = factory.createComponentService();
                	target.setUnresolved(true);
                	target.setName(getString(attr, Constants.TARGET));
                	componentReference.getTargets().add(target);
                    
                    component.getReferences().add(componentReference);
                } else {
                    compositeReference = factory.createCompositeReference();
                    contract = compositeReference;
                    compositeReference.setName(getString(attr, Constants.NAME));

                    //TODO support multivalued attribute
                    ComponentReference promoted = factory.createComponentReference();
                	promoted.setUnresolved(true);
                	promoted.setName(getString(attr, Constants.PROMOTE));
                	compositeReference.getPromotedReferences().add(promoted);

                	composite.getReferences().add(compositeReference);
                }
                readRequiredIntents(contract, attr);
                readPolicySets(contract, attr);
                return;

            } else if (Constants.PROPERTY.equals(name)) {
                if (component != null) {
                    componentProperty = factory.createComponentProperty();
                    property = componentProperty;
                    readProperty(componentProperty, attr);
                    component.getProperties().add(componentProperty);
                } else {
                    property = factory.createProperty();
                    readProperty(property, attr);
                    composite.getProperties().add(property);
                }
                readRequiredIntents(property, attr);
                readPolicySets(property, attr);
                return;

            } else if (Constants.COMPONENT.equals(name)) {
                component = factory.createComponent();
                component.setName(getString(attr, Constants.NAME));
                component.setConstrainingType(getConstrainingType(attr));
                composite.getComponents().add(component);
                readRequiredIntents(component, attr);
                readPolicySets(component, attr);
                return;
                
            } else if (Constants.WIRE.equals(name)) {
            	wire = factory.createWire();
            	ComponentReference source = factory.createComponentReference();
            	source.setUnresolved(true);
            	source.setName(getString(attr, Constants.SOURCE));
            	wire.setSource(source);
            	
            	ComponentService target = factory.createComponentService();
            	target.setUnresolved(true);
            	target.setName(getString(attr, Constants.TARGET));
            	wire.setTarget(target);
            	
                composite.getWires().add(wire);
                readRequiredIntents(wire, attr);
                readPolicySets(wire, attr);
                return;
            	
	        } else if (Constants.CALLBACK.equals(name)) {
                callback = factory.createCallback();
                contract.setCallback(callback);
                readRequiredIntents(callback, attr);
                readPolicySets(callback, attr);
                return;
	        }
        }

        // Handle interface elements
        if (contract != null) {
        	interfaceHandler = startInterfaceElement(uri, name, qname, attr);
        	if (interfaceHandler == null) {
        		bindingHandler = startBindingElement(uri, name, qname, attr);
        	}
        	
        } else if (component != null) {
        	
        	// Handle implementation elements
        	implementationHandler = startImplementationElement(uri, name, qname, attr);
        }
    }
    
    public void characters(char[] ch, int start, int length) throws SAXException {
    	
    	// Handle property value
    	if (property != null) {
    		property.setDefaultValue(new String(ch, start, length));
    	} else if (include != null) {
    		include.setName(getQName(new String(ch, start, length)));
    	}
    }

    public void endElement(String uri, String name, String qname) throws SAXException {
    	
    	// Handle interface elements
        if (contract != null) {
			if (endInterfaceElement(uri, name, qname)) {
				contract.setInterface(interfaceHandler.getInterface());
				interfaceHandler = null;
				return;
				
			} else if (endBindingElement(uri, name, qname)) {
				contract.getBindings().add(bindingHandler.getBinding());
				bindingHandler = null;
				return;
			}
        } else if (component != null) {
        	
        	// Handle implementation elements
        	if (endImplementationElement(uri, name, qname)) {
        		component.setImplementation(implementationHandler.getImplementation());
        		implementationHandler = null;
        		return;
        	}
        }
		
        if (Constants.SERVICE.equals(name)) {
            componentService = null;
            compositeService = null;
            contract = null;
        } else if (Constants.INCLUDE.equals(name)) {
        	include = null;
        } else if (Constants.REFERENCE.equals(name)) {
            componentReference = null;
            compositeReference = null;
            contract = null;
        } else if (Constants.PROPERTY.equals(name)) {
            componentProperty = null;
            property = null;
        } else if (Constants.COMPONENT.equals(name)) {
            component = null;
        } else if (Constants.WIRE.equals(name)) {
            wire= null;
        } else if (Constants.CALLBACK.equals(name)) {
        	callback = null;
        }
    }

	public Composite getComposite() {
		return composite;
	}

}
