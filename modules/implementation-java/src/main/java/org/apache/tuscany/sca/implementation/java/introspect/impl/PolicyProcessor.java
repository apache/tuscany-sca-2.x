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
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.namespace.QName;

import org.apache.tuscany.sca.assembly.AssemblyFactory;
import org.apache.tuscany.sca.assembly.Reference;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.core.FactoryExtensionPoint;
import org.apache.tuscany.sca.implementation.java.IntrospectionException;
import org.apache.tuscany.sca.implementation.java.JavaElementImpl;
import org.apache.tuscany.sca.implementation.java.JavaImplementation;
import org.apache.tuscany.sca.implementation.java.JavaParameterImpl;
import org.apache.tuscany.sca.implementation.java.introspect.BaseJavaClassVisitor;
import org.apache.tuscany.sca.interfacedef.java.JavaInterfaceFactory;
import org.apache.tuscany.sca.policy.Intent;
import org.apache.tuscany.sca.policy.PolicyFactory;
import org.apache.tuscany.sca.policy.PolicySet;
import org.apache.tuscany.sca.policy.PolicySubject;
import org.oasisopen.sca.ServiceRuntimeException;
import org.oasisopen.sca.annotation.PolicySets;
import org.oasisopen.sca.annotation.Qualifier;
import org.oasisopen.sca.annotation.Requires;

/**
 * Processes an {@link org.oasisopen.sca.annotation.Requires} annotation
 *
 * @version $Rev$ $Date$
 */
public class PolicyProcessor extends BaseJavaClassVisitor {

    private PolicyFactory policyFactory;

    public PolicyProcessor(AssemblyFactory assemblyFactory,
                           PolicyFactory policyFactory,
                           JavaInterfaceFactory javaInterfaceFactory) {
        super(assemblyFactory);
        this.policyFactory = policyFactory;
        this.javaInterfaceFactory = javaInterfaceFactory;
    }

    public PolicyProcessor(ExtensionPointRegistry registry) {
        super(registry);
        FactoryExtensionPoint factories = registry.getExtensionPoint(FactoryExtensionPoint.class);
        this.policyFactory = factories.getFactory(PolicyFactory.class);
    }

    @Override
    public <T> void visitClass(Class<T> clazz, JavaImplementation type) throws IntrospectionException {

        // Read intents on the Java implementation class
        readPolicySetAndIntents((PolicySubject)type, clazz);

        /*
        // FIXME: [rfeng] We might want to refactor this out
        // Find the business methods in the implementation class for all services
        Set<Method> methods = new HashSet<Method>();
        for (Service service : type.getServices()) {
            for (Operation op : service.getInterfaceContract().getInterface().getOperations()) {
                Method method;
                try {
                    method = JavaInterfaceUtil.findMethod(clazz, op);
                } catch (NoSuchMethodException e1) {
                    throw new IntrospectionException(e1);
                }
                if (method != null) {
                    methods.add(method);
                }
            }
        }
        for (Method method : methods) {
            JavaOperation op = javaInterfaceFactory.createJavaOperation(method);
            type.getOperations().add(op);
        }

        // Read the operation-level policy settings for the implementation
        for (Operation op : type.getOperations()) {
            JavaOperation operation = (JavaOperation)op;
            PolicySubject subject = op;
            Method method = operation.getJavaMethod();
            if (subject != null) {
                readPolicySetAndIntents(subject, method);
            }
        }
        */

        // Start to process annotations on the reference members
        Map<String, Reference> referenceMap = new HashMap<String, Reference>();
        for (Reference ref : type.getReferences()) {
            referenceMap.put(ref.getName(), ref);
        }
        Map<String, JavaElementImpl> members = type.getReferenceMembers();
        for (Map.Entry<String, JavaElementImpl> e : members.entrySet()) {
            Reference reference = referenceMap.get(e.getKey());
            readPolicySetAndIntents(reference, e.getValue().getAnchor());
        }

    }

    private void readPolicySetAndIntents(PolicySubject subject, AnnotatedElement element) {
        readIntents(element.getAnnotation(Requires.class), subject.getRequiredIntents());
        readSpecificIntents(element.getAnnotations(), subject.getRequiredIntents());
        readPolicySets(element.getAnnotation(PolicySets.class), subject.getPolicySets());
    }
    
    private void readPolicySetAndIntents(PolicySubject subject, JavaElementImpl element) {
        readIntents(element.getAnnotation(Requires.class), subject.getRequiredIntents());
        readSpecificIntents(element.getAnnotations(), subject.getRequiredIntents());
        readPolicySets(element.getAnnotation(PolicySets.class), subject.getPolicySets());
    }

    private void readSpecificIntents(Annotation[] annotations, List<Intent> requiredIntents) {
        for (Annotation a : annotations) {
            org.oasisopen.sca.annotation.Intent intentAnnotation =
                a.annotationType().getAnnotation(org.oasisopen.sca.annotation.Intent.class);
            if (intentAnnotation == null) {
                continue;
            }
            QName qname = null;
            String value = intentAnnotation.value();
            if (!value.equals("")) {
                qname = getQName(value);
            } else {
                qname = new QName(intentAnnotation.targetNamespace(), intentAnnotation.localPart());
            }
            Set<String> qualifiers = new HashSet<String>();
            for (Method m : a.annotationType().getMethods()) {
                Qualifier qualifier = m.getAnnotation(Qualifier.class);
                if (qualifier != null && m.getReturnType() == String[].class) {
                    try {
                        qualifiers.addAll(Arrays.asList((String[])m.invoke(a)));
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                }
            }
            qualifiers.remove("");
            if (qualifiers.isEmpty()) {
                Intent intent = policyFactory.createIntent();
                intent.setUnresolved(true);
                intent.setName(qname);
                requiredIntents.add(intent);
            } else {
                for (String q : qualifiers) {
                    Intent intent = policyFactory.createIntent();
                    intent.setUnresolved(true);
                    qname = new QName(qname.getNamespaceURI(), qname.getLocalPart() + "." + q);
                    intent.setName(qname);
                    requiredIntents.add(intent);
                }
            }
        }
    }

    /**
     * Read intent annotations on the given interface or class
     * @param intentAnnotation
     * @param requiredIntents
     */
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

    /**
     * Read policy set annotations on a given interface or class
     * @param policySetAnnotation
     * @param policySets
     */
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

    /**
     * Utility methods
     */

    /**
     * 
     * @param intentName
     * @return
     */
    private static QName getQName(String intentName) {
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

    @Override
    public void visitField(Field field, JavaImplementation type) throws IntrospectionException {
        // Check if a field that is not an SCA reference has any policySet/intent annotations
        JavaElementImpl element = new JavaElementImpl(field);
        if (!type.getReferenceMembers().values().contains(element)) {
            PolicySubject subject = assemblyFactory.createComponent();
            readPolicySetAndIntents(subject, field);
            if (subject.getPolicySets().isEmpty() && subject.getRequiredIntents().isEmpty()) {
                return;
            }
            throw new ServiceRuntimeException(
                                              "[JCA70002,JCA70005] Field that is not an SCA reference cannot have policySet/intent annotations: " + field);
        }
    }

    @Override
    public void visitConstructorParameter(JavaParameterImpl parameter, JavaImplementation type) {
        if (!type.getReferenceMembers().values().contains(parameter)) {
            PolicySubject subject = assemblyFactory.createComponent();
            readPolicySetAndIntents(subject, parameter);
            if (subject.getPolicySets().isEmpty() && subject.getRequiredIntents().isEmpty()) {
                return;
            }
            throw new ServiceRuntimeException(
                                              "[JCA70002,JCA70005] Constructor parameter that is not an SCA reference cannot have policySet/intent annotations: " + parameter);
        }
    }

    @Override
    public void visitMethod(Method method, JavaImplementation type) throws IntrospectionException {
        Set<AnnotatedElement> annotatedElements = new HashSet<AnnotatedElement>();
        for (JavaElementImpl element : type.getReferenceMembers().values()) {
            annotatedElements.add(element.getAnchor());
        }
        // Check if a field that is not an SCA reference has any policySet/intent annotations
        if (!annotatedElements.contains(method)) {
            PolicySubject subject = assemblyFactory.createComponent();
            readPolicySetAndIntents(subject, method);
            if (subject.getPolicySets().isEmpty() && subject.getRequiredIntents().isEmpty()) {
                return;
            }
            throw new ServiceRuntimeException(
                                              "[JCA70002,JCA70005] Method that is not an SCA reference cannot have policySet/intent annotations: " + method);
        }
    }

}
