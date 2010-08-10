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
package org.apache.tuscany.sca.implementation.java.introspect.impl;

import javax.jws.WebService;
import javax.xml.ws.ServiceMode;
import javax.xml.ws.WebServiceProvider;

import org.apache.tuscany.sca.assembly.AssemblyFactory;
import org.apache.tuscany.sca.assembly.Service;
import org.apache.tuscany.sca.assembly.xml.Constants;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.core.FactoryExtensionPoint;
import org.apache.tuscany.sca.implementation.java.IntrospectionException;
import org.apache.tuscany.sca.implementation.java.JavaImplementation;
import org.apache.tuscany.sca.implementation.java.introspect.BaseJavaClassVisitor;
import org.apache.tuscany.sca.interfacedef.InvalidInterfaceException;
import org.apache.tuscany.sca.interfacedef.java.JavaInterface;
import org.apache.tuscany.sca.interfacedef.java.JavaInterfaceContract;
import org.apache.tuscany.sca.interfacedef.java.JavaInterfaceFactory;
import org.apache.tuscany.sca.interfacedef.util.JavaXMLMapper;
import org.apache.tuscany.sca.interfacedef.wsdl.WSDLFactory;
import org.apache.tuscany.sca.interfacedef.wsdl.WSDLInterface;
import org.apache.tuscany.sca.interfacedef.wsdl.WSDLInterfaceContract;
import org.apache.tuscany.sca.policy.Intent;
import org.apache.tuscany.sca.policy.PolicyFactory;

/**
 * Process JAXWS annotations and updates the component type accordingly
 * 
 */
public class JAXWSProcessor extends BaseJavaClassVisitor {
    
    private PolicyFactory policyFactory;
    private WSDLFactory wsdlFactory;
    
    public JAXWSProcessor(ExtensionPointRegistry registry) {
        super(registry);
        FactoryExtensionPoint factories = registry.getExtensionPoint(FactoryExtensionPoint.class);
        this.wsdlFactory = factories.getFactory(WSDLFactory.class);
        this.assemblyFactory = factories.getFactory(AssemblyFactory.class);
        this.policyFactory = factories.getFactory(PolicyFactory.class);
        this.javaInterfaceFactory = factories.getFactory(JavaInterfaceFactory.class);
    }

    @Override
    public <T> void visitClass(Class<T> clazz, JavaImplementation type) throws IntrospectionException {
    	if ( clazz.getAnnotation(ServiceMode.class) != null ) {
    		// Add soap intent - JCA 11013    	
            Intent soapIntent = policyFactory.createIntent();
            soapIntent.setName(Constants.SOAP_INTENT);         
    		type.getRequiredIntents().add(soapIntent);
    	}
    	
    	if ( clazz.getAnnotation(WebServiceProvider.class) != null ) {
    		// If the implementation is annotated with @WebServiceProvider,
    		// make all service interfaces remotable
    		for ( Service s : type.getServices() ) {
    			s.getInterfaceContract().getInterface().setRemotable(true);
    		}
    		// JCA 11015
    	}
    	
        WebService webServiceAnnotation = clazz.getAnnotation(WebService.class);
        org.oasisopen.sca.annotation.Service serviceAnnotation = clazz.getAnnotation(org.oasisopen.sca.annotation.Service.class);
        String tns = JavaXMLMapper.getNamespace(clazz);
        String localName = clazz.getSimpleName();
        Class<?> interfaze = clazz;
        if (webServiceAnnotation != null &&
            serviceAnnotation == null) {
            tns = getValue(webServiceAnnotation.targetNamespace(), tns);
            localName = getValue(webServiceAnnotation.name(), localName);
            
            String serviceInterfaceName = webServiceAnnotation.endpointInterface();
            String wsdlLocation = webServiceAnnotation.wsdlLocation();

            Service service;
            try {
                service = createService(clazz, localName, serviceInterfaceName, wsdlLocation);
            } catch (InvalidInterfaceException e) {
                throw new IntrospectionException(e);
            }
            
            if (!type.getServices().contains(service)){
                type.getServices().add(service);   
            }
        }
    }
    
    /**
     * Utility methods
     */    

    private static String getValue(String value, String defaultValue) {
        return "".equals(value) ? defaultValue : value;
    }
    
    public Service createService(Class<?> clazz, String serviceName, String javaInterfaceName, String wsdlFileName)  throws InvalidInterfaceException, IntrospectionException {
        Service service = assemblyFactory.createService();

        if (serviceName != null) {
            service.setName(serviceName);
        } else if (javaInterfaceName != null){
            service.setName(javaInterfaceName.substring(javaInterfaceName.lastIndexOf('.')));
        } 
        
        // create the physical Java interface contract
        JavaInterfaceContract javaInterfaceContract = javaInterfaceFactory.createJavaInterfaceContract();;
        service.setInterfaceContract(javaInterfaceContract);
        
        if (javaInterfaceName != null &&
            javaInterfaceName.length() > 0){
            JavaInterface callInterface = javaInterfaceFactory.createJavaInterface();
            callInterface.setName(javaInterfaceName);
            callInterface.setRemotable(true);
            callInterface.setUnresolved(true);
            javaInterfaceContract.setInterface(callInterface);
        } else {
            // we use the bean class as the service interface
            JavaInterface callInterface = javaInterfaceFactory.createJavaInterface(clazz);
            callInterface.setRemotable(true);
            callInterface.setUnresolved(false); // this will already be false but this makes it easy to follow the logic
            javaInterfaceContract.setInterface(callInterface);
        }
        
        // create the logical WSDL interface if it's specified in 
        // the @WebService annotation
        if (wsdlFileName != null &&
                wsdlFileName.length() > 0){         
             WSDLInterface callInterface = wsdlFactory.createWSDLInterface();
             callInterface.setUnresolved(true);
             callInterface.setRemotable(true);
            
             WSDLInterfaceContract wsdlInterfaceContract = wsdlFactory.createWSDLInterfaceContract();
             wsdlInterfaceContract.setInterface(callInterface);
             wsdlInterfaceContract.setLocation(wsdlFileName);
             javaInterfaceContract.setNormailizedWSDLContract(wsdlInterfaceContract);
         }  
         return service;
    }    

}
