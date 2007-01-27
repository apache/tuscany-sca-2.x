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
package org.apache.tuscany.binding.jsonrpc;

import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.replay;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import junit.framework.TestCase;

import org.apache.tuscany.spi.component.CompositeComponent;
import org.apache.tuscany.spi.deployer.DeploymentContext;
import org.apache.tuscany.spi.loader.LoaderException;
import org.apache.tuscany.spi.loader.LoaderRegistry;
import org.apache.tuscany.spi.model.ModelObject;

public class JSONRPCBindingLoaderTestCase extends TestCase {

    public void testJSONRPCBindingLoader() {
        LoaderRegistry mockRegistry = createMock(LoaderRegistry.class);
        replay(mockRegistry);
        JSONRPCBindingLoader loader = new JSONRPCBindingLoader(mockRegistry);
        assertNotNull(loader);
    }

    public void testGetXMLType() {
        LoaderRegistry mockRegistry = createMock(LoaderRegistry.class);
        replay(mockRegistry);
        JSONRPCBindingLoader loader = new JSONRPCBindingLoader(mockRegistry);
        assertNotNull(loader);
        assertEquals(JSONRPCBindingLoader.BINDING_JSON, loader.getXMLType());
    }

    public void testLoad() {
        LoaderRegistry mockRegistry = createMock(LoaderRegistry.class);
        replay(mockRegistry);
        JSONRPCBindingLoader loader = new JSONRPCBindingLoader(mockRegistry);
        assertNotNull(loader);
        
        CompositeComponent mockParent = createMock(CompositeComponent.class);
        replay(mockParent);
        ModelObject mockModelObject = createMock(ModelObject.class);
        replay(mockModelObject);
        XMLStreamReader mockReader = createMock(XMLStreamReader.class);
        replay(mockReader);
        DeploymentContext mockDeploymentContext = createMock(DeploymentContext.class);
        replay(mockDeploymentContext);
        
        try {
            JSONRPCBindingDefinition jsonBinding = loader.load(mockParent, mockModelObject, mockReader, mockDeploymentContext);
            assertNotNull(jsonBinding);
        } catch (LoaderException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            fail(e.toString());
        } catch (XMLStreamException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            fail(e.toString());
        }
    }

}
