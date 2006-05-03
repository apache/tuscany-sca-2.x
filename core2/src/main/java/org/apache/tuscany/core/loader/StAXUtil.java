/**
 *
 * Copyright 2005 The Apache Software Foundation
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
package org.apache.tuscany.core.loader;

import java.util.HashMap;
import java.util.Map;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.apache.tuscany.model.InteractionScope;
import org.apache.tuscany.model.Multiplicity;
import org.apache.tuscany.spi.loader.MissingResourceException;

/**
 * Utility classes to support StAX-based loaders
 *
 * @version $Rev$ $Date$
 */
public final class StAXUtil {
    private static final Map<String, Multiplicity> MULTIPLICITY = new HashMap<String, Multiplicity>(4);

    static {
        MULTIPLICITY.put("0..1", Multiplicity.ZERO_ONE);
        MULTIPLICITY.put("1..1", Multiplicity.ONE_ONE);
        MULTIPLICITY.put("0..n", Multiplicity.ZERO_N);
        MULTIPLICITY.put("1..n", Multiplicity.ONE_N);
    }

    private StAXUtil() {
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
     * Convert a "multiplicity" attribute to the equivalent enum value.
     *
     * @param multiplicity the attribute to convert
     * @param def          the default value
     * @return the enum equivalent
     */
    public static Multiplicity multiplicity(String multiplicity, Multiplicity def) {
        return multiplicity == null ? def : MULTIPLICITY.get(multiplicity);
    }

    /**
     * Convert a "scope" attribute to the equivalent enum value.
     * Returns CONVERSATIONAL if the value equals (ignoring case) "conversational",
     * otherwise returns NONCONVERSATIONAL.
     *
     * @param scope the attribute to convert
     * @return the enum equivalent
     */
    public static InteractionScope interactionScope(String scope) {
        if ("conversational".equalsIgnoreCase(scope)) {
            return InteractionScope.CONVERSATIONAL;
        } else {
            return InteractionScope.NONCONVERSATIONAL;
        }
    }

    /**
     * Load but do not define the class (to avoid any initializers being fired).
     *
     * @param name the name of the class to load
     * @param cl   the classloader to use to load it
     * @return the class
     * @throws MissingResourceException if the class could not be found
     */
    public static Class<?> loadClass(String name, ClassLoader cl) throws MissingResourceException {
        try {
            return Class.forName(name, false, cl);
        } catch (ClassNotFoundException e) {
            throw new MissingResourceException(name, e);
        }
    }
}
