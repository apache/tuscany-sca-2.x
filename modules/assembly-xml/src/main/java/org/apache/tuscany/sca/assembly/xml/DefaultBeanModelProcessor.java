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

package org.apache.tuscany.sca.assembly.xml;

import static javax.xml.stream.XMLStreamConstants.END_ELEMENT;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.apache.tuscany.sca.assembly.AssemblyFactory;
import org.apache.tuscany.sca.assembly.Base;
import org.apache.tuscany.sca.assembly.ComponentType;
import org.apache.tuscany.sca.assembly.Implementation;
import org.apache.tuscany.sca.contribution.processor.StAXArtifactProcessor;
import org.apache.tuscany.sca.contribution.resolver.ModelResolver;
import org.apache.tuscany.sca.contribution.service.ContributionReadException;
import org.apache.tuscany.sca.contribution.service.ContributionResolveException;
import org.apache.tuscany.sca.contribution.service.ContributionWriteException;
import org.apache.tuscany.sca.policy.IntentAttachPoint;
import org.apache.tuscany.sca.policy.PolicyFactory;
import org.apache.tuscany.sca.policy.PolicySetAttachPoint;

public class DefaultBeanModelProcessor extends BaseArtifactProcessor implements StAXArtifactProcessor {

    private QName artifactType;
    private Class<Implementation> modelClass;
    private Object modelFactory;
    private Method factoryMethod;
    private Map<String, Method> setterMethods = new HashMap<String, Method>();
    private Map<String, Method> getterMethods = new HashMap<String, Method>();

    public DefaultBeanModelProcessor(AssemblyFactory assemblyFactory,
                                       PolicyFactory policyFactory,
                                       QName artifactType,
                                       Class<Implementation> modelClass,
                                       Object modelFactory) {
        super(assemblyFactory, policyFactory, null);
        this.artifactType = artifactType;
        this.modelClass = modelClass;
        this.modelFactory = modelFactory;
        
        // Introspect the factory class and bean model class
        if (modelFactory != null) {
            
            // Find the model create method
            for (Method method: modelFactory.getClass().getMethods()) {
                if (method.getName().startsWith("create") && method.getReturnType() == modelClass) {
                    factoryMethod = method;
                    break;
                }
            }
        }
        
        // Index the bean's setter methods
        for (Method method: modelClass.getMethods()) {
            Method getter;
            String name = method.getName();
            if (name.startsWith("set") && name.length() > 3) {
                
                // Get the corresponding getter method
                try {
                    getter = modelClass.getMethod("get" + name.substring(3));
                } catch (Exception e) {
                    getter = null;
                }
                
                // Get the property name
                name = name.substring(3);
                if (name.length() > 1) {
                    if (!name.toUpperCase().equals(name)) {
                        name = name.substring(0, 1).toLowerCase() + name.substring(1);
                    }
                }
            } else {
                continue;
            }
            
            // Map an uppercase property name to a lowercase attribute name 
            if (name.toUpperCase().equals(name)) {
                name = name.toLowerCase();
            }
            
            // Trim trailing _ from property names
            if (name.endsWith("_")) {
                name = name.substring(0, name.length()-1);
            }
            setterMethods.put(name, method);
            getterMethods.put(name, getter);
        }
    }

    public Object read(XMLStreamReader reader) throws ContributionReadException {

        try {

            // Read an element
            
            // Create a new instance of the model
            Object model;
            if (modelFactory != null) {
                // Invoke the factory create method
                model = factoryMethod.invoke(modelFactory);
            } else {
                // Invoke the model bean class default constructor
                model = modelClass.newInstance();
            }

            // Initialize the bean properties with the attributes found in the
            // XML element
            for (int i = 0, n = reader.getAttributeCount(); i < n; i++) {
                String attributeName = reader.getAttributeLocalName(i);
                Method setter = setterMethods.get(attributeName);
                if (setter != null) {
                    String value = reader.getAttributeValue(i);
                    setter.invoke(model, value);
                }
            }

            // Read policies
            if (model instanceof PolicySetAttachPoint) {
                readPolicies((PolicySetAttachPoint)model, reader);
            } else if (model instanceof IntentAttachPoint) {
                readIntents((IntentAttachPoint)model, reader);
            }

            // TODO read extension elements
            
            // By default mark the model object unresolved
            if (model instanceof Base) {
                ((Base)model).setUnresolved(true);
            }
            
            // Skip to end element
            while (reader.hasNext()) {
                if (reader.next() == END_ELEMENT && artifactType.equals(reader.getName())) {
                    break;
                }
            }
            return model;

        } catch (Exception e) {
            throw new ContributionReadException(e);
        }
    }

    public void write(Object bean, XMLStreamWriter writer) throws ContributionWriteException {
        try {
            // Write an <bean>
            writer.writeStartElement(artifactType.getNamespaceURI(), artifactType.getLocalPart());

            // Write the bean properties as attributes
            for (Map.Entry<String, Method> entry: getterMethods.entrySet()) {
                if (entry.getValue().getReturnType() == String.class) {
                    String value = (String)entry.getValue().invoke(bean);
                    writer.writeAttribute(entry.getKey(), value);
                }
            }
            
            writer.writeEndElement();

        } catch (Exception e) {
            throw new ContributionWriteException(e);
        }
    }

    public void resolve(Object bean, ModelResolver resolver) throws ContributionResolveException {
        
        // Resolve and merge the component type associated with an
        // implementation model
        if (bean instanceof Implementation) {
            Implementation implementation = (Implementation)bean;
            String uri = implementation.getURI();
            if (uri != null) {
                int d = uri.lastIndexOf('.');
                if (d != -1) {
                    uri = uri.substring(0, d) + ".componentType";
                    
                    // Resolve the component type
                    ComponentType componentType = assemblyFactory.createComponentType();
                    componentType.setURI(uri);
                    componentType.setUnresolved(true);
                    
                    componentType = resolver.resolveModel(ComponentType.class, componentType);
                    if (componentType != null && !componentType.isUnresolved()) {
                        
                        // We found a component type, merge it into the implementation model
                        implementation.getServices().addAll(componentType.getServices());
                        implementation.getReferences().addAll(componentType.getReferences());
                        implementation.getProperties().addAll(componentType.getProperties());
                        implementation.setConstrainingType(componentType.getConstrainingType());
                        
                        if (implementation instanceof PolicySetAttachPoint &&
                            componentType instanceof PolicySetAttachPoint )
                        {
                            PolicySetAttachPoint policiedImpl = (PolicySetAttachPoint)implementation;
                            PolicySetAttachPoint policiedCompType = (PolicySetAttachPoint)componentType;
                            
                            if ( policiedImpl.getPolicySets() != null) {
                                policiedImpl.getPolicySets().addAll(policiedCompType.getPolicySets());
                            }
                            if (policiedImpl.getRequiredIntents() != null) {
                                policiedImpl.getRequiredIntents().addAll(policiedCompType.getRequiredIntents());
                            }
                        }
                    }
                }
            }
        }
        
        // Mark the model resolved
        if (bean instanceof Base) {
            ((Base)bean).setUnresolved(false);
        }
    }

    public QName getArtifactType() {
        return artifactType;
    }

    public Class<?> getModelType() {
        return modelClass;
    }

}
