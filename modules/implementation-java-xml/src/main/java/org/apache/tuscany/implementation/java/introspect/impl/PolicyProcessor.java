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
package org.apache.tuscany.implementation.java.introspect.impl;

import java.lang.reflect.Method;
import java.util.List;

import javax.xml.namespace.QName;

import org.apache.tuscany.assembly.AssemblyFactory;
import org.apache.tuscany.assembly.Callback;
import org.apache.tuscany.assembly.Service;
import org.apache.tuscany.implementation.java.JavaImplementation;
import org.apache.tuscany.implementation.java.introspect.IntrospectionException;
import org.apache.tuscany.interfacedef.InterfaceContract;
import org.apache.tuscany.interfacedef.Operation;
import org.apache.tuscany.interfacedef.java.JavaInterface;
import org.apache.tuscany.interfacedef.java.JavaInterfaceContract;
import org.apache.tuscany.policy.Intent;
import org.apache.tuscany.policy.PolicyFactory;
import org.osoa.sca.annotations.Requires;

/**
 * Processes an {@link org.osoa.sca.annotations.Requires} annotation
 * 
 * @version $Rev:
 */
public class PolicyProcessor extends BaseJavaClassVisitor {
    
    private PolicyFactory policyFactory;

    public PolicyProcessor(AssemblyFactory assemblyFactory, PolicyFactory policyFactory) {
        super(assemblyFactory);
        this.policyFactory = policyFactory;
    }

    private QName getQName(String intentName) {
        QName qname;
        if (intentName.startsWith("{")) {
            int i = intentName.indexOf('}');
            if (i != -1) {
                qname = new QName(intentName.substring(1, i), intentName.substring(i + 1));
            } else {
                qname = new QName("", intentName);
            }
        } else {
            qname = new QName("", intentName);
        }
        return qname;
    }

    /**
     * Read policy intents on the given interface or class 
     * @param clazz
     * @param requiredIntents
     */
    private void readIntents(Class<?> clazz, List<Intent> requiredIntents) {
        Requires intentAnnotation = clazz.getAnnotation(Requires.class);
        if (intentAnnotation != null) {
            String[] intentNames = intentAnnotation.value();
            if (intentNames.length != 0) {
                for (String intentName : intentNames) {
                    
                    // Add each intent to the list
                    Intent intent = policyFactory.createIntent();
                    intent.setName(getQName(intentName));
                    requiredIntents.add(intent);
                }
            }
        }
    }
    
    private void readIntents(Method method, List<Intent> requiredIntents) {
        Requires intentAnnotation = method.getAnnotation(Requires.class);
        if (intentAnnotation != null) {
            String[] intentNames = intentAnnotation.value();
            if (intentNames.length != 0) {
                Operation operation = assemblyFactory.createOperation();
                operation.setName(method.getName());
                operation.setUnresolved(true);
                for (String intentName : intentNames) {

                    // Add each intent to the list, associated with the
                    // operation corresponding to the annotated method
                    Intent intent = policyFactory.createIntent();
                    intent.setName(getQName(intentName));
                    intent.getOperations().add(operation);
                    requiredIntents.add(intent);
                }
            }
        }
    }

    public <T> void visitClass(Class<T> clazz, JavaImplementation type) throws IntrospectionException {
        
        // Read intents on the Java implementation class
        readIntents(clazz, type.getRequiredIntents());
        
        // Process annotations on the service interfaces
        //TODO This will have to move to a JavaInterface introspector later
        for (Service service: type.getServices()) {
            InterfaceContract interfaceContract = service.getInterfaceContract();
            if (interfaceContract instanceof JavaInterfaceContract) {
                JavaInterfaceContract javaInterfaceContract = (JavaInterfaceContract)interfaceContract;

                // Read intents on the service interface
                if (javaInterfaceContract.getInterface() != null) {
                    JavaInterface javaInterface = (JavaInterface)javaInterfaceContract.getInterface();
                    if (javaInterface.getJavaClass() != null) {
                        readIntents(javaInterface.getJavaClass(), service.getRequiredIntents());

                        // Read intents on the service interface methods 
                        Method[] methods = javaInterface.getJavaClass().getMethods();
                        for (Method method: methods) {
                            readIntents(method, service.getRequiredIntents());
                        }
                    }
                    
                }
                
                // Read intents on the callback interface 
                if (javaInterfaceContract.getCallbackInterface() != null) {
                    JavaInterface javaCallbackInterface = (JavaInterface)javaInterfaceContract.getCallbackInterface();
                    if (javaCallbackInterface.getJavaClass() != null) {
                        Callback callback = service.getCallback();
                        if (callback == null) {
                            callback = assemblyFactory.createCallback();
                            service.setCallback(callback);
                        }
                        readIntents(javaCallbackInterface.getJavaClass(), callback.getRequiredIntents());

                        // Read intents on the callback interface methods 
                        Method[] methods = javaCallbackInterface.getJavaClass().getMethods();
                        for (Method method: methods) {
                            readIntents(method, callback.getRequiredIntents());
                        }
                    }
                }
            }
        }
    }

    public void visitMethod(Method method, JavaImplementation type) throws IntrospectionException {
        
        // Read the intents specified on the given implementation method
        readIntents(method, type.getRequiredIntents());
    }
}
