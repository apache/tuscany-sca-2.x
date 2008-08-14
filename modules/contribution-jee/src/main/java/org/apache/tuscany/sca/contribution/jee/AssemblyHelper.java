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

package org.apache.tuscany.sca.contribution.jee;

import java.util.HashMap;
import java.util.Map;

import javax.xml.namespace.QName;

import org.apache.tuscany.sca.assembly.AssemblyFactory;
import org.apache.tuscany.sca.assembly.Component;
import org.apache.tuscany.sca.assembly.ComponentProperty;
import org.apache.tuscany.sca.assembly.ComponentReference;
import org.apache.tuscany.sca.assembly.ComponentService;
import org.apache.tuscany.sca.assembly.ComponentType;
import org.apache.tuscany.sca.assembly.Composite;
import org.apache.tuscany.sca.assembly.CompositeReference;
import org.apache.tuscany.sca.assembly.CompositeService;
import org.apache.tuscany.sca.assembly.DefaultAssemblyFactory;
import org.apache.tuscany.sca.assembly.Property;
import org.apache.tuscany.sca.assembly.Reference;
import org.apache.tuscany.sca.assembly.Service;
import org.apache.tuscany.sca.interfacedef.InvalidInterfaceException;
import org.apache.tuscany.sca.interfacedef.java.DefaultJavaInterfaceFactory;
import org.apache.tuscany.sca.interfacedef.java.JavaInterface;
import org.apache.tuscany.sca.interfacedef.java.JavaInterfaceContract;
import org.apache.tuscany.sca.interfacedef.java.JavaInterfaceFactory;
import org.apache.tuscany.sca.policy.DefaultPolicyFactory;
import org.apache.tuscany.sca.policy.Intent;
import org.apache.tuscany.sca.policy.PolicyFactory;

/**
 * @version $Rev$ $Date$
 */
public class AssemblyHelper {
    private AssemblyFactory af;
    private JavaInterfaceFactory jif;

    public static final Map<String, QName> ALLOWED_ENV_ENTRY_TYPES;
    public static final Intent CONVERSATIONAL_INTENT;
    public static final Intent EJB_INTENT;
    static {
        ALLOWED_ENV_ENTRY_TYPES = new HashMap<String, QName>();
        ALLOWED_ENV_ENTRY_TYPES.put(String.class.getName(), new QName("http://www.w3.org/2001/XMLSchema", "string",
                                                                      "xsd"));
        ALLOWED_ENV_ENTRY_TYPES.put(Character.class.getName(), new QName("http://www.w3.org/2001/XMLSchema", "string",
                                                                         "xsd"));
        ALLOWED_ENV_ENTRY_TYPES.put(Byte.class.getName(), new QName("http://www.w3.org/2001/XMLSchema", "byte", "xsd"));
        ALLOWED_ENV_ENTRY_TYPES.put(Short.class.getName(),
                                    new QName("http://www.w3.org/2001/XMLSchema", "short", "xsd"));
        ALLOWED_ENV_ENTRY_TYPES.put(Integer.class.getName(),
                                    new QName("http://www.w3.org/2001/XMLSchema", "int", "xsd"));
        ALLOWED_ENV_ENTRY_TYPES.put(Long.class.getName(), new QName("http://www.w3.org/2001/XMLSchema", "long", "xsd"));
        ALLOWED_ENV_ENTRY_TYPES.put(Boolean.class.getName(), new QName("http://www.w3.org/2001/XMLSchema", "boolean",
                                                                       "xsd"));
        ALLOWED_ENV_ENTRY_TYPES.put(Double.class.getName(), new QName("http://www.w3.org/2001/XMLSchema", "double",
                                                                      "xsd"));
        ALLOWED_ENV_ENTRY_TYPES.put(Float.class.getName(),
                                    new QName("http://www.w3.org/2001/XMLSchema", "float", "xsd"));
    }

    static {
        PolicyFactory dpf = new DefaultPolicyFactory();
        CONVERSATIONAL_INTENT = dpf.createIntent();
        CONVERSATIONAL_INTENT.setName(new QName("http://www.osoa.org/xmlns/sca/1.0", "conversational"));

        EJB_INTENT = dpf.createIntent();
        EJB_INTENT.setName(new QName("http://www.osoa.org/xmlns/sca/1.0", "ejb"));
    }

    public AssemblyHelper() {
        super();
        af = new DefaultAssemblyFactory();
        jif = new DefaultJavaInterfaceFactory();
    }

    public AssemblyHelper(AssemblyFactory af, JavaInterfaceFactory jif) {
        super();
        this.af = af;
        this.jif = jif;
    }

    public JavaInterfaceContract createInterfaceContract(Class<?> clazz) throws InvalidInterfaceException {
        JavaInterface ji = jif.createJavaInterface(clazz);
        JavaInterfaceContract jic = jif.createJavaInterfaceContract();
        jic.setInterface(ji);

        return jic;
    }

    public ComponentService createComponentService() {
        return af.createComponentService();
    }

    public ComponentReference createComponentReference() {
        return af.createComponentReference();
    }

    public ComponentProperty createComponentProperty() {
        return af.createComponentProperty();
    }

    public ComponentType createComponentType() {
        return af.createComponentType();
    }

    public Component createComponentFromComponentType(ComponentType componentType, String componentName) {
        Component component = af.createComponent();
        component.setName(componentName);

        for (Service service : componentType.getServices()) {
            ComponentService compService = af.createComponentService();
            compService.setService(service);
            component.getServices().add(compService);
        }

        for (Reference reference : componentType.getReferences()) {
            ComponentReference compReference = af.createComponentReference();
            compReference.setReference(reference);
            component.getReferences().add(compReference);
        }

        for (Property property : componentType.getProperties()) {
            ComponentProperty compProperty = af.createComponentProperty();
            compProperty.setProperty(property);
            component.getProperties().add(compProperty);
        }
        return component;
    }

    public Composite createComposite() {
        return af.createComposite();
    }

    public Component createComponent() {
        return af.createComponent();
    }

    public CompositeReference createCompositeReference() {
        return af.createCompositeReference();
    }

    public CompositeService createCompositeService() {
        return af.createCompositeService();
    }
}
