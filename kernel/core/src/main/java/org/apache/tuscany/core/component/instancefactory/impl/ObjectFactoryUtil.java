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
package org.apache.tuscany.core.component.instancefactory.impl;

import java.beans.PropertyEditor;
import java.beans.PropertyEditorManager;

import org.apache.tuscany.core.injection.SingletonObjectFactory;
import org.apache.tuscany.spi.ObjectFactory;

/**
 * Utility class for object factory.
 * 
 * @version $Revision$ $Date$
 */
public class ObjectFactoryUtil {
    
    private ObjectFactoryUtil() {
    }
    
    /**
     * Create an object factory for the type.
     * 
     * @param <T>
     * @param text Property text.
     * @param type Property type.
     * @return Object factory.
     */
    @SuppressWarnings("unchecked")
    public static <T> ObjectFactory<T> create(String text, Class<T> type) {
        
        T value = null;
        if (String.class.equals(type)) {
            value = type.cast(text);
        }
        
        PropertyEditor editor = PropertyEditorManager.findEditor(type);
        if(editor != null) {
            editor.setAsText(text);
            value  = (T) editor.getValue();
        }
        return new SingletonObjectFactory<T>(value);
        
    }

}
