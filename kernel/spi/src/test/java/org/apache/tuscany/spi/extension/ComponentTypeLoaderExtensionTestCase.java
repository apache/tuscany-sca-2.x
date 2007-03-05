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

import junit.framework.TestCase;
import org.easymock.EasyMock;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.eq;

import org.apache.tuscany.spi.deployer.DeploymentContext;
import org.apache.tuscany.spi.loader.LoaderException;
import org.apache.tuscany.spi.loader.LoaderRegistry;
import org.apache.tuscany.spi.model.Implementation;

/**
 * @version $Rev$ $Date$
 */
public class ComponentTypeLoaderExtensionTestCase extends TestCase {

    public void testRegistrationDeregistration() throws Exception {
        Extension loader = new Extension();
        LoaderRegistry registry = createMock(LoaderRegistry.class);
        registry.registerLoader(eq(Implementation.class), eq(loader));
        registry.unregisterLoader(eq(Implementation.class));
        EasyMock.replay(registry);
        loader.setLoaderRegistry(registry);
        loader.start();
        loader.stop();
    }


    private class Extension extends ComponentTypeLoaderExtension<Implementation> {

        protected Class<Implementation> getImplementationClass() {
            return Implementation.class;
        }

        public void load(Implementation implementation, DeploymentContext context) throws LoaderException {

        }
    }
}
