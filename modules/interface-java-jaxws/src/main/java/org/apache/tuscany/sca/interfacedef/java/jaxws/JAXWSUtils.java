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
    
    private static String getValue(String value, String defaultValue) {
        return "".equals(value) ? defaultValue : value;
    }    
}
