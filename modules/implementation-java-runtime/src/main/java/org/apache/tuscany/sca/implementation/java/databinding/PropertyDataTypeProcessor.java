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

package org.apache.tuscany.sca.implementation.java.databinding;

import org.apache.tuscany.sca.assembly.Property;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.core.UtilityExtensionPoint;
import org.apache.tuscany.sca.databinding.Mediator;
import org.apache.tuscany.sca.implementation.java.IntrospectionException;
import org.apache.tuscany.sca.implementation.java.JavaElementImpl;
import org.apache.tuscany.sca.implementation.java.JavaImplementation;
import org.apache.tuscany.sca.implementation.java.introspect.BaseJavaClassVisitor;
import org.apache.tuscany.sca.implementation.java.introspect.JavaIntrospectionHelper;
import org.apache.tuscany.sca.interfacedef.DataType;
import org.apache.tuscany.sca.interfacedef.util.JavaXMLMapper;
import org.apache.tuscany.sca.interfacedef.util.XMLType;

/**
 * 
 */
public class PropertyDataTypeProcessor extends BaseJavaClassVisitor {
    private Mediator mediator;

    /**
     * @param registry
     */
    public PropertyDataTypeProcessor(ExtensionPointRegistry registry) {
        super(registry);
        UtilityExtensionPoint utilityExtensionPoint = registry.getExtensionPoint(UtilityExtensionPoint.class);
        this.mediator = utilityExtensionPoint.getUtility(Mediator.class);
    }

    /**
     * Introspect the property 
     * @param javaElement
     * @return
     */
    private void introspect(Property property, JavaElementImpl javaElement) {
        // XMLType xmlType = new XMLType(property.getXSDElement(), property.getXSDType());
        // property.getDataType().setLogical(xmlType);
        mediator.getDataBindings().introspectType(property.getDataType(), null);
    }

    @Override
    public <T> void visitEnd(Class<T> clazz, JavaImplementation type) throws IntrospectionException {
        for (Property property : type.getProperties()) {
            String name = property.getName();
            JavaElementImpl element = type.getPropertyMembers().get(name);
            introspect(property, element);
            DataType dt = property.getDataType();
            if (dt.getLogical() instanceof XMLType) {
                XMLType xmlType = (XMLType)dt.getLogical();
                property.setXSDType(xmlType.getTypeName());
                property.setXSDElement(xmlType.getElementName());
            } else {
                Class<?> baseType = JavaIntrospectionHelper.getBaseType(element.getType(), element.getGenericType());
                property.setXSDType(JavaXMLMapper.getXMLType(baseType));
            }
        }
        super.visitEnd(clazz, type);
    }

}
