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

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

import javax.xml.namespace.QName;

import org.apache.tuscany.sca.assembly.AssemblyFactory;
import org.apache.tuscany.sca.assembly.Callback;
import org.apache.tuscany.sca.assembly.ConfiguredOperation;
import org.apache.tuscany.sca.assembly.OperationsConfigurator;
import org.apache.tuscany.sca.assembly.Reference;
import org.apache.tuscany.sca.assembly.Service;
import org.apache.tuscany.sca.implementation.java.IntrospectionException;
import org.apache.tuscany.sca.implementation.java.JavaImplementation;
import org.apache.tuscany.sca.interfacedef.InterfaceContract;
import org.apache.tuscany.sca.interfacedef.java.JavaInterface;
import org.apache.tuscany.sca.interfacedef.java.JavaInterfaceContract;
import org.apache.tuscany.sca.policy.Intent;
import org.apache.tuscany.sca.policy.PolicyFactory;
import org.apache.tuscany.sca.policy.PolicySet;
import org.apache.tuscany.sca.policy.PolicySetAttachPoint;
import org.osoa.sca.annotations.PolicySets;
import org.osoa.sca.annotations.Requires;

/**
 * Processes an {@link org.osoa.sca.annotations.Requires} annotation
 *
 * @version $Rev$ $Date$
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
    private void readIntentsAndPolicySets(Class<?> clazz, 
                                          List<Intent> requiredIntents, 
                                          List<PolicySet> policySets) {
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
        
        PolicySets policySetAnnotation = clazz.getAnnotation(PolicySets.class);
        if (policySetAnnotation != null) {
            String[] policySetNames = policySetAnnotation.value();
            if (policySetNames.length != 0) {
                for (String policySetName : policySetNames) {

                    // Add each intent to the list
                    PolicySet policySet = policyFactory.createPolicySet();
                    policySet.setName(getQName(policySetName));
                    policySets.add(policySet);
                }
            }
        }
    }
    
    private void readIntents(Requires intentAnnotation, List<Intent> requiredIntents) {
        //Requires intentAnnotation = method.getAnnotation(Requires.class);
        if (intentAnnotation != null) {
            String[] intentNames = intentAnnotation.value();
            if (intentNames.length != 0) {
                //Operation operation = assemblyFactory.createOperation();
                //operation.setName(method.getName());
                //operation.setUnresolved(true);
                for (String intentName : intentNames) {

                    // Add each intent to the list, associated with the
                    // operation corresponding to the annotated method
                    Intent intent = policyFactory.createIntent();
                    intent.setName(getQName(intentName));
                    //intent.getOperations().add(operation);
                    requiredIntents.add(intent);
                }
            }
        }
    }
    
    private void readPolicySets(PolicySets policySetAnnotation, List<PolicySet> policySets) {
        if (policySetAnnotation != null) {
            String[] policySetNames = policySetAnnotation.value();
            if (policySetNames.length != 0) {
                //Operation operation = assemblyFactory.createOperation();
                //operation.setName(method.getName());
                //operation.setUnresolved(true);
                for (String policySetName : policySetNames) {
                    // Add each intent to the list, associated with the
                    // operation corresponding to the annotated method
                    PolicySet policySet = policyFactory.createPolicySet();
                    policySet.setName(getQName(policySetName));
                    //intent.getOperations().add(operation);
                    policySets.add(policySet);
                }
            }
        }
    }

    @Override
    public <T> void visitClass(Class<T> clazz, JavaImplementation type) throws IntrospectionException {
        
        // Read intents on the Java implementation class
        if ( type instanceof PolicySetAttachPoint ) {
            readIntentsAndPolicySets(clazz, 
                                     ((PolicySetAttachPoint)type).getRequiredIntents(),
                                     ((PolicySetAttachPoint)type).getPolicySets());
        }
        
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
                        readIntentsAndPolicySets(javaInterface.getJavaClass(), 
                                                 service.getRequiredIntents(),
                                                 service.getPolicySets());

                        // Read intents on the service interface methods 
                        Method[] methods = javaInterface.getJavaClass().getMethods();
                        ConfiguredOperation confOp = null;
                        for (Method method: methods) {
                            if ( method.getAnnotation(Requires.class) != null  ||    
                                method.getAnnotation(PolicySets.class) != null ) {
                                confOp = assemblyFactory.createConfiguredOperation();
                                confOp.setName(method.getName());
                                confOp.setContractName(service.getName());
                            
                                service.getConfiguredOperations().add(confOp);
                                readIntents(method.getAnnotation(Requires.class), confOp.getRequiredIntents());
                                readPolicySets(method.getAnnotation(PolicySets.class), confOp.getPolicySets());
                            }
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
                        readIntentsAndPolicySets(javaCallbackInterface.getJavaClass(), 
                                                 callback.getRequiredIntents(),
                                                 callback.getPolicySets());

                        // Read intents on the callback interface methods 
                        Method[] methods = javaCallbackInterface.getJavaClass().getMethods();
                        ConfiguredOperation confOp = null;
                        for (Method method: methods) {
                            confOp = assemblyFactory.createConfiguredOperation();
                            confOp.setName(method.getName());
                            callback.getConfiguredOperations().add(confOp);
                            readIntents(method.getAnnotation(Requires.class), confOp.getRequiredIntents());
                            readPolicySets(method.getAnnotation(PolicySets.class), confOp.getPolicySets());
                        }
                    }
                }
            }
        }
    }

    private Reference getReference(Method method, JavaImplementation type) {
        //since the ReferenceProcessor is called ahead of the PolicyProcessor the type should have
        //picked up the reference setter method
        org.osoa.sca.annotations.Reference annotation = 
                                        method.getAnnotation(org.osoa.sca.annotations.Reference.class);
        if (annotation != null) {
            if (JavaIntrospectionHelper.isSetter(method)) {
                String name = annotation.name();
                if ("".equals(name)) {
                    name = JavaIntrospectionHelper.toPropertyName(method.getName());
                }
                return getReferenceByName(name, type);
            }
        }
        return null;
    }
    
    private Reference getReferenceByName(String name, JavaImplementation type) {
        for ( Reference reference : type.getReferences() ) {
            if ( reference.getName().equals(name) ) {
                return reference;
            }
        }
        return null;
    }
    
    @Override
    public void visitField(Field field, JavaImplementation type) throws IntrospectionException {
        org.osoa.sca.annotations.Reference annotation = 
            field.getAnnotation( org.osoa.sca.annotations.Reference.class);
        if (annotation == null) {
            return;
        }
        String name = annotation.name();
        if ("".equals(name)) {
            name = field.getName();
        }
        
        Reference reference = null;
        if ( (reference = getReferenceByName(name, type)) != null ) {
            readIntents(field.getAnnotation(Requires.class), reference.getRequiredIntents());
            readPolicySets(field.getAnnotation(PolicySets.class), reference.getPolicySets());
        }
    }

    @Override
    public void visitMethod(Method method, JavaImplementation type) throws IntrospectionException {
        Reference reference = null;
        if ( (reference = getReference(method, type)) != null ) {
            readIntents(method.getAnnotation(Requires.class), reference.getRequiredIntents());
            readPolicySets(method.getAnnotation(PolicySets.class), reference.getPolicySets());
        } else {
            if ( type instanceof OperationsConfigurator ) {
                //Read the intents specified on the given implementation method
                if ( (method.getAnnotation(Requires.class) != null || 
                        method.getAnnotation(PolicySets.class) != null ) && 
                            (type instanceof PolicySetAttachPoint )) {
                    ConfiguredOperation confOp = assemblyFactory.createConfiguredOperation();
                    confOp.setName(method.getName());
                    ((OperationsConfigurator)type).getConfiguredOperations().add(confOp);
            
                
                    readIntents(method.getAnnotation(Requires.class), confOp.getRequiredIntents());
                    readPolicySets(method.getAnnotation(PolicySets.class), confOp.getPolicySets());
                }
            }
        }
    }
}
