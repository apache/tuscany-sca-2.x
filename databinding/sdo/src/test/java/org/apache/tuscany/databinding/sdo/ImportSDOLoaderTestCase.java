/**
 *
 * Copyright 2006 The Apache Software Foundation or its licensors as applicable
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
package org.apache.tuscany.databinding.sdo;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.apache.tuscany.core.config.ConfigurationLoadException;

/**
 * @version $Rev$ $Date$
 */
public class ImportSDOLoaderTestCase extends LoaderTestSupport {
    private ImportSDOLoader loader;

    public void testMinimal() throws XMLStreamException, ConfigurationLoadException {
        String xml = "<import.sdo xmlns='http://www.osoa.org/xmlns/sca/0.9'/>";
        XMLStreamReader reader = getReader(xml);
        assertNull(loader.load(reader, null));
    }

    public void testFactory() throws XMLStreamException, ConfigurationLoadException {
        String xml = "<import.sdo xmlns='http://www.osoa.org/xmlns/sca/0.9' factory='org.apache.tuscany.databinding.sdo.ImportSDOLoaderTestCase$MockFactory'/>";
        XMLStreamReader reader = getReader(xml);
        assertFalse(inited);
        assertNull(loader.load(reader, loaderContext));
        assertTrue(inited);
    }

    protected void setUp() throws Exception {
        super.setUp();
        loader = new ImportSDOLoader();
    }

    private static boolean inited = false;

    public static class MockFactory {
        public static Object INSTANCE;
        
        static {
            ImportSDOLoaderTestCase.inited = true;
        }
    }
}

