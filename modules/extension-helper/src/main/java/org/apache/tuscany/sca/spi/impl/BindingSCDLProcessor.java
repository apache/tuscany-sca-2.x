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

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.apache.tuscany.sca.assembly.Binding;
import org.apache.tuscany.sca.contribution.processor.StAXArtifactProcessor;
import org.apache.tuscany.sca.contribution.resolver.ModelResolver;
import org.apache.tuscany.sca.contribution.service.ContributionReadException;
import org.apache.tuscany.sca.contribution.service.ContributionResolveException;
import org.apache.tuscany.sca.contribution.service.ContributionWriteException;
import org.apache.tuscany.sca.spi.utils.AbstractBinding;

/**
 * An SCDL ArtifactProcessor which uses the Binding class getters/setters
 * to define the SCDL attributes.
 * 
 * TODO: merge this with SCDLProcessor
 */
public class BindingSCDLProcessor implements StAXArtifactProcessor {

    protected QName scdlQName;
    protected Class<Binding> bindingClass;

    protected Map<String, Method> attributeSetters;
    protected Method elementTextSetter;

    public BindingSCDLProcessor(QName scdlQName, Class<Binding> implementationClass) {
        this.scdlQName = scdlQName;
        this.bindingClass = implementationClass;
        initAttributes();
    }

    protected void initAttributes() {
        attributeSetters = new HashMap<String, Method>();
        Set<Method> methods = new HashSet<Method>(Arrays.asList(bindingClass.getMethods()));
        methods.removeAll(Arrays.asList(AbstractBinding.class.getMethods()));
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

    public QName getArtifactType() {
        return scdlQName;
    }

    public Class<Binding> getModelType() {
        return bindingClass;
    }

    public Binding read(XMLStreamReader reader) throws ContributionReadException, XMLStreamException {
        Object impl;
        try {
            impl = bindingClass.newInstance();
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

        if (!(impl instanceof Binding)) {
            impl = new PojoBinding(impl);
        }
        return (Binding)impl;
    }

    public void resolve(Object model, ModelResolver resolver) throws ContributionResolveException {
    }

    public void write(Object model, XMLStreamWriter outputSource) throws ContributionWriteException, XMLStreamException {
    }

}
