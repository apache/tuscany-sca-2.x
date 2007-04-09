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
import javax.transaction.RollbackException;
import javax.transaction.Status;
import javax.transaction.Synchronization;
import javax.transaction.Transaction;

import junit.framework.TestCase;
import org.apache.geronimo.transaction.manager.XidFactoryImpl;
import org.easymock.EasyMock;

import org.apache.tuscany.runtime.standalone.StandaloneRuntimeInfo;
import org.apache.tuscany.spi.host.ResourceHostRegistry;
import org.apache.tuscany.transaction.geronimo.TestUtils;

/**
 * Sanity checks for the Geronimo Transaction Manager
 *
 * @version $Rev$ $Date$
 */
public class GeronimoTransactionManagerServiceTestCase extends TestCase {
    private GeronimoTransactionManagerService service;

    public void testBeginCommit() throws Exception {
        service.init();
        service.begin();
        Transaction trx = service.getTransaction();
        assertEquals(Status.STATUS_ACTIVE, trx.getStatus());
        service.commit();
        assertEquals(Status.STATUS_NO_TRANSACTION, trx.getStatus());
        service.destroy();
    }

    public void testBeginRollback() throws Exception {
        service.init();
        service.begin();
        Transaction trx = service.getTransaction();
        assertEquals(Status.STATUS_ACTIVE, trx.getStatus());
        service.rollback();
        assertEquals(Status.STATUS_NO_TRANSACTION, trx.getStatus());
        service.destroy();
    }

    public void testBeginRollbackOnly() throws Exception {
        service.init();
        service.begin();
        Transaction trx = service.getTransaction();
        assertEquals(Status.STATUS_ACTIVE, trx.getStatus());
        service.setRollbackOnly();
        assertEquals(Status.STATUS_MARKED_ROLLBACK, trx.getStatus());
        service.rollback();
        assertEquals(Status.STATUS_NO_TRANSACTION, trx.getStatus());
        service.destroy();
    }

    public void testBeginRollbackOnlyBadCommit() throws Exception {
        service.init();
        service.begin();
        Transaction trx = service.getTransaction();
        assertEquals(Status.STATUS_ACTIVE, trx.getStatus());
        service.setRollbackOnly();
        assertEquals(Status.STATUS_MARKED_ROLLBACK, trx.getStatus());
        try {
            service.commit();
            fail();
        } catch (RollbackException e) {
            // expected
        }
    }

    public void testSuspendResume() throws Exception {
        service.begin();
        Transaction trx = service.getTransaction();
        assertEquals(Status.STATUS_ACTIVE, trx.getStatus());
        trx = service.suspend();
        assertNull(service.getTransaction());
        service.resume(trx);
        assertEquals(Status.STATUS_ACTIVE, trx.getStatus());
        service.commit();
        assertEquals(Status.STATUS_NO_TRANSACTION, trx.getStatus());
    }

    public void testSynchronization() throws Exception {
        Synchronization sync = createSynchronization();
        service.init();
        service.getTransactionManager();
        service.begin();
        Transaction trx = service.getTransaction();
        trx.registerSynchronization(sync);
        assertEquals(Status.STATUS_ACTIVE, trx.getStatus());
        service.commit();
        assertEquals(Status.STATUS_NO_TRANSACTION, trx.getStatus());
        service.destroy();
        EasyMock.verify(sync);
    }

    protected void setUp() throws Exception {
        super.setUp();
        TestUtils.cleanupLog();
        ResourceHostRegistry registry = EasyMock.createNiceMock(ResourceHostRegistry.class);
        EasyMock.replay(registry);
        StandaloneRuntimeInfo info = EasyMock.createMock(StandaloneRuntimeInfo.class);
        EasyMock.expect(info.getInstallDirectory()).andReturn(new File("."));
        EasyMock.replay(info);
        GeronimoTransactionLogService logService = new GeronimoTransactionLogService(info, new XidFactoryImpl());
        service = new GeronimoTransactionManagerService(registry, logService);
        service.init();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
        service.destroy();
        TestUtils.cleanupLog();
    }

    private Synchronization createSynchronization() {
        Synchronization sync = EasyMock.createMock(Synchronization.class);
        sync.beforeCompletion();
        sync.afterCompletion(EasyMock.anyInt());
        EasyMock.replay(sync);
        return sync;
    }

}
