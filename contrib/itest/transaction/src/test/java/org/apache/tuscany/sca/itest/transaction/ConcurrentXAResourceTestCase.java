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
package org.apache.tuscany.sca.itest.transaction;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Logger;

import javax.sql.XAConnection;
import javax.transaction.Transaction;
import javax.transaction.TransactionManager;

import junit.framework.TestCase;

import org.apache.derby.jdbc.EmbeddedXADataSource;
import org.apache.tuscany.sca.policy.transaction.TransactionManagerHelper;
import org.apache.tuscany.sca.policy.transaction.TransactionManagerWrapper;

public class ConcurrentXAResourceTestCase extends TestCase {
    class TestThread extends Thread {
        private int counter;

        TestThread(int i) {
            counter = i;
        }

        public void run() {
            log.info(this + " running...");

            EmbeddedXADataSource xads = new EmbeddedXADataSource();
            xads.setDatabaseName("target/test" + counter);
            xads.setCreateDatabase("create");
            Connection conn = null;
            try {

                Transaction trans = helper.managedGlobalTransactionPreInvoke();

                XAConnection xaconn = xads.getXAConnection();

                trans.enlistResource(xaconn.getXAResource());

                conn = xaconn.getConnection();
                try {
                    conn.prepareStatement("create table T1(col1 char(100))").execute();
                } catch (SQLException ex) {
                    log.info("table T1 exists");
                }
                conn.prepareStatement("insert into T1 values('kkkkkkkk')").execute();
                ResultSet rs = conn.prepareStatement("select count(*) from T1").executeQuery();
                rs.next();
                log.info(String.valueOf(rs.getInt(1)));

                helper.managedGlobalTransactionPostInvoke(trans, false);

            } catch (Exception ex) {
                ex.printStackTrace();
            } finally {
                try {
                    conn.close();
                } catch (Exception _ex) {
                    _ex.printStackTrace();
                }
            }
        }

    }
    private TransactionManagerWrapper activator;
    private TransactionManagerHelper helper;

    private Logger log = Logger.getLogger(this.getClass().getName());

    public void setUp() throws Exception {
        activator = new TransactionManagerWrapper();
        activator.start();
        TransactionManager tm = activator.getTransactionManager();
        helper = new TransactionManagerHelper(tm);
    }

    public void tearDown() throws Exception {
        activator.stop();

    }

    public void testConcurrent() {
        TestThread[] tts = new TestThread[5];
        for (int i = 0; i < tts.length; i++) {
            tts[i] = new TestThread(i);
            tts[i].start();
            // log.info("one TestThread started...");
        }
        try {
            for (TestThread tt : tts) {
                while (tt != null && tt.isAlive()) {
                    // log.info("wait for...");
                    Thread.sleep(200);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
