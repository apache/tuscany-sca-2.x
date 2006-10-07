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
package org.apache.tuscany.core.loader;

import java.beans.PropertyEditor;
import java.beans.PropertyEditorManager;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import javax.xml.stream.XMLStreamException;

import org.apache.tuscany.spi.ObjectFactory;
import org.apache.tuscany.spi.loader.LoaderException;
import org.apache.tuscany.spi.loader.PropertyObjectFactory;
import org.apache.tuscany.spi.model.Property;
import org.apache.tuscany.spi.model.PropertyValue;

import org.apache.tuscany.core.injection.SingletonObjectFactory;

/**
 * Implementation of StAXPropertyFactory that interprets the XML as
 *
 * @version $Rev$ $Date$
 */
public class StringParserPropertyFactory implements PropertyObjectFactory {

    public <T> ObjectFactory<T> createObjectFactory(Property<T> property, PropertyValue<T> value)
        throws LoaderException {
        String text = value.getValue().getDocumentElement().getTextContent();
        return new SingletonObjectFactory<T>(createInstance(text, property.getJavaType()));
    }

    @SuppressWarnings("unchecked")
    public <T> T createInstance(String text, Class<T> type) throws LoaderException {
        // Class<T> type = property.getJavaType();
        assert type != null : "property type is null";

        // degenerate case where property type is a String
        if (String.class.equals(type)) {
            return type.cast(text);
        }

        // special handler to convert hexBinary to a byte[]
        if (byte[].class.equals(type)) {
            byte[] instance = new byte[text.length() >> 1];
            for (int i = 0; i < instance.length; i++) {
                instance[i] =
                    (byte) (Character.digit(text.charAt(i << 1), 16) << 4 | Character.digit(text
                        .charAt((i << 1) + 1), 16));
            }
            return type.cast(instance);
        }

        // does this type have a static valueOf(String) method?
        try {
            Method valueOf = type.getMethod("valueOf", String.class);
            if (Modifier.isStatic(valueOf.getModifiers())) {
                try {
                    return type.cast(valueOf.invoke(null, text));
                } catch (IllegalAccessException e) {
                    throw new AssertionError("getMethod returned an inaccessible method");
                } catch (InvocationTargetException e) {
                    // FIXME we should throw something better
                    throw new LoaderException(e.getCause());
                }
            }
        } catch (NoSuchMethodException e) {
            // try something else
        }

        // does this type have a constructor that takes a String?
        try {
            Constructor<T> ctr = type.getConstructor(String.class);
            return ctr.newInstance(text);
        } catch (NoSuchMethodException e) {
            // try something else
        } catch (IllegalAccessException e) {
            throw new AssertionError("getConstructor returned an inaccessible method");
        } catch (InstantiationException e) {
            throw new LoaderException("Property type cannot be instantiated: " + type.getName());
        } catch (InvocationTargetException e) {
            // FIXME we should throw something better
            throw new LoaderException(e.getCause());
        }

        // do we have a property editor for it?
        PropertyEditor editor = PropertyEditorManager.findEditor(type);
        if (editor != null) {
            try {
                editor.setAsText(text);
                return (T) editor.getValue();
            } catch (IllegalArgumentException e) {
                // FIXME we should throw something better
                throw new LoaderException(e);

            }
        }

        // FIXME we should throw something better
        throw new LoaderException("Do not have a way to parse a String into a " + type.getName());

    }

    @SuppressWarnings("unchecked")
    public <T> ObjectFactory<T> createObjectFactory(String text, Property<T> property)
        throws XMLStreamException, LoaderException {
        Class<T> type = property.getJavaType();
        assert type != null : "property type is null";

        // degenerate case where property type is a String
        if (String.class.equals(type)) {
            return new SingletonObjectFactory<T>(type.cast(text));
        }

        // special handler to convert hexBinary to a byte[]
        if (byte[].class.equals(type)) {
            byte[] instance = new byte[text.length() >> 1];
            for (int i = 0; i < instance.length; i++) {
                instance[i] =
                    (byte) (Character.digit(text.charAt(i << 1), 16) << 4 | Character.digit(text
                        .charAt((i << 1) + 1), 16));
            }
            return new SingletonObjectFactory<T>(type.cast(instance));
        }

        // does this type have a static valueOf(String) method?
        try {
            Method valueOf = type.getMethod("valueOf", String.class);
            if (Modifier.isStatic(valueOf.getModifiers())) {
                try {
                    return new SingletonObjectFactory<T>(type.cast(valueOf.invoke(null, text)));
                } catch (IllegalAccessException e) {
                    throw new AssertionError("getMethod returned an inaccessible method");
                } catch (InvocationTargetException e) {
                    // FIXME we should throw something better
                    throw new LoaderException(e.getCause());
                }
            }
        } catch (NoSuchMethodException e) {
            // try something else
        }

        // does this type have a constructor that takes a String?
        try {
            Constructor<T> ctr = type.getConstructor(String.class);
            return new SingletonObjectFactory<T>(ctr.newInstance(text));
        } catch (NoSuchMethodException e) {
            // try something else
        } catch (IllegalAccessException e) {
            throw new AssertionError("getConstructor returned an inaccessible method");
        } catch (InstantiationException e) {
            throw new LoaderException("Property type cannot be instantiated: " + type.getName());
        } catch (InvocationTargetException e) {
            // FIXME we should throw something better
            throw new LoaderException(e.getCause());
        }

        // do we have a property editor for it?
        PropertyEditor editor = PropertyEditorManager.findEditor(type);
        if (editor != null) {
            try {
                editor.setAsText(text);
                return new SingletonObjectFactory<T>((T) editor.getValue());
            } catch (IllegalArgumentException e) {
                // FIXME we should throw something better
                throw new LoaderException(e);

            }
        }

        // FIXME we should throw something better
        throw new LoaderException("Do not have a way to parse a String into a " + type.getName());
    }
}
