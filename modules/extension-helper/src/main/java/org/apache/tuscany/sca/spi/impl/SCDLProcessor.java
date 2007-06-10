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

package org.apache.tuscany.sca.spi.impl;

import static javax.xml.stream.XMLStreamConstants.END_ELEMENT;

import java.lang.reflect.Constructor;
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
import org.apache.tuscany.sca.assembly.Implementation;
import org.apache.tuscany.sca.contribution.service.ContributionReadException;
import org.apache.tuscany.sca.contribution.service.ContributionWriteException;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.core.ModelFactoryExtensionPoint;
import org.apache.tuscany.sca.spi.utils.AbstractStAXArtifactProcessor;
import org.apache.tuscany.sca.spi.utils.DynamicImplementation;

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
                String name = m.getName().substring(3).toLowerCase();
                if (name.endsWith("_")) {
                    name = name.substring(0,name.length()-1);
                }
                attributeSetters.put(name, m);
            }
        }
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

}
