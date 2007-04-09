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
package org.apache.tuscany.spi.extension;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.apache.tuscany.spi.deployer.DeploymentContext;
import org.apache.tuscany.spi.loader.LoaderException;
import org.apache.tuscany.spi.loader.LoaderRegistry;
import org.apache.tuscany.spi.model.ModelObject;

import junit.framework.TestCase;
import org.easymock.EasyMock;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.isA;

/**
 * @version $Rev$ $Date$
 */
public class LoaderExtensionTestCase extends TestCase {

    @SuppressWarnings("unchecked")
    public void testRegistrationDeregistration() throws Exception {
        LoaderRegistry registry = EasyMock.createMock(LoaderRegistry.class);
        registry.registerLoader(isA(QName.class), isA(Extension.class));
        expectLastCall();
        registry.unregisterLoader(isA(QName.class), isA(Extension.class));
        expectLastCall();
        EasyMock.replay(registry);
        Extension loader = new Extension(registry);
        loader.start();
        loader.stop();
    }


    private static class Extension extends LoaderExtension {

        public Extension(LoaderRegistry registry) {
            super(registry);
        }

        public QName getXMLType() {
            return new QName("");
        }

        public ModelObject load(
            ModelObject object, XMLStreamReader reader,
            DeploymentContext deploymentContext) throws XMLStreamException, LoaderException {
            throw new AssertionError();
        }
    }
}
