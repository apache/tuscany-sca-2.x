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

package org.apache.tuscany.scdl.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.StringTokenizer;

import javax.xml.namespace.QName;

import org.apache.tuscany.assembly.model.AbstractProperty;
import org.apache.tuscany.assembly.model.AssemblyFactory;
import org.apache.tuscany.assembly.model.ConstrainingType;
import org.apache.tuscany.assembly.model.Property;
import org.apache.tuscany.policy.model.Intent;
import org.apache.tuscany.policy.model.IntentAttachPoint;
import org.apache.tuscany.policy.model.PolicyFactory;
import org.apache.tuscany.policy.model.PolicySet;
import org.apache.tuscany.policy.model.PolicySetAttachPoint;
import org.apache.tuscany.sca.idl.Operation;
import org.apache.tuscany.scdl.BindingHandler;
import org.apache.tuscany.scdl.Constants;
import org.apache.tuscany.scdl.HandlerRegistry;
import org.apache.tuscany.scdl.ImplementationHandler;
import org.apache.tuscany.scdl.InterfaceHandler;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * A test handler to test the usability of the assembly model API when loading SCDL
 * 
 * @version $Rev$ $Date$
 */
public abstract class BaseHandler extends DefaultHandler implements ContentHandler {

    protected final static String sca10 = "http://www.osoa.org/xmlns/sca/1.0";

    private AssemblyFactory factory;
    private PolicyFactory policyFactory;
    private HandlerRegistry<InterfaceHandler> interfaceHandlers;
    private HandlerRegistry<ImplementationHandler> implementationHandlers;
    private HandlerRegistry<BindingHandler> bindingHandlers;
    private NamespaceStack nsStack = new NamespaceStack();
    private InterfaceHandler interfaceHandler;
    private ImplementationHandler implementationHandler;
    private BindingHandler bindingHandler;
    private int elementCount;

    public BaseHandler(AssemblyFactory factory, PolicyFactory policyFactory,
    		HandlerRegistry<InterfaceHandler> interfaceHandlers,
    		HandlerRegistry<ImplementationHandler> implementationHandlers,
    		HandlerRegistry<BindingHandler> bindingHandlers) {
    	
    	this.factory = factory;
    	this.policyFactory = policyFactory;
    	this.interfaceHandlers = interfaceHandlers;
    	this.implementationHandlers = implementationHandlers;
    	this.bindingHandlers = bindingHandlers;
    }

    protected String getString(Attributes attr, String name) {
        return attr.getValue(name);
    }
    
    protected QName getQName(String qname) {
        if (qname != null) {
	        int index = qname.indexOf(':');
	        String prefix = index == -1 ? "" : qname.substring(0, index);
	        String localName = index == -1 ? qname : qname.substring(index+1);
	        String ns = nsStack.getNamespaceURI(prefix);
	        if (ns == null) {
	            ns = "";
	        }
	        return new QName(ns, localName, prefix);
        } else {
        	return null;
        }
    }

    protected QName getQName(Attributes attr, String name) {
        return getQName(attr.getValue(name));
    }

    protected boolean getBoolean(Attributes attr, String name) {
        return Boolean.valueOf(attr.getValue(name));
    }
    
    protected List<QName> getQNames(Attributes attr, String name) {
    	String value = attr.getValue(name);
    	if (value != null) {
    		List<QName> qnames = new ArrayList<QName>();
    		for (StringTokenizer tokens = new StringTokenizer(value); tokens.hasMoreTokens(); ) {
    			qnames.add(getQName(tokens.nextToken()));
    		}
    		return qnames;
    	} else {
    		return Collections.emptyList();
    	}
    }
    
    protected void readIntents(IntentAttachPoint attachPoint, Attributes attr) {
    	readIntents(attachPoint, null, attr);
    }
    
    protected void readIntents(IntentAttachPoint attachPoint, Operation operation, Attributes attr) {
    	String value = attr.getValue(Constants.REQUIRES);
    	if (value != null) {
			List<Intent> requiredIntents = attachPoint.getRequiredIntents();
    		for (StringTokenizer tokens = new StringTokenizer(value); tokens.hasMoreTokens(); ) {
    			QName qname = getQName(tokens.nextToken());
    			Intent intent = policyFactory.createIntent();
    			intent.setName(qname);
    			if (operation != null) {
    				intent.getOperations().add(operation);
    			}
				requiredIntents.add(intent);
    		}
    	}
    }
    
    protected void readPolicies(PolicySetAttachPoint attachPoint, Attributes attr) {
    	readPolicies(attachPoint, null, attr);
    }

	protected void readPolicies(PolicySetAttachPoint attachPoint, Operation operation, Attributes attr) {
		readIntents(attachPoint, operation, attr);
		
    	String value = attr.getValue(Constants.POLICY_SETS);
    	if (value != null) {
			List<PolicySet> policySets = attachPoint.getPolicySets();
    		for (StringTokenizer tokens = new StringTokenizer(value); tokens.hasMoreTokens(); ) {
    			QName qname = getQName(tokens.nextToken());
    			PolicySet policySet = policyFactory.createPolicySet();
    			policySet.setName(qname);
    			if (operation != null) {
    				policySet.getOperations().add(operation);
    			}
				policySets.add(policySet);
    		}
    	}
    }

    protected ConstrainingType getConstrainingType(Attributes attr) {
        QName constrainingTypeName = getQName(attr, "constrainingType");
        if (constrainingTypeName != null) {
            ConstrainingType constrainingType = factory.createConstrainingType();
            constrainingType.setName(constrainingTypeName);
            constrainingType.setUnresolved(true);
            return constrainingType;
        } else {
            return null;
        }
    }

    protected void readAbstractProperty(AbstractProperty prop, Attributes attr) {
        prop.setName(getString(attr, "name"));
        prop.setMany(getBoolean(attr, "many"));
        prop.setMustSupply(getBoolean(attr, "mustSupply"));
        prop.setXSDElement(getQName(attr, "element"));
        prop.setXSDType(getQName(attr, "type"));
        // TODO handle default value
    }

    protected void readProperty(Property prop, Attributes attr) {
        readAbstractProperty(prop, attr);
    }
    
    protected InterfaceHandler startInterfaceElement(String uri, String name, String qname, Attributes attr) throws SAXException {
    	if (interfaceHandler == null && interfaceHandlers != null) {
    		interfaceHandler = interfaceHandlers.getHandler(uri, name);
    	}
    	if (interfaceHandler != null) {
    		interfaceHandler.startElement(uri, name, qname, attr);
    		elementCount++;
    	}
    	return interfaceHandler;
    }
    
    protected boolean endInterfaceElement(String uri, String name, String qname) throws SAXException {
    	if (interfaceHandler != null) {
    		interfaceHandler.endElement(uri, name, qname);
    		elementCount--;
    		if (elementCount == 0) {
    			interfaceHandler = null;
    			return true;
    		}
    	}
    	return false;
    }

    protected ImplementationHandler startImplementationElement(String uri, String name, String qname, Attributes attr) throws SAXException {
    	if (implementationHandler == null && implementationHandlers != null) {
    		implementationHandler = implementationHandlers.getHandler(uri, name);
    	}
    	if (implementationHandler != null) {
    		implementationHandler.startElement(uri, name, qname, attr);
    		elementCount++;
    	}
    	return implementationHandler;
    }
    
    protected boolean endImplementationElement(String uri, String name, String qname) throws SAXException {
    	if (implementationHandler != null) {
    		implementationHandler.endElement(uri, name, qname);
    		elementCount--;
    		if (elementCount == 0) {
    			implementationHandler = null;
    			return true;
    		}
    	}
    	return false;
    }

    protected BindingHandler startBindingElement(String uri, String name, String qname, Attributes attr) throws SAXException {
    	if (bindingHandler == null && bindingHandlers != null) {
    		bindingHandler = bindingHandlers.getHandler(uri, name);
    	}
    	if (bindingHandler != null) {
    		bindingHandler.startElement(uri, name, qname, attr);
    		elementCount++;
    	}
    	return bindingHandler;
    }
    
    protected boolean endBindingElement(String uri, String name, String qname) throws SAXException {
    	if (bindingHandler != null) {
    		bindingHandler.endElement(uri, name, qname);
    		elementCount--;
    		if (elementCount == 0) {
    			bindingHandler = null;
    			return true;
    		}
    	}
    	return false;
    }

    public void endPrefixMapping(String prefix) throws SAXException {
        nsStack.endPrefixMapping(prefix);
    }

    public void startPrefixMapping(String prefix, String uri) throws SAXException {
        nsStack.startPrefixMapping(prefix, uri);
    }

}
