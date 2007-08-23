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
package org.apache.tuscany.implementation.xquery;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import org.apache.tuscany.sca.assembly.ComponentProperty;
import org.apache.tuscany.sca.assembly.ComponentReference;
import org.apache.tuscany.sca.assembly.ComponentService;
import org.apache.tuscany.sca.assembly.Reference;
import org.apache.tuscany.sca.core.factory.ObjectFactory;
import org.apache.tuscany.sca.databinding.saxon.SaxonNodeDataBinding;
import org.apache.tuscany.sca.databinding.saxon.SaxonValueDataBinding;
import org.apache.tuscany.sca.implementation.java.injection.JavaPropertyValueObjectFactory;
import org.apache.tuscany.sca.interfacedef.DataType;
import org.apache.tuscany.sca.interfacedef.Interface;
import org.apache.tuscany.sca.interfacedef.InterfaceContract;
import org.apache.tuscany.sca.interfacedef.Operation;
import org.apache.tuscany.sca.interfacedef.java.JavaInterface;
import org.apache.tuscany.sca.interfacedef.util.JavaXMLMapper;
import org.apache.tuscany.sca.invocation.Invoker;
import org.apache.tuscany.sca.provider.ImplementationProvider;
import org.apache.tuscany.sca.runtime.RuntimeComponent;
import org.apache.tuscany.sca.runtime.RuntimeComponentService;

/**
 * Provides the runtime configuration of xquery-typed component.
 * @version $Rev$ $Date$
 * 
 * Generally the following is done:
 * 1. During constructuion all services and references are provided
 *    with the saxon data binding, which is the one required by the 
 *    XQueryInvoker
 * 
 * 2. During startup:
 *    - a proxy is created for each reference and it is kept in the referenceProxies
 *      map, which is used later by the invoker to configure the saxon parser;
 *    - each property value is read and put into the properties map. This is
 *      again used by the invoker to configure the saxon parser
 */
public class XQueryImplementationProvider implements ImplementationProvider {
	
	private RuntimeComponent component; 
	private XQueryImplementation implementation;
	private Map<String, Object> referenceProxies = new HashMap<String, Object>();
	private Map<String, Object> properties = new HashMap<String, Object>();
	private JavaPropertyValueObjectFactory javaFactory;
	
	public XQueryImplementationProvider(RuntimeComponent component, XQueryImplementation implementation,
			JavaPropertyValueObjectFactory factory) {
		this.component = component;
		this.implementation = implementation;
		this.javaFactory = factory;
		init();
	}
	
	private void init() {
		List<ComponentService> services = component.getServices();
		for(ComponentService sevice : services) {
			InterfaceContract interfaceContract = sevice.getInterfaceContract();
			//interfaceContract.getInterface().setDefaultDataBinding(ValueRepresentation.class.getName());
			setDataBinding(interfaceContract.getInterface(), false);
		}
		
		List<ComponentReference> references = component.getReferences();
		for(ComponentReference reference : references) {
			InterfaceContract interfaceContract = reference.getInterfaceContract();
			//interfaceContract.getInterface().setDefaultDataBinding(ValueRepresentation.class.getName());
			setDataBinding(interfaceContract.getInterface(), true);
		}
	}

	public Invoker createCallbackInvoker(Operation operation) {
		return new XQueryInvoker(null, operation, implementation, referenceProxies, properties);
	}

	public Invoker createInvoker(RuntimeComponentService service,
			Operation operation) {
		return new XQueryInvoker(service, operation, implementation, referenceProxies, properties);
	}

	public void start() {
		
		for(Reference reference : component.getReferences()) {
			String refName = reference.getName();
			if(refName.startsWith("$self$.")) {
				continue;
			}
			Class interfaze = ((JavaInterface)reference.getInterfaceContract().getInterface()).getJavaClass();
			Object refProxy = component.getComponentContext().getService(interfaze, refName);
			referenceProxies.put(refName, refProxy);
		}
		
		for(ComponentProperty property : component.getProperties()) {
			String propName = property.getName();
			QName xmlType = property.getXSDType();
			Class clazz = JavaXMLMapper.getJavaType(xmlType);
			
			Object propertyValue = null;
			if(clazz == null || java.lang.Object.class.equals(clazz)) {
				propertyValue = property.getValue();
			} else {
				ObjectFactory objfactory = javaFactory.createValueFactory(property, property.getValue(), clazz);
				propertyValue = objfactory.getInstance();
			}
			properties.put(propName, propertyValue);
		}
	}
  
  private void setDataBinding(Interface interfaze, boolean isReference) {
	interfaze.setDefaultDataBinding(SaxonNodeDataBinding.NAME);
	List<Operation> operations = interfaze.getOperations();
	for(Operation operation : operations) {
		DataType<List<DataType>> inputType = operation.getInputType();
		if(inputType != null) {
			List<DataType> logical = inputType.getLogical();
			for(DataType inArg : logical) {
				if(inArg.getPhysical().isPrimitive() || inArg.getPhysical() == java.lang.String.class) {
					if(!isReference) {
						inArg.setDataBinding(SaxonValueDataBinding.NAME);
					}
				} else {
					inArg.setDataBinding(SaxonNodeDataBinding.NAME);
				}
			}
		}
		DataType outputType = operation.getOutputType();
		if(outputType != null) {
			if(outputType.getPhysical().isPrimitive() || outputType.getPhysical() == java.lang.String.class) {
				if(!isReference) {
					outputType.setDataBinding(SaxonValueDataBinding.NAME);
				}
			} else {
				outputType.setDataBinding(SaxonNodeDataBinding.NAME);
			}
		}
	}
  }

	public void stop() {
		// TODO Auto-generated method stub

	}

}
