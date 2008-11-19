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
package org.apache.tuscany.sca.interfacedef.java.impl;

import java.lang.reflect.Method;
import java.util.List;

import javax.xml.namespace.QName;

import org.apache.tuscany.sca.interfacedef.InvalidInterfaceException;
import org.apache.tuscany.sca.interfacedef.Operation;
import org.apache.tuscany.sca.interfacedef.java.JavaInterface;
import org.apache.tuscany.sca.interfacedef.java.JavaOperation;
import org.apache.tuscany.sca.interfacedef.java.introspect.JavaInterfaceVisitor;
import org.apache.tuscany.sca.policy.Intent;
import org.apache.tuscany.sca.policy.PolicyFactory;
import org.apache.tuscany.sca.policy.PolicySet;
import org.osoa.sca.annotations.PolicySets;
import org.osoa.sca.annotations.Requires;

/**
 * Processes an {@link org.osoa.sca.annotations.Requires} annotation
 *
 * @version $Rev$ $Date$
 */
public class PolicyJavaInterfaceVisitor implements JavaInterfaceVisitor {
    private PolicyFactory policyFactory;

    public PolicyJavaInterfaceVisitor(PolicyFactory policyFactory) {
        super();
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
    private void readIntentsAndPolicySets(Class<?> clazz, List<Intent> requiredIntents, List<PolicySet> policySets) {
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

    public void visitInterface(JavaInterface javaInterface) throws InvalidInterfaceException {

        if (javaInterface.getJavaClass() != null) {
            readIntentsAndPolicySets(javaInterface.getJavaClass(), javaInterface.getRequiredIntents(), javaInterface
                .getPolicySets());

            // Read intents on the service interface methods 
            List<Operation> operations = javaInterface.getOperations();
            for (Operation op : operations) {
                JavaOperation operation = (JavaOperation)op;
                Method method = operation.getJavaMethod();
                if (method.getAnnotation(Requires.class) != null || method.getAnnotation(PolicySets.class) != null) {
                    readIntents(method.getAnnotation(Requires.class), op.getRequiredIntents());
                    readPolicySets(method.getAnnotation(PolicySets.class), op.getPolicySets());
                }
            }
        }
    }

}
