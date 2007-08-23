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

package org.apache.tuscany.sca.extension.helper.impl;

import static javax.xml.stream.XMLStreamConstants.END_ELEMENT;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.apache.tuscany.sca.assembly.AssemblyFactory;
import org.apache.tuscany.sca.assembly.ComponentType;
import org.apache.tuscany.sca.assembly.Implementation;
import org.apache.tuscany.sca.assembly.Property;
import org.apache.tuscany.sca.assembly.Reference;
import org.apache.tuscany.sca.assembly.Service;
import org.apache.tuscany.sca.contribution.ModelFactoryExtensionPoint;
import org.apache.tuscany.sca.contribution.resolver.ModelResolver;
import org.apache.tuscany.sca.contribution.service.ContributionReadException;
import org.apache.tuscany.sca.contribution.service.ContributionWriteException;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.extension.helper.utils.AbstractStAXArtifactProcessor;
import org.apache.tuscany.sca.extension.helper.utils.DynamicImplementation;
import org.osoa.sca.ServiceRuntimeException;

/**
 * An SCDL ArtifactProcessor which uses the Implementation class getters/setters
 * to define the SCDL attributes.
 */
public class SCDLProcessor extends AbstractStAXArtifactProcessor<Implementation> {

    protected QName scdlQName;
    protected Class<Implementation> implementationClass;
    protected ExtensionPointRegistry registry;
    protected ModelFactoryExtensionPoint factories;

    protected Map<String, Method> attributeSetters;
    protected Method elementTextSetter;

    public SCDLProcessor(AssemblyFactory assemblyFactory, QName scdlQName, Class<Implementation> implementationClass, ExtensionPointRegistry registry, ModelFactoryExtensionPoint factories) {
        super(assemblyFactory);
        this.scdlQName = scdlQName;
        this.implementationClass = implementationClass;
        this.registry = registry;
        this.factories = factories;
        initAttributes();
    }

    protected void initAttributes() {
        attributeSetters = new HashMap<String, Method>();
        Set<Method> methods = new HashSet<Method>(Arrays.asList(implementationClass.getMethods()));
        methods.removeAll(Arrays.asList(DynamicImplementation.class.getMethods()));
        for (Method m : methods) {
            if ("setElementText".equals(m.getName())) {
                elementTextSetter = m;
            } else if ((m.getName().startsWith("set"))) {
                attributeSetters.put(getFieldName(m), m);
            }
        }
    }

    /**
     * Remove get/set from method name, set 1st char to lowercase and
     * remove any trailing underscore character
     */
    protected String getFieldName(Method m) {
        StringBuilder sb = new StringBuilder(m.getName().substring(3));
        sb.setCharAt(0, Character.toLowerCase(sb.charAt(0)));
        String name = sb.toString();
        if (name.endsWith("_")) {
            name = name.substring(0,name.length()-1);
        }
        return name;
    }

    private Object[] getImplConstrArgs() {
        Constructor[] cs = implementationClass.getConstructors();
        if (cs.length != 1) {
            throw new IllegalArgumentException("Implementation class must have a single constructor: "+ implementationClass.getName());
        }
        List args = new ArrayList();
        for (Class c : cs[0].getParameterTypes()) {
            Object o = factories.getFactory(c);
            if (o == null) {
                o = registry.getExtensionPoint(c);
            }
            args.add(o);
        }
        return args.toArray();
    }

    
    public QName getArtifactType() {
        return scdlQName;
    }

    public Class<Implementation> getModelType() {
        Class clazz;
        if (Implementation.class.isAssignableFrom(implementationClass)) {
            clazz = implementationClass;
        } else {
            clazz = PojoImplementation.class;
        }
        return clazz;
    }

    public Implementation read(XMLStreamReader reader) throws ContributionReadException, XMLStreamException {
        Object impl;
        try {
            impl = implementationClass.getConstructors()[0].newInstance(getImplConstrArgs());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        for (String attribute : attributeSetters.keySet()) {
            String value = reader.getAttributeValue(null, attribute);
            if (value != null && value.length() > 0) {
                try {
                    attributeSetters.get(attribute).invoke(impl, new Object[] {value});
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }

        if (elementTextSetter != null) {
            try {
                String value = reader.getElementText();
                if (value != null && value.length() > 0) {
                    elementTextSetter.invoke(impl, value);
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        while (!(reader.getEventType() == END_ELEMENT && scdlQName.equals(reader.getName())) && reader.hasNext()) {
            reader.next();
        }
        
        if (!(impl instanceof Implementation)) {
            impl = new PojoImplementation(impl);
        }

        return (Implementation)impl;
    }

    public void write(Implementation arg0, XMLStreamWriter arg1) throws ContributionWriteException, XMLStreamException {
    }

    @Override
    protected void addSideFileComponentType(String name, Implementation impl, ModelResolver resolver) {
//    protected void addSideFileComponentType(Implementation impl, ModelResolver resolver) {

        ComponentType componentType;
        try {
            componentType = getComponentType(resolver, impl);
        } catch (Exception e) {
            throw new ServiceRuntimeException(e);
        }

        if (componentType != null && !componentType.isUnresolved()) {
            for (Reference reference : componentType.getReferences()) {
                impl.getReferences().add(reference);
            }
            for (Service service : componentType.getServices()) {
                impl.getServices().add(service);
            }
            for (Property property : componentType.getProperties()) {
                impl.getProperties().add(property);
            }
            if (componentType.getConstrainingType() != null) {
                impl.setConstrainingType(componentType.getConstrainingType());
            }
        }
    }

    ComponentType getComponentType(ModelResolver resolver, Implementation impl) throws IllegalArgumentException, IllegalAccessException,
            InvocationTargetException {
        for (Method m : getGetters()) {
            Object io;
            if (impl instanceof PojoImplementation) {
                io = ((PojoImplementation) impl).getUserImpl();
            } else {
                io = impl;
            }
            String value = (String) m.invoke(io, new Object[] {});
            if (value != null) {
                value = value.substring(0, value.lastIndexOf('.'));
                ComponentType componentType = assemblyFactory.createComponentType();
                componentType.setUnresolved(true);
                componentType.setURI(value + ".componentType");
                componentType = resolver.resolveModel(ComponentType.class, componentType);
                if (!componentType.isUnresolved()) {
                    return componentType;
                }
            }
        }
        return null;
    }

    private List<Method> getGetters() {
        List<Method> ms = new ArrayList<Method>();
        for (Method setter : attributeSetters.values()) {
            String s = getFieldName(setter);
            for (Method m : implementationClass.getMethods()) {
                String name = m.getName();
                if (name.length() > 3 && name.startsWith("get")) {
                    if (s.endsWith(name.substring(4))) {
                        ms.add(m);
                    }
                }
            }
        }
        return ms;
    }
}
