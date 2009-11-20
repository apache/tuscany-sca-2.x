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

package org.apache.tuscany.sca.wicket;

import java.lang.reflect.Field;

import org.apache.tuscany.sca.implementation.web.runtime.utils.ContextHelper;
import org.apache.tuscany.sca.runtime.RuntimeComponent;
import org.apache.wicket.injection.IFieldValueFactory;
import org.oasisopen.sca.annotation.ComponentName;
import org.oasisopen.sca.annotation.Context;
import org.oasisopen.sca.annotation.Property;
import org.oasisopen.sca.annotation.Reference;

public class TuscanyFieldValueFactory implements IFieldValueFactory {

    private IContextLocator contextLocator;
    
    public TuscanyFieldValueFactory(IContextLocator contextLocator) {
        this.contextLocator = contextLocator;
    }

    public Object getFieldValue(Field field, Object instance) {
        Object value = null;
        if (field.isAnnotationPresent(Reference.class)) {
            Reference ref = field.getAnnotation(Reference.class);
            String name = ref.name() != null && !ref.name().equals("") ? ref.name() : field.getName();
            value = ContextHelper.getReference(name, field.getType(), contextLocator.getServletContext());
        } else if (field.isAnnotationPresent(Property.class)) {
            Property prop = field.getAnnotation(Property.class);
            String name = prop.name() != null && !prop.name().equals("") ? prop.name() : field.getName();
            value = ContextHelper.getProperty(name, contextLocator.getServletContext());
        } else if (field.isAnnotationPresent(ComponentName.class)) {
            RuntimeComponent rc = (RuntimeComponent)contextLocator.getServletContext().getAttribute(ContextHelper.COMPONENT_ATTR);
            value = rc.getName();
        } else if (field.isAnnotationPresent(Context.class)) {
            value = ContextHelper.getComponentContext(contextLocator.getServletContext());
        }
        return value;
    }

    public boolean supportsField(Field field) {
        return field.isAnnotationPresent(Reference.class) 
            || field.isAnnotationPresent(Property.class)
            || field.isAnnotationPresent(Context.class)
            || field.isAnnotationPresent(ComponentName.class);
    }

}
