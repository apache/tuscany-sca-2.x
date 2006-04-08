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

import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamException;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.tuscany.core.loader.StAXPropertyFactory;
import org.apache.tuscany.core.builder.ObjectFactory;
import org.apache.tuscany.core.config.ConfigurationLoadException;
import org.apache.tuscany.core.injection.JNDIObjectFactory;
import org.apache.tuscany.model.assembly.Property;

/**
 * A StAXPropertyFactory that creates property values by looking them
 * up in the default JNDI InitialContext.
 *
 * This can be used to locate resources in a J2EE environment and inject
 * them as configuration properties. For example, to access a database
 * a component could write:
 * <code>
 *     &at;Property DataSource myDB;
 * </code>
 * and configure with
 * <code>
 *     &lt;properties&gt;
 *       &lt;v:myDb&gt;java:comp/env/jdbc/MyDatabase&lt;/v:myDB&gt;
 *     &lt;/properties&gt;
 * </code>
 *
 * @version $Rev$ $Date$
 */
public class JNDIPropertyFactory implements StAXPropertyFactory {
    public ObjectFactory<?> createObjectFactory(XMLStreamReader reader, Property property) throws XMLStreamException, ConfigurationLoadException {
        Class<?> type = property.getType();
        assert type != null : "property type is null";
        String text = reader.getElementText();
        try {
            Context context = new InitialContext();
            return new JNDIObjectFactory(context, text);
        } catch (NamingException e) {
            throw (ConfigurationLoadException) new ConfigurationLoadException(e.getMessage()).initCause(e);
        }
    }
}
