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

import java.lang.reflect.Type;
import java.util.Hashtable;
import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.spi.InitialContextFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import org.apache.tuscany.spi.model.PropertyValue;

import junit.framework.TestCase;
import org.apache.tuscany.core.injection.JNDIObjectFactory;
import org.easymock.EasyMock;

/**
 * @version $Rev$ $Date$
 */
public class JNDIPropertyFactoryTestCase extends TestCase {

    public void testCreate() throws Exception {
        String old = System.getProperty(Context.INITIAL_CONTEXT_FACTORY);
        try {
            System.setProperty(Context.INITIAL_CONTEXT_FACTORY, MockInitialContextFactory.class.getName());
            JNDIPropertyFactory factory = new JNDIPropertyFactory();
            Element element = EasyMock.createMock(Element.class);
            EasyMock.expect(element.getTextContent()).andReturn("foo");
            EasyMock.replay(element);
            Document doc = EasyMock.createMock(Document.class);
            EasyMock.expect(doc.getDocumentElement()).andReturn(element);
            EasyMock.replay(doc);
            PropertyValue<?> value = new MockPropertyValue<Type>();
            value.setValue(doc);
            JNDIObjectFactory<?> jndiFactory = (JNDIObjectFactory<?>) factory.createObjectFactory(null, value);
            assertEquals("bar", jndiFactory.getInstance());
        } finally {
            System.clearProperty(Context.INITIAL_CONTEXT_FACTORY);
            if (old != null) {
                System.setProperty(Context.INITIAL_CONTEXT_FACTORY, old);
            }
        }

    }

    private class MockPropertyValue<T> extends PropertyValue<T> {

    }

    public static class MockInitialContextFactory implements InitialContextFactory {
        public MockInitialContextFactory() {
        }

        public Context getInitialContext(Hashtable<?, ?> environment) throws NamingException {
            Context context = EasyMock.createMock(Context.class);
            EasyMock.expect(context.lookup("foo")).andReturn("bar");
            EasyMock.replay(context);
            return context;
        }
    }

}
