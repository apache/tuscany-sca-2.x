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
package org.apache.tuscany.core.implementation.processor;

import java.lang.reflect.Constructor;

import javax.xml.namespace.QName;

import org.apache.tuscany.spi.annotation.Autowire;
import org.apache.tuscany.spi.component.CompositeComponent;
import org.apache.tuscany.spi.databinding.extension.SimpleTypeMapperExtension;
import org.apache.tuscany.spi.deployer.DeploymentContext;
import org.apache.tuscany.spi.idl.TypeInfo;
import org.apache.tuscany.spi.implementation.java.AbstractPropertyProcessor;
import org.apache.tuscany.spi.implementation.java.ImplementationProcessorService;
import org.apache.tuscany.spi.implementation.java.JavaMappedProperty;
import org.apache.tuscany.spi.implementation.java.JavaMappedReference;
import org.apache.tuscany.spi.implementation.java.JavaMappedService;
import org.apache.tuscany.spi.implementation.java.PojoComponentType;
import org.apache.tuscany.spi.implementation.java.ProcessingException;
import org.apache.tuscany.spi.model.OverrideOptions;
import org.osoa.sca.annotations.Property;

/**
 * Processes an {@link @Property} annotation, updating the component type with corresponding {@link JavaMappedProperty}
 *
 * @version $Rev$ $Date$
 */
public class PropertyProcessor extends AbstractPropertyProcessor<Property> {
    private SimpleTypeMapperExtension typeMapper = new SimpleTypeMapperExtension();
    
    public PropertyProcessor(@Autowire ImplementationProcessorService service) {
        super(Property.class, service);
    }

    protected String getName(Property annotation) {
        return annotation.name();
    }

    protected <T> void initProperty(JavaMappedProperty<T> property,
                                    Property annotation,
                                    CompositeComponent parent,
                                    DeploymentContext context) {
        property.setOverride(OverrideOptions.valueOf(annotation.override().toUpperCase()));
        String xmlType = annotation.xmlType();
        if (xmlType != null && xmlType.length() != 0) {
            property.setXmlType(QName.valueOf(annotation.xmlType()));
        } else {
            TypeInfo type = typeMapper.getXMLType(property.getJavaType());
            if (type != null) {
                property.setXmlType(type.getQName());
            }
        }
    }

    public <T> void visitConstructor(CompositeComponent parent, Constructor<T> constructor,
                                     PojoComponentType<JavaMappedService, JavaMappedReference,
                                         JavaMappedProperty<?>> type,
                                     DeploymentContext context) throws ProcessingException {
        // override since heuristic pojo processor evalautes properties
    }
}
