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

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.apache.tuscany.core.injection.JNDIObjectFactory;
import org.apache.tuscany.spi.ObjectFactory;
import org.apache.tuscany.spi.loader.LoaderException;
import org.apache.tuscany.spi.loader.StAXPropertyFactory;
import org.apache.tuscany.spi.model.Property;

/**
 * A StAXPropertyFactory that creates property values by looking them up in the default JNDI InitialContext.
 * <p/>
 * This can be used to locate resources in a J2EE environment and inject them as configuration properties. For example,
 * to access a database a component could write: <code> &at;Property DataSource myDB; </code> and configure with <code>
 * &lt;properties&gt; &lt;v:myDb&gt;java:comp/env/jdbc/MyDatabase&lt;/v:myDB&gt; &lt;/properties&gt; </code>
 *
 * @version $Rev$ $Date$
 */
public class JNDIPropertyFactory implements StAXPropertyFactory {
    public <T> ObjectFactory<T> createObjectFactory(XMLStreamReader reader,
                                                    Property<T> property)
        throws XMLStreamException, LoaderException {
        Class<T> type = property.getJavaType();
        assert type != null : "property type is null";
        String text = reader.getElementText();
        try {
            Context context = new InitialContext();
            return new JNDIObjectFactory<T>(context, text);
        } catch (NamingException e) {
            throw new LoaderException(e);
        }
    }
}
