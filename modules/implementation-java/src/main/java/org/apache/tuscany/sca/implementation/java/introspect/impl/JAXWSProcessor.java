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

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Iterator;

import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.xml.ws.ServiceMode;
import javax.xml.ws.WebServiceProvider;

import org.apache.tuscany.sca.assembly.AssemblyFactory;
import org.apache.tuscany.sca.assembly.Service;
import org.apache.tuscany.sca.assembly.xml.Constants;
import org.apache.tuscany.sca.binding.ws.WebServiceBinding;
import org.apache.tuscany.sca.binding.ws.WebServiceBindingFactory;
import org.apache.tuscany.sca.binding.ws.xml.WebServiceConstants;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.core.FactoryExtensionPoint;
import org.apache.tuscany.sca.implementation.java.IntrospectionException;
import org.apache.tuscany.sca.implementation.java.JavaImplementation;
import org.apache.tuscany.sca.implementation.java.introspect.BaseJavaClassVisitor;
import org.apache.tuscany.sca.interfacedef.InvalidInterfaceException;
import org.apache.tuscany.sca.interfacedef.Operation;
import org.apache.tuscany.sca.interfacedef.java.JavaInterface;
import org.apache.tuscany.sca.interfacedef.java.JavaInterfaceContract;
import org.apache.tuscany.sca.interfacedef.java.JavaInterfaceFactory;
import org.apache.tuscany.sca.interfacedef.java.JavaOperation;
import org.apache.tuscany.sca.interfacedef.util.JavaXMLMapper;
import org.apache.tuscany.sca.interfacedef.wsdl.WSDLFactory;
import org.apache.tuscany.sca.interfacedef.wsdl.WSDLInterface;
import org.apache.tuscany.sca.interfacedef.wsdl.WSDLInterfaceContract;
import org.apache.tuscany.sca.policy.ExtensionType;
import org.apache.tuscany.sca.policy.Intent;
import org.apache.tuscany.sca.policy.PolicyFactory;
import org.apache.tuscany.sca.policy.PolicySubject;

/**
 * Process JAXWS annotations and updates the component type accordingly
 * 
 */
public class JAXWSProcessor extends BaseJavaClassVisitor {
    
    private PolicyFactory policyFactory;
    private WSDLFactory wsdlFactory;
    private WebServiceBindingFactory wsBindingFactory;
    
    public JAXWSProcessor(ExtensionPointRegistry registry) {
        super(registry);
        FactoryExtensionPoint factories = registry.getExtensionPoint(FactoryExtensionPoint.class);
        this.wsdlFactory = factories.getFactory(WSDLFactory.class);
        this.assemblyFactory = factories.getFactory(AssemblyFactory.class);
        this.policyFactory = factories.getFactory(PolicyFactory.class);
        this.javaInterfaceFactory = factories.getFactory(JavaInterfaceFactory.class);
        this.wsBindingFactory = factories.getFactory(WebServiceBindingFactory.class);
    }

    @Override
    public <T> void visitClass(Class<T> clazz, JavaImplementation type) throws IntrospectionException {
        
        boolean hasJaxwsAnnotation = false;
        
        // Process @ServiceMode annotation - JCA 11013
    	if ( clazz.getAnnotation(ServiceMode.class) != null ) {
    		addSOAPIntent(type);
    		hasJaxwsAnnotation = true;
    	}
    	
        // Process @WebService annotation - POJO_8029, POJO_8030
        WebService webServiceAnnotation = clazz.getAnnotation(WebService.class);
        org.oasisopen.sca.annotation.Service serviceAnnotation = clazz.getAnnotation(org.oasisopen.sca.annotation.Service.class);
        
        if (webServiceAnnotation != null &&
            serviceAnnotation == null) {
            String serviceName = clazz.getSimpleName();
            serviceName = getValue(webServiceAnnotation.name(), serviceName);
            
            String serviceInterfaceClassName = webServiceAnnotation.endpointInterface();
            
            String wsdlLocation = webServiceAnnotation.wsdlLocation();
            
            try {
                createService(type, clazz, serviceName, serviceInterfaceClassName, wsdlLocation, false);
            } catch (InvalidInterfaceException e) {
                throw new IntrospectionException(e);
            }
            hasJaxwsAnnotation = true;
        }
        
        // Process @WebServiceProvider annotation - JCA_11015, POJO_8034
        WebServiceProvider webServiceProviderAnnotation = clazz.getAnnotation(WebServiceProvider.class);
        if (webServiceProviderAnnotation != null) {
            // if the implmentation already has a service set, use it's name
            // and the new service, which uses the implementation as an interface,
            // will be replaced 
            String serviceName = clazz.getSimpleName();
            
            if (type.getServices().size() > 0){
                serviceName = ((Service)type.getServices().get(0)).getName();
            } 
            
            // the annotation may specify a service name
            serviceName = getValue(webServiceProviderAnnotation.serviceName(), serviceName);
            
            String wsdlLocation = webServiceProviderAnnotation.wsdlLocation();
            
            // Make sure that there is a service with an interface
            // based on the implementation class and have it replace 
            // any service with the same name
            try {
                createService(type, clazz, serviceName, null, wsdlLocation, true);
            } catch (InvalidInterfaceException e) {
                throw new IntrospectionException(e);
            }
            
            // Make sure all service references are remotable
            for ( Service service : type.getServices() ) {
                service.getInterfaceContract().getInterface().setRemotable(true);
            }
            
            hasJaxwsAnnotation = true;
        }         
        
        // Process @WebParam and @WebResult annotations - POJO_8031, POJO_8032
        Class<?> interfaze = clazz;
        Method[] implMethods = interfaze.getDeclaredMethods();
        for ( Service service : type.getServices() ) {
            JavaInterface javaInterface = (JavaInterface)service.getInterfaceContract().getInterface();
            interfaze = javaInterface.getJavaClass();
            
            if (interfaze == null){
                // this interface has come from an @WebService enpointInterface annotation
                // so hasn't been resolved. Use the implementation class as the interface
                interfaze = clazz;
            }
            
            boolean hasHeaderParam = false;
            for (Method method : interfaze.getDeclaredMethods()){
                // find the impl method for this service method    
                for (int i = 0; i < implMethods.length; i++){ 
                    Method implMethod = implMethods[i];
                    if (implMethod.getName().equals(method.getName())){
                        for (int j = 0; j < implMethod.getParameterTypes().length; j++) {
                            WebParam webParamAnnotation = getParameterAnnotation(implMethod, j, WebParam.class); 
                            if (webParamAnnotation != null &&
                                webParamAnnotation.header()) {
                                hasHeaderParam = true;
                                break;
                            }
                        }
                        
                        WebResult webResultAnnotation = implMethod.getAnnotation(WebResult.class);
                        if (webResultAnnotation != null &&
                            webResultAnnotation.header()){
                            hasHeaderParam = true;
                            break;
                        }
                    }
                }
            } 
            
            if (hasHeaderParam){                   
                // Add a SOAP intent to the service
                addSOAPIntent(service);
                hasJaxwsAnnotation = true;
            }
        }
        
        // Process @SOAPBinding annotation - POJO_8033
        if ( clazz.getAnnotation(SOAPBinding.class) != null ) {
            // If the implementation is annotated with @SOAPBinding,
            // give all services a SOAP intent
            for ( Service service : type.getServices() ) {
                addSOAPIntent(service);
            }
            hasJaxwsAnnotation = true;
        }               
        
        if (hasJaxwsAnnotation == true){
            // Note that services are based on JAXWS annotations so 
            // that during the build process a binding.ws can be added
            // if required
            for ( Service service : type.getServices() ) {
                service.setJAXWSService(true);
                createWSBinding(type, service);
            }
        }
    }
    
    /**
     * Utility methods
     */    

    private static String getValue(String value, String defaultValue) {
        return "".equals(value) ? defaultValue : value;
    }
    
    private Service createService(JavaImplementation type, Class<?> clazz, String serviceName, String javaInterfaceName, String wsdlFileName, boolean replace)  throws InvalidInterfaceException, IntrospectionException {
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
        
        // add the service model into the implementation type
        Service serviceAlreadyPresent = null;
        for (Service typeService : type.getServices()){
            if (typeService.getName().equals(service.getName())){
                serviceAlreadyPresent = typeService;
                break;
            }
        }
        
        if (replace == true){
            type.getServices().remove(serviceAlreadyPresent); 
            type.getServices().add(service);
        } else {
            if (serviceAlreadyPresent == null){
                type.getServices().add(service);
            }
        }
        
         return service;
    }    
    
    private <T extends Annotation> T getParameterAnnotation(Method method, int index, Class<T> annotationType) {
        Annotation[] annotations = method.getParameterAnnotations()[index];
        for (Annotation annotation : annotations) {
            if (annotation.annotationType() == annotationType) {
                return annotationType.cast(annotation);
            }
        }
        return null;
    }
    
    private void addSOAPIntent(PolicySubject policySubject){
        Intent soapIntent = policyFactory.createIntent();
        soapIntent.setName(Constants.SOAP_INTENT);         
        policySubject.getRequiredIntents().add(soapIntent);
    }
    
    private void createWSBinding(JavaImplementation javaImplementation, Service service){
        if(service.getBindings().size() == 0){
            WebServiceBinding wsBinding = wsBindingFactory.createWebServiceBinding();
            ExtensionType bindingType = policyFactory.createBindingType();
            bindingType.setType(WebServiceConstants.BINDING_WS_QNAME);
            bindingType.setUnresolved(true);
            ((PolicySubject)wsBinding).setExtensionType(bindingType);
            service.getBindings().add(wsBinding);
        }
    }

}
