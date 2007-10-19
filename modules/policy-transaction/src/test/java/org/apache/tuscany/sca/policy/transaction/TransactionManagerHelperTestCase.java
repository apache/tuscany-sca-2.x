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

package org.apache.tuscany.sca.policy.transaction;

import java.util.logging.Logger;

import javax.transaction.Transaction;
import javax.transaction.xa.XAException;
import javax.transaction.xa.XAResource;
import javax.transaction.xa.Xid;

import junit.framework.TestCase;

import org.apache.geronimo.transaction.GeronimoUserTransaction;
import org.apache.geronimo.transaction.manager.GeronimoTransactionManager;

/**
 * @version $Rev$ $Date$
 */
public class TransactionManagerHelperTestCase extends TestCase {
    private static final Logger logger = Logger.getLogger(TransactionManagerHelper.class.getName());

    public static class MockXAResource implements XAResource {
        private String id;
        private int timeout = 1000;

        public MockXAResource(String id) {
            super();
            this.id = id;
        }

        public void commit(Xid xid, boolean onePhase) throws XAException {
            logger.info(id + ": commit(" + xid + "," + onePhase + ")");
        }

        public void end(Xid xid, int flags) throws XAException {
            logger.info(id + ": end(" + xid + "," + toString(flags) + ")");
        }

        public void forget(Xid xid) throws XAException {
            logger.info(id + ": forget(" + xid + ")");
        }

        public int getTransactionTimeout() throws XAException {
            return timeout;
        }

        public boolean isSameRM(XAResource xares) throws XAException {
            return xares instanceof MockXAResource;
        }

        public int prepare(Xid xid) throws XAException {
            logger.info(id + ": prepare(" + xid + ")");
            return XA_OK;
        }

        public Xid[] recover(int flag) throws XAException {
            return null;
        }

        public void rollback(Xid xid) throws XAException {
            logger.info(id + ": rollback(" + xid + ")");
        }

        public boolean setTransactionTimeout(int seconds) throws XAException {
            this.timeout = seconds;
            return true;
        }

        public void start(Xid xid, int flags) throws XAException {
            logger.info(id + ": start(" + xid + "," + toString(flags) + ")");
        }

        private String toString(int flags) {
            StringBuffer sb = new StringBuffer();
            if ((flags & TMENDRSCAN) != 0) {
                sb.append("TMENDRSCAN ");
            }
            if ((flags & TMFAIL) != 0) {
                sb.append("TMFAIL ");
            }
            if ((flags & TMJOIN) != 0) {
                sb.append("TMJOIN ");
            }
            if ((flags & TMONEPHASE) != 0) {
                sb.append("TMONEPHASE ");
            }
            if ((flags & TMRESUME) != 0) {
                sb.append("TMRESUME ");
            }
            if ((flags & TMSTARTRSCAN) != 0) {
                sb.append("TMSTARTRSCAN ");
            }
            if ((flags & TMSUCCESS) != 0) {
                sb.append("TMSUCCESS ");
            }
            if ((flags & TMSUSPEND) != 0) {
                sb.append("TMSUSPEND ");
            }
            if (sb.length() == 0) {
                sb.append("TMNOFLAGS");
            } else {
                sb.deleteCharAt(sb.length() - 1);
            }
            return sb.toString();
        }

    }

    public void testHelper() throws Exception {
        GeronimoTransactionManager tm = new GeronimoTransactionManager();
        GeronimoUserTransaction tx = new GeronimoUserTransaction(tm);
        TransactionManagerHelper helper = new TransactionManagerHelper(tm);

        // No TX yet
        assertNull(tm.getTransaction());
        Transaction t1 = helper.managedGlobalTransactionPreInvoke();
        // Should create T1
        assertNotNull(t1);
        // The current TX should be T1
        assertSame(t1, tm.getTransaction());
        tm.getTransaction().enlistResource(new MockXAResource("001"));
        tm.getTransaction().enlistResource(new MockXAResource("002"));

        Transaction suspended = helper.suspendsTransactionPreInvoke();
        // T1 is suspended
        assertSame(t1, suspended);
        // No more active TX
        assertNull(tm.getTransaction());

        Transaction t2 = helper.managedGlobalTransactionPreInvoke();
        assertNotNull(t2);
        // The current TX should be T2
        assertSame(t2, tm.getTransaction());
        tm.getTransaction().enlistResource(new MockXAResource("003"));

        tx.rollback();

        // Skip post
        // helper.managedGlobalTransactionPostInvoke(t2);

        helper.suspendsTransactionPostInvoke(suspended);
        // T1 is not resumed
        assertSame(t1, tm.getTransaction());

        helper.managedGlobalTransactionPostInvoke(t1);
        assertNotNull(tm.getTransaction());
        assertEquals(6, t1.getStatus());
    }
}
