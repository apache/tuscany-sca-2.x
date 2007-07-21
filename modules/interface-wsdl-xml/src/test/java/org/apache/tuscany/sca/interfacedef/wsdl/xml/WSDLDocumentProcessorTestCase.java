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

package org.apache.tuscany.sca.interfacedef.wsdl.xml;

import java.net.URI;
import java.net.URL;
import java.util.HashMap;
import java.util.List;

import javax.wsdl.Import;

import junit.framework.Assert;

import org.apache.tuscany.sca.contribution.resolver.ModelResolver;
import org.apache.tuscany.sca.interfacedef.wsdl.DefaultWSDLFactory;
import org.apache.tuscany.sca.interfacedef.wsdl.WSDLDefinition;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @version $Rev$ $Date$
 */
public class WSDLDocumentProcessorTestCase {
    private WSDLDocumentProcessor processor;

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        processor = new WSDLDocumentProcessor(new DefaultWSDLFactory(), null);
    }

    /**
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testWSDL() throws Exception {
        URL url = getClass().getResource("/wsdl/helloworld-service.wsdl");
        WSDLDefinition definition = processor.read(null, URI.create("wsdl/helloworld-service.wsdl"), url);
        Assert.assertNotNull(definition);
        URL url1 = getClass().getResource("/wsdl/helloworld-interface.wsdl");
        WSDLDefinition definition1 = processor.read(null, URI.create("wsdl/helloworld-interface.wsdl"), url1);
        ModelResolver resolver = new MockResolver();
        resolver.addModel(definition);
        resolver.addModel(definition1);
        processor.resolve(definition, resolver);
        List imports = (List)definition.getDefinition().getImports().get("http://helloworld");
        Assert.assertNotNull(imports);
        Assert.assertNotNull(((Import)imports.get(0)).getDefinition());
    }

    private static class MockResolver extends HashMap implements ModelResolver {

        /**
         * @see org.apache.tuscany.sca.contribution.resolver.ModelResolver#addModel(java.lang.Object)
         */
        public void addModel(Object resolved) {
            super.put(resolved, resolved);
        }

        /**
         * @see org.apache.tuscany.sca.contribution.resolver.ModelResolver#removeModel(java.lang.Object)
         */
        public Object removeModel(Object resolved) {
            return super.remove(resolved);
        }

        /**
         * @see org.apache.tuscany.sca.contribution.resolver.ModelResolver#resolveModel(java.lang.Class,
         *      java.lang.Object)
         */
        public <T> T resolveModel(Class<T> modelClass, T unresolved) {
            return (T)super.get(unresolved);
        }

    }

}
