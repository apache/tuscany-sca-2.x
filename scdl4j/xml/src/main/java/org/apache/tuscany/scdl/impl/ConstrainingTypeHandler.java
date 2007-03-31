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

import org.apache.tuscany.assembly.model.AbstractContract;
import org.apache.tuscany.assembly.model.AbstractProperty;
import org.apache.tuscany.assembly.model.AbstractReference;
import org.apache.tuscany.assembly.model.AbstractService;
import org.apache.tuscany.assembly.model.AssemblyFactory;
import org.apache.tuscany.assembly.model.ConstrainingType;
import org.apache.tuscany.policy.model.PolicyFactory;
import org.apache.tuscany.scdl.Constants;
import org.apache.tuscany.scdl.HandlerRegistry;
import org.apache.tuscany.scdl.InterfaceHandler;
import org.apache.tuscany.scdl.util.BaseHandler;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

/**
 * A contrainingType content handler.
 * 
 * @version $Rev$ $Date$
 */
public class ConstrainingTypeHandler extends BaseHandler implements ContentHandler {

    private ConstrainingType constrainingType;
    private AbstractService abstractService;
    private AbstractReference abstractReference;
    private AbstractProperty abstractProperty;
    private AbstractContract abstractContract;
	private AssemblyFactory factory;
	private InterfaceHandler interfaceHandler;

    public ConstrainingTypeHandler(AssemblyFactory factory, PolicyFactory policyFactory,
    		HandlerRegistry<InterfaceHandler> interfaceHandlers) {
        super(factory, policyFactory, interfaceHandlers, null, null);
        this.factory = factory;
    }
    
    public void startDocument() throws SAXException {
    	constrainingType = null;
    }

    public void startElement(String uri, String name, String qname, Attributes attr) throws SAXException {
        if (Constants.SCA10_NS.equals(uri)) {

            if (Constants.CONSTRAINING_TYPE.equals(name)) {
                constrainingType = factory.createConstrainingType();
                constrainingType.setName(getQName(attr, Constants.NAME));
                readIntents(constrainingType, attr);
                return;

            } else if (Constants.SERVICE.equals(name)) {
                abstractService = factory.createAbstractService();
                abstractContract = abstractService;
                abstractService.setName(getString(attr, Constants.NAME));
                constrainingType.getServices().add(abstractService);
                readIntents(abstractService, attr);
                return;

            } else if (Constants.REFERENCE.equals(name)) {
                abstractReference = factory.createAbstractReference();
                abstractContract = abstractReference;
                abstractReference.setName(getString(attr, Constants.NAME));
                constrainingType.getReferences().add(abstractReference);
                readIntents(abstractReference, attr);
                return;

            } else if (Constants.PROPERTY.equals(name)) {
                abstractProperty = factory.createAbstractProperty();
                readAbstractProperty(abstractProperty, attr);
                constrainingType.getProperties().add(abstractProperty);
                readIntents(abstractProperty, attr);
                return;
            }
        }
        
        // Handle interface elements
        if (abstractContract !=null) {
        	interfaceHandler = startInterfaceElement(uri, name, qname, attr);
        }
        
    }

    public void endElement(String uri, String name, String qname) throws SAXException {
    	
    	// Handle interface elements
        if (abstractContract !=null) {
			if (endInterfaceElement(uri, name, qname)) {
				abstractContract.setInterface(interfaceHandler.getInterface());
				interfaceHandler = null;
				return;
			}
        }
    	
        if (Constants.SCA10_NS.equals(uri)) {
            if (Constants.SERVICE.equals(name)) {
                abstractService = null;
                abstractContract = null;

            } else if (Constants.REFERENCE.equals(name)) {
                abstractReference = null;
                abstractContract = null;

            } else if (Constants.PROPERTY.equals(name)) {
                abstractProperty = null;
            }
        }
    }

	public ConstrainingType getConstrainingType() {
		return constrainingType;
	}

}
