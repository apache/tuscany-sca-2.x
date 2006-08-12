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
package org.apache.tuscany.spi.loader;

import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamConstants;

/**
 * Utility functions to support loader implementations.
 *
 * @version $Rev$ $Date$
 */
public final class LoaderUtil {
    private LoaderUtil() {
    }

    /**
     * Advance the stream to the next END_ELEMENT event skipping any nested content.
     *
     * @param reader the reader to advance
     * @throws XMLStreamException if there was a problem reading the stream
     */
    public static void skipToEndElement(XMLStreamReader reader) throws XMLStreamException {
        int depth = 0;
        while (true) {
            int event = reader.next();
            if (event == XMLStreamConstants.START_ELEMENT) {
                depth++;
            } else if (event == XMLStreamConstants.END_ELEMENT) {
                if (depth == 0) {
                    return;
                }
                depth--;
            }
        }
    }

    /**
     * Load the class using the supplied ClassLoader.
     * The class will be defined so any initializers present will be fired.
     * As the class is being loaded, the Thread context ClassLoader will be
     * set to the supplied classloader.
     *
     * @param name the name of the class to load
     * @param cl   the classloader to use to load it
     * @return the class
     * @throws MissingResourceException if the class could not be found
     */
    public static Class<?> loadClass(String name, ClassLoader cl) throws MissingResourceException {
        final Thread thread = Thread.currentThread();
        final ClassLoader oldCL = thread.getContextClassLoader();
        try {
            thread.setContextClassLoader(cl);
            return Class.forName(name, true, cl);
        } catch (ClassNotFoundException e) {
            throw new MissingResourceException(name, e);
        } finally {
            thread.setContextClassLoader(oldCL);
        }
    }
}
