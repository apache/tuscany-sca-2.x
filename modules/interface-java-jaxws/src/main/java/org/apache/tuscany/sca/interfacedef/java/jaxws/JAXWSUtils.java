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

package org.apache.tuscany.sca.interfacedef.java.jaxws;

import javax.jws.WebService;
import javax.xml.namespace.QName;
import javax.xml.ws.WebServiceProvider;

import org.apache.tuscany.sca.interfacedef.InvalidInterfaceException;
import org.apache.tuscany.sca.interfacedef.java.JavaInterface;
import org.apache.tuscany.sca.interfacedef.util.JavaXMLMapper;


/**
 * A set of utility methods for processing JAXWS annotations that are 
 * shared between Java inteface and implementation processing
 */
public class JAXWSUtils {
    
    /**
     * JAXWS annotations may identify a service interface via either
     *   - an interface class name, e.g. @WebService(endpointInterface="my.service.ServiceImpl") 
     *   - a wsdl file name, e.g. @WebService(wsdlLocation="some.wsdl")
     *   - a Java class/interface, e.g. @WebService
     * This operation configures the Java interface based on these separate pieces
     * of information. The resulting interface contract must be subsequently resolved in order that
     * the named endpoint interface class or wsdl file is found
     *   
     * @param javaInterface the Tuscany representation of the Java interface
     * @param clazz the Java class that the interface refers to (may have JAXWS annotations)
     * @return
     */
    public static JavaInterface configureJavaInterface(JavaInterface javaInterface,
                                                       Class<?> clazz){
        
        String servineNamespace = JavaXMLMapper.getNamespace(clazz);
        String serviceName = clazz.getSimpleName();
        QName serviceQName = null;
        String serviceInterfaceClassName = null;
        String wsdlFileName = null;
        
        WebService webServiceAnnotation = clazz.getAnnotation(WebService.class);
        if (webServiceAnnotation != null) {
            servineNamespace = getValue(webServiceAnnotation.targetNamespace(), servineNamespace);
            serviceName = getValue(webServiceAnnotation.name(), serviceName);
            serviceInterfaceClassName = webServiceAnnotation.endpointInterface();
            wsdlFileName = webServiceAnnotation.wsdlLocation();
            javaInterface.setRemotable(true);
        }
        
        WebServiceProvider webServiceProviderAnnotation = clazz.getAnnotation(WebServiceProvider.class);
        if (webServiceProviderAnnotation != null) {
            servineNamespace = getValue(webServiceProviderAnnotation.targetNamespace(), servineNamespace);
            serviceName = getValue(webServiceProviderAnnotation.serviceName(), serviceName);
            wsdlFileName = webServiceProviderAnnotation.wsdlLocation();
            javaInterface.setRemotable(true);
        }  
        
        serviceQName = new QName(servineNamespace, serviceName);
        javaInterface.setQName(serviceQName);
        
        // use the provided Java interface name to overwrite
        // any Java interface created from an implemented interfaces
        if (serviceInterfaceClassName != null &&
                serviceInterfaceClassName.length() > 0){
            javaInterface.setName(serviceInterfaceClassName);
            javaInterface.setJAXWSJavaInterfaceName(serviceInterfaceClassName);
            javaInterface.setUnresolved(true);
        } 
        
        // Store the WSDL location if it's specified in 
        // the @WebService annotation. Later this is resolved and is attached 
        // to the Java interface contract in the normalized space so that effectively the contract
        // has both Java and WSDL interfaces. This allows databinding to 
        // operate correctly as it still expects a Java interface for a Java implementation
        if (wsdlFileName != null &&
            wsdlFileName.length() > 0){         
            javaInterface.setJAXWSWSDLLocation(wsdlFileName);
        }  
        
        return javaInterface;
    }      
    
    /**
     * Given a class that may have @WebService or @WebServiceProvider 
     * annotations this determines what the service QName should be. 
     * 
     * @param clazz
     * @return
     */
/*    
    public static QName calculateServiceQName(JavaInterface javaInterface, Class<?> clazz){
        WebService webServiceAnnotation = clazz.getAnnotation(WebService.class);
        String servineNamespace = JavaXMLMapper.getNamespace(clazz);
        String serviceName = clazz.getSimpleName();
        if (webServiceAnnotation != null) {
            servineNamespace = getValue(webServiceAnnotation.targetNamespace(), servineNamespace);
            serviceName = getValue(webServiceAnnotation.name(), serviceName);
            javaInterface.setRemotable(true);
        }
        
        WebServiceProvider webServiceProviderAnnotation = clazz.getAnnotation(WebServiceProvider.class);
        if (webServiceProviderAnnotation != null) {
            servineNamespace = getValue(webServiceProviderAnnotation.targetNamespace(), servineNamespace);
            serviceName = getValue(webServiceProviderAnnotation.serviceName(), serviceName);
            javaInterface.setRemotable(true);
        }  
        
        QName serviceQName = new QName(servineNamespace, serviceName);
        javaInterface.setQName(serviceQName);
        
        return serviceQName;
    }
*/
    
    /**
     * Given a class that may have @WebService or @WebServiceProvider 
     * annotations this determines what the name of the Java class 
     * that defines the service interface should be 
     * 
     * @param clazz
     * @return
     */
/*    
    public static String calculateServiceInterfaceJavaClassName(JavaInterface javaInterface, Class<?> clazz){
        String serviceInterfaceClassName = null;
        
        WebService webServiceAnnotation = clazz.getAnnotation(WebService.class);        
        if (webServiceAnnotation != null) {
            serviceInterfaceClassName = webServiceAnnotation.endpointInterface();
            javaInterface.setRemotable(true);
        } 
        
        return serviceInterfaceClassName;
    } 
*/ 
    
    /**
     * Given a class that may have @WebService or @WebServiceProvider 
     * annotations this determines what the name of the WSDL file is  
     * that defines the service interface should be 
     * 
     * @param clazz
     * @return
     */
/*    
    public static String calculateServiceInterfaceWSDLLocation(JavaInterface javaInterface, Class<?> clazz){
        WebService webServiceAnnotation = clazz.getAnnotation(WebService.class);
        String wsdlLocation = null;
        
        if (webServiceAnnotation != null) {
            wsdlLocation = webServiceAnnotation.wsdlLocation();
            javaInterface.setRemotable(true);
        } 
        
        WebServiceProvider webServiceProviderAnnotation = clazz.getAnnotation(WebServiceProvider.class);
        
        if (webServiceProviderAnnotation != null) {
            wsdlLocation = webServiceProviderAnnotation.wsdlLocation();
            javaInterface.setRemotable(true);
        } 
        
        return wsdlLocation;
    }     
*/    
    
    /**
     * JAXWS annotations may identify a service interface via either
     *   - an interface class name, e.g. @WebService(endpointInterface="my.service.ServiceImpl") 
     *   - a wsdl file name, e.g. @WebService(wsdlLocation="some.wsdl")
     *   - a Java class/interface, e.g. @WebService
     * This operation configures the Java interface based on these separate pieces
     * of information. The resulting interface contract must be subsequently resolved in order that
     * the named endpoint interface class or wsdl file is found
     *   
     * @param javaInterface
     * @param servicceQName
     * @param javaInterfaceName
     * @param wsdlFileName
     * @return
     */
/*    
    public static JavaInterface configureJavaInterface(JavaInterface javaInterface,
                                                       QName serviceQName,
                                                       String javaInterfaceName, 
                                                       String wsdlFileName)  throws InvalidInterfaceException {
        
        // use the provided Java interface name to overwrite
        // any Java contract created from an implemented interfaces
        if (javaInterfaceName != null &&
            javaInterfaceName.length() > 0){
            javaInterface.setName(javaInterfaceName);
            javaInterface.setJAXWSJavaInterfaceName(javaInterfaceName);
            javaInterface.setQName(serviceQName);
            javaInterface.setUnresolved(true);
        } else {
            // we use the bean class as the service interface if no interface
            // has already been set. This should have already been resolved
            javaInterface.setQName(serviceQName);
        }
        
        // Store the WSDL location if it's specified in 
        // the @WebService annotation. Later this is resolved and is attached 
        // to the Java interface contract in the normalized space so that effectively the contract
        // has both Java and WSDL interfaces. This allows databinding to 
        // operate correctly as it still expects a Java interface for a Java implementation
        if (wsdlFileName != null &&
            wsdlFileName.length() > 0){         
            javaInterface.setJAXWSWSDLLocation(wsdlFileName);
        }  
        
        return javaInterface;
    }  
*/
    
    
    /**
     * JAXWS annotations may identify a service interface via either
     *   - an interface class name, e.g. @WebService(endpointInterface="my.service.ServiceImpl") 
     *   - a wsdl file name, e.g. @WebService(wsdlLocation="some.wsdl")
     *   - a Java class/interface, e.g. @WebService
     * This operation creates the right sort of interface contract based on these separate pieces
     * of information. The resulting interface contract must be subsequently resolved in order that
     * the named endpoint interface class or wsdl file is found
     *   
     * @param javaInterfaceFactory
     * @param wsdlInterfaceFactory
     * @param clazz
     * @param javaInterfaceName
     * @param wsdlFileName
     * @return
     * @throws InvalidInterfaceException
     */
/*    
    public static JavaInterfaceContract configureJavaInterface(JavaInterfaceFactory javaInterfaceFactory, 
                                                               WSDLFactory wsdlFactory,
                                                               JavaInterfaceContract javaInterfaceContract,
                                                               Class<?> clazz, 
                                                               QName serviceQName,
                                                               String javaInterfaceName, 
                                                               String wsdlFileName)  throws InvalidInterfaceException {
        
        // use the provided Java interface name to overwrite
        // any Java contract created from an implemented interfaces
        if (javaInterfaceName != null &&
            javaInterfaceName.length() > 0){
            JavaInterface callInterface = javaInterfaceFactory.createJavaInterface();
            callInterface.setName(javaInterfaceName);
            callInterface.setQName(serviceQName);
            callInterface.setRemotable(true);
            callInterface.setUnresolved(true);
            javaInterfaceContract.setInterface(callInterface);
        } else {
            // we use the bean class as the service interface if no interface
            // has already been set
            if (javaInterfaceContract.getInterface() == null){
                JavaInterface callInterface = javaInterfaceFactory.createJavaInterface(clazz);
                callInterface.setQName(serviceQName);
                callInterface.setRemotable(true);
                callInterface.setUnresolved(false); // this will already be false but this makes it easy to follow the logic
                javaInterfaceContract.setInterface(callInterface);
            } else {
                JavaInterface callInterface = (JavaInterface)javaInterfaceContract.getInterface();
                callInterface.setRemotable(true);
                callInterface.setQName(serviceQName);
            }
        }
        
        // create the logical WSDL interface if it's specified in 
        // the @WebService annotation. This is attached to the Java interface
        // contract in the normalized space so that effectively the contract
        // has both Java and WSDL interfaces. This allows databinding to 
        // operate correctly as it expects a Java interface for a Java implementation
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
        
        return javaInterfaceContract;
    }
*/
    
    private static String getValue(String value, String defaultValue) {
        return "".equals(value) ? defaultValue : value;
    }    
}
