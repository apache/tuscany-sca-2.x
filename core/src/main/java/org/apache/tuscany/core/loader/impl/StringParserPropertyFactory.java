/**
 *
 * Copyright 2006 The Apache Software Foundation
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.apache.tuscany.core.loader.impl;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Constructor;
import java.beans.PropertyEditorManager;
import java.beans.PropertyEditor;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamException;

import org.apache.tuscany.core.loader.StAXPropertyFactory;
import org.apache.tuscany.core.builder.ObjectFactory;
import org.apache.tuscany.core.injection.SingletonObjectFactory;
import org.apache.tuscany.core.config.ConfigurationLoadException;
import org.apache.tuscany.model.assembly.Property;

/**
 * @version $Rev$ $Date$
 */
public class StringParserPropertyFactory implements StAXPropertyFactory {
    public ObjectFactory<?> createObjectFactory(XMLStreamReader reader, Property property) throws XMLStreamException, ConfigurationLoadException {
        Class<?> type = property.getType();
        assert type != null : "property type is null";
        String text = reader.getElementText();

        // degenerate case where we are returning a String
        if (String.class.equals(type)) {
            return new SingletonObjectFactory(text);
        }

        // special handler to convert hexBinary to a byte[]
        if (byte[].class.equals(type)) {
            byte[] instance = new byte[text.length() >> 1];
            for (int i = 0; i < instance.length; i++) {
                instance[i] = (byte) (Character.digit(text.charAt(i << 1), 16) << 4 | Character.digit(text.charAt((i << 1) + 1), 16));
            }
            return new SingletonObjectFactory(instance);
        }

        // does this type have a static valueOf(String) method?
        try {
            Method valueOf = type.getMethod("valueOf", String.class);
            if (Modifier.isStatic(valueOf.getModifiers())) {
                try {
                    return new SingletonObjectFactory(valueOf.invoke(null, text));
                } catch (IllegalAccessException e) {
                    throw new AssertionError("getMethod returned an inaccessible method");
                } catch (InvocationTargetException e) {
                    // FIXME we should throw something better
                    throw (ConfigurationLoadException) new ConfigurationLoadException(property.getName()).initCause(e.getCause());
                }
            }
        } catch (NoSuchMethodException e) {
            // try something else
        }

        // does this type have a constructor that takes a String?
        try {
            Constructor<?> ctr = type.getConstructor(String.class);
            return new SingletonObjectFactory(ctr.newInstance(text));
        } catch (NoSuchMethodException e) {
            // try something else
        } catch (IllegalAccessException e) {
            throw new AssertionError("getConstructor returned an inaccessible method");
        } catch (InstantiationException e) {
            throw new ConfigurationLoadException("Property type cannot be instantiated: " + type.getName());
        } catch (InvocationTargetException e) {
            // FIXME we should throw something better
            throw (ConfigurationLoadException) new ConfigurationLoadException(property.getName()).initCause(e.getCause());
        }

        // do we have a property editor for it?
        PropertyEditor editor = PropertyEditorManager.findEditor(type);
        if (editor != null) {
            try {
                editor.setAsText(text);
                return new SingletonObjectFactory(editor.getValue());
            } catch (IllegalArgumentException e) {
                // FIXME we should throw something better
                throw (ConfigurationLoadException) new ConfigurationLoadException(property.getName()).initCause(e.getCause());
            }
        }

        // FIXME we should throw something better
        throw new ConfigurationLoadException("Do not have a way to parse a String into a " + type.getName());
    }
}
