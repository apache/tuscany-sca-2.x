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

package org.apache.tuscany.sca.implementation.spring.metadata;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * A hacking utility to copy beans field by field between two class loaders
 */
public class SpringElementTie {
    public static <T> T copy(Object source, Class<T> cls, Type genericType) {
        if (source == null) {
            return null;
        }
        if (cls.isPrimitive()) {
            return (T)source;
        }
        if (Collection.class.isAssignableFrom(cls)) {
            ParameterizedType pType = (ParameterizedType)genericType;
            Type itemType = pType.getActualTypeArguments()[0];
            Collection col = (Collection)source;
            List target = new ArrayList();
            for (Object item : col) {
                target.add(copy(item, (Class<?>)itemType, itemType));
            }
            return (T)target;
        }
        if (cls.isInstance(source)) {
            return cls.cast(source);
        }
        try {
            Class<?> sourceClass = source.getClass();
            T target = cls.newInstance();
            for (Field sourceField : sourceClass.getDeclaredFields()) {
                sourceField.setAccessible(true);
                Field targetField = cls.getDeclaredField(sourceField.getName());
                targetField.setAccessible(true);
                Object sourceFieldValue = sourceField.get(source);
                Object targetFieldValue = copy(sourceFieldValue, targetField.getType(), targetField.getGenericType());
                targetField.set(target, targetFieldValue);
            }
            return target;
        } catch (Throwable e) {
            throw new IllegalArgumentException(e);
        }
    }

}
