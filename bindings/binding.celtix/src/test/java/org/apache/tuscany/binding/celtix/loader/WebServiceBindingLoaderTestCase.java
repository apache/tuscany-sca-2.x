/**
 *
 *  Copyright 2005 The Apache Software Foundation or its licensors, as applicable.
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
package org.apache.tuscany.binding.celtix.loader;

import javax.xml.stream.XMLStreamReader;

import junit.framework.TestCase;

import org.apache.tuscany.core.loader.LoaderContext;
import org.apache.tuscany.core.loader.StAXLoaderRegistry;
import org.apache.tuscany.model.assembly.AssemblyContext;
import org.easymock.EasyMock;

public class WebServiceBindingLoaderTestCase extends TestCase {

    @SuppressWarnings("deprecation")
    public void testLoad() throws Exception {
        
        WebServiceBindingLoader loader = new WebServiceBindingLoader();
        StAXLoaderRegistry reg = EasyMock.createNiceMock(StAXLoaderRegistry.class);
        reg.getContext();
        EasyMock.expectLastCall().andReturn(EasyMock.createNiceMock(AssemblyContext.class));
        EasyMock.replay(reg);
            
        loader.setRegistry(reg);
        
        XMLStreamReader reader = EasyMock.createNiceMock(XMLStreamReader.class);
        reader.getAttributeValue(null, "uri");
        EasyMock.expectLastCall().andReturn("http://objectweb.org/hello_world_soap_http");
        reader.getAttributeValue(null, "port");
        EasyMock.expectLastCall().andReturn("SoapPort");
        EasyMock.replay(reader);
        
        LoaderContext loaderContext = new LoaderContext(null);
        
        assertNotNull("Did not load binding", loader.load(reader, loaderContext));
        
    }

}
