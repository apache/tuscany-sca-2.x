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
import org.apache.tuscany.assembly.model.ComponentService;
import org.apache.tuscany.assembly.model.ComponentType;
import org.apache.tuscany.assembly.model.Contract;
import org.apache.tuscany.assembly.model.Property;
import org.apache.tuscany.assembly.model.Reference;
import org.apache.tuscany.assembly.model.Service;
import org.apache.tuscany.policy.model.PolicyFactory;
import org.apache.tuscany.scdl.Constants;
import org.apache.tuscany.scdl.HandlerRegistry;
import org.apache.tuscany.scdl.InterfaceHandler;
import org.apache.tuscany.scdl.util.BaseHandler;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

/**
 A componentType content handler.
 * 
 * @version $Rev$ $Date$
 */
public class ComponentTypeHandler extends BaseHandler implements ContentHandler {

    private ComponentType componentType;
    private Service service;
    private Reference reference;
    private Contract contract;
    private Property property;
    private Callback callback;
    private InterfaceHandler interfaceHandler;
	private AssemblyFactory factory;

    public ComponentTypeHandler(AssemblyFactory factory, PolicyFactory policyFactory,
    		HandlerRegistry<InterfaceHandler> interfaceHandlers) {
        super(factory, policyFactory, interfaceHandlers, null, null);
        this.factory = factory;
    }
    
    public void startDocument() throws SAXException {
    	componentType = null;
    }

    public void startElement(String uri, String name, String qname, Attributes attr) throws SAXException {
        if (Constants.SCA10_NS.equals(uri)) {

            if (Constants.COMPONENT_TYPE.equals(name)) {
                componentType = factory.createComponentType();
                componentType.setConstrainingType(getConstrainingType(attr));
                readRequiredIntents(componentType, attr);
                readPolicySets(componentType, attr);
                return;

            } else if (Constants.SERVICE.equals(name)) {
                service = factory.createService();
                contract = service;
                service.setName(getString(attr, Constants.NAME));
                componentType.getServices().add(service);
                readRequiredIntents(service, attr);
                readPolicySets(service, attr);
                return;

            } else if (Constants.REFERENCE.equals(name)) {
                reference = factory.createReference();
                contract = reference;
                reference.setName(getString(attr, Constants.NAME));

                //TODO support multivalued attribute
            	ComponentService target = factory.createComponentService();
            	target.setUnresolved(true);
            	target.setName(getString(attr, Constants.TARGET));
            	reference.getTargets().add(target);

            	componentType.getReferences().add(reference);
                readRequiredIntents(reference, attr);
                readPolicySets(reference, attr);
            	return;
            	
            } else if (Constants.PROPERTY.equals(name)) {
                property = factory.createProperty();
                readProperty(property, attr);
                componentType.getProperties().add(property);
                readRequiredIntents(property, attr);
                readPolicySets(property, attr);
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
        }
    }

    public void endElement(String uri, String name, String qname) throws SAXException {
    	
    	// Handle interface elements
        if (contract != null) {
			if (endInterfaceElement(uri, name, qname)) {
				contract.setInterface(interfaceHandler.getInterface());
				interfaceHandler = null;
				return;
			}
        }
    	
        if (Constants.SCA10_NS.equals(uri)) {
            if (Constants.SERVICE.equals(name)) {
                service = null;
                contract = null;
            } else if (Constants.REFERENCE.equals(name)) {
                reference = null;
                contract = null;
            } else if (Constants.PROPERTY.equals(name)) {
                property = null;
            } else if (Constants.CALLBACK.equals(name))
            	callback = null;
        }
    }

	public ComponentType getComponentType() {
		return componentType;
	}

}
