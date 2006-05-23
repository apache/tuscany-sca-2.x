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
package org.apache.tuscany.core.bootstrap;

import junit.framework.TestCase;
import org.apache.tuscany.core.system.context.SystemCompositeContext;
import org.apache.tuscany.spi.deployer.Deployer;
import org.apache.tuscany.spi.bootstrap.ContextNames;

/**
 * @version $Rev$ $Date$
 */
public class BootstrapperTestCase extends TestCase {
    private DefaultBootstrapper bootstrapper;

    public void testDeployerBootstrap() {
        SystemCompositeContext<Deployer> context =
                (SystemCompositeContext<Deployer>) bootstrapper.createDeployer(ContextNames.TUSCANY_DEPLOYER, null);
        assertEquals("tuscany.deployer", context.getName());
        // todo this should work
//        Deployer deployer = context.getService();
//        assertNotNull(deployer);
//        Deployer deployer = context.resolveExternalInstance(Deployer.class);
//        assertNotNull(deployer);
    }

    protected void setUp() throws Exception {
        super.setUp();
        bootstrapper = new DefaultBootstrapper();
    }
}
