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

package org.apache.tuscany.databinding.sample;

import org.apache.tuscany.test.SCATestCase;
import org.osoa.sca.CompositeContext;
import org.osoa.sca.CurrentCompositeContext;

/**
 * @version $Rev$ $Date$
 */
public class DataBindingBootStrapTest extends SCATestCase {

    private Client client;
    private Client clientService;

    public void testDataBindingBootstrap() {
        client.call("foo");
        clientService.call("foo");
    }

    protected void setUp() throws Exception {
        addExtension("databinding", getClass().getClassLoader().getResource("META-INF/sca/databinding.scdl"));
        super.setUp();
        CompositeContext context = CurrentCompositeContext.getContext();
        client = context.locateService(Client.class, "Client");
        clientService = context.locateService(Client.class, "ClientService");
    }
}
