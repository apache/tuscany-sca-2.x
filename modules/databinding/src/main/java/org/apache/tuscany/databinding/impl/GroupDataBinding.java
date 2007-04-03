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

package org.apache.tuscany.databinding.impl;

import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import org.apache.tuscany.spi.databinding.extension.DataBindingExtension;
import org.apache.tuscany.spi.model.DataType;

/**
 * The base class for a special databinding which represents a group of other databindings
 * 
 * @version $Rev$ $Date$
 */
public abstract class GroupDataBinding extends DataBindingExtension {
    public static final String NAME = "databinding:group";

    /**
     * Marker type is a java class or interface representing the data format. 
     */
    protected Class[] markerTypes;

    public GroupDataBinding(Class[] types) {
        super(NAME, null, GroupDataBinding.class);
        this.markerTypes = types;
    }

    @SuppressWarnings("unchecked")
    public boolean introspect(DataType type, Annotation[] annotations) {
        if (markerTypes == null) {
            return false;
        }
        Type physical = type.getPhysical();
        if (physical instanceof ParameterizedType) {
            physical = ((ParameterizedType)physical).getRawType();
        }
        if (!(physical instanceof Class)) {
            return false;
        }
        Class cls = (Class)physical;
        for (Class<?> c : markerTypes) {
            if (isTypeOf(c, cls)) {
                type.setDataBinding(getDataBinding(c));
                type.setLogical(getLogical(cls, annotations));
                return true;
            }
        }
        return false;
    }
    
    /**
     * Test if the given type is a subtype of the base type
     * @param markerType
     * @param type
     * @return
     */
    protected boolean isTypeOf(Class<?> markerType, Class<?> type) {
        return markerType.isAssignableFrom(type);
    }
    
    /**
     * Derive the databinding name from a base class
     * @param baseType
     * @return
     */
    protected String getDataBinding(Class<?> baseType) {
        return baseType.getName();
    }
    
    /**
     * Get the logical type
     * @param type The java type
     * @param annotations
     * @return
     */
    protected abstract Object getLogical(Class<?> type, Annotation[] annotations);

}
