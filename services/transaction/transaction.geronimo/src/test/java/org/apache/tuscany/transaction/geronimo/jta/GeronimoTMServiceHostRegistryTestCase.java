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
package org.apache.tuscany.transaction.geronimo.jta;

import java.io.File;
import javax.transaction.TransactionManager;

import junit.framework.TestCase;
import org.apache.geronimo.transaction.manager.XidFactoryImpl;
import org.easymock.EasyMock;

import org.apache.tuscany.runtime.standalone.StandaloneRuntimeInfo;
import org.apache.tuscany.spi.host.ResourceHostRegistry;
import org.apache.tuscany.transaction.geronimo.TestUtils;

/**
 * @version $Rev$ $Date$
 */
public class GeronimoTMServiceHostRegistryTestCase extends TestCase {
    private GeronimoTransactionManagerService service;
    private ResourceHostRegistry registry;

    public void testRegisterUnregister() throws Exception {
        service.init();
        service.destroy();
        EasyMock.verify(registry);
    }

    protected void setUp() throws Exception {
        super.setUp();
        TestUtils.cleanupLog();
        registry = EasyMock.createMock(ResourceHostRegistry.class);
        registry.registerResource(EasyMock.eq(TransactionManager.class), EasyMock.isA(TransactionManager.class));
        registry.unregisterResource(EasyMock.eq(TransactionManager.class));
        EasyMock.replay(registry);
        StandaloneRuntimeInfo info = EasyMock.createMock(StandaloneRuntimeInfo.class);
        EasyMock.expect(info.getInstallDirectory()).andReturn(new File("."));
        EasyMock.replay(info);
        GeronimoTransactionLogService logService = new GeronimoTransactionLogService(info, new XidFactoryImpl());
        service = new GeronimoTransactionManagerService(registry, logService);
    }

    protected void tearDown() throws Exception {
        super.tearDown();
        TestUtils.cleanupLog();
    }


}
