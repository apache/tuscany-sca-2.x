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

import static org.apache.tuscany.sca.implementation.java.introspect.JavaIntrospectionHelper.getAllInterfaces;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.jws.WebService;
import javax.xml.namespace.QName;

import org.apache.tuscany.sca.assembly.AssemblyFactory;
import org.apache.tuscany.sca.assembly.Service;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.implementation.java.IntrospectionException;
import org.apache.tuscany.sca.implementation.java.JavaElementImpl;
import org.apache.tuscany.sca.implementation.java.JavaImplementation;
import org.apache.tuscany.sca.implementation.java.introspect.BaseJavaClassVisitor;
import org.apache.tuscany.sca.implementation.java.introspect.JavaIntrospectionHelper;
import org.apache.tuscany.sca.interfacedef.InvalidInterfaceException;
import org.apache.tuscany.sca.interfacedef.java.JavaInterface;
import org.apache.tuscany.sca.interfacedef.java.JavaInterfaceContract;
import org.apache.tuscany.sca.interfacedef.java.JavaInterfaceFactory;
import org.apache.tuscany.sca.interfacedef.util.JavaXMLMapper;
import org.oasisopen.sca.ServiceReference;
import org.oasisopen.sca.annotation.Callback;
import org.oasisopen.sca.annotation.Remotable;

/**
 * Process JAXWS annotations and updates the component type accordingly
 * 
 */
public class JAXWSProcessor extends BaseJavaClassVisitor {
    
    public JAXWSProcessor(AssemblyFactory assemblyFactory, JavaInterfaceFactory javaFactory) {
        super(assemblyFactory);
        this.javaInterfaceFactory = javaFactory;
    }
    
    public JAXWSProcessor(ExtensionPointRegistry registry) {
        super(registry);
    }

    @Override
    public <T> void visitClass(Class<T> clazz, JavaImplementation type) throws IntrospectionException {
        WebService webService = clazz.getAnnotation(WebService.class);
        String tns = JavaXMLMapper.getNamespace(clazz);
        String localName = clazz.getSimpleName();
        Class<?> interfaze = clazz;
        if (webService != null) {
            tns = getValue(webService.targetNamespace(), tns);
            localName = getValue(webService.name(), localName);
            
            String serviceInterfaceName = webService.endpointInterface();
            // TODO - how to resolve this interface name 
            //        needs to be done higher up where we have 
            //        access to the resolver. 

            Service service;
            try {
                service = createService(clazz, interfaze, localName);
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
    
    public Service createService(Class<?> clazz, Class<?> interfaze, String name) throws InvalidInterfaceException {
        Service service = assemblyFactory.createService();
        JavaInterfaceContract interfaceContract = javaInterfaceFactory.createJavaInterfaceContract();
        service.setInterfaceContract(interfaceContract);

        if (name == null) {
            service.setName(interfaze.getSimpleName());
        } else {
            service.setName(name);
        }

        JavaInterface callInterface = javaInterfaceFactory.createJavaInterface(interfaze);
        boolean remotable = clazz.getAnnotation(Remotable.class) != null;
        if (remotable){
            callInterface.setRemotable(true);
        }
        service.getInterfaceContract().setInterface(callInterface);
        
        if (callInterface.getCallbackClass() != null) {
            JavaInterface callbackInterface = javaInterfaceFactory.createJavaInterface(callInterface.getCallbackClass());
            if (remotable){
                callbackInterface.setRemotable(true);
            }
            service.getInterfaceContract().setCallbackInterface(callbackInterface);
        }
        return service;
    }    

}
