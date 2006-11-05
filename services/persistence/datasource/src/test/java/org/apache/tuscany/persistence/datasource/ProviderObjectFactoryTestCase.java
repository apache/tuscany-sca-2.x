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
package org.apache.tuscany.persistence.datasource;

import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.sql.DataSource;

import org.apache.tuscany.spi.ObjectCreationException;
import org.apache.tuscany.spi.ObjectFactory;

import junit.framework.TestCase;
import org.easymock.EasyMock;

/**
 * @version $Rev$ $Date$
 */
public class ProviderObjectFactoryTestCase extends TestCase {

    public void testInstantiation() throws Exception {
        List<Injector> injectors = new ArrayList<Injector>();
        Method m = Provider.class.getMethod("setVal", Integer.TYPE);
        injectors.add(new Injector(m, new MockFactory(1)));
        ProviderObjectFactory factory = new ProviderObjectFactory(Provider.class, injectors);
        DataSource ds = factory.getInstance();
        Provider provider = (Provider) ds;
        assertEquals(1, provider.getVal());
    }

    public void testInitInstantiation() throws Exception {
        List<Injector> injectors = new ArrayList<Injector>();
        Method m = DSProvider.class.getMethod("setVal", Integer.TYPE);
        injectors.add(new Injector(m, new MockFactory(1)));
        ProviderObjectFactory factory = new ProviderObjectFactory(DSProvider.class, injectors);
        assertNotNull(factory.getInstance());

    }

    public static class Provider implements DataSource {
        private int val;

        public Provider() {
        }

        public int getVal() {
            return val;
        }

        public void setVal(int val) {
            this.val = val;
        }

        public Connection getConnection() throws SQLException {
            return null;
        }

        public Connection getConnection(String username, String password) throws SQLException {
            return null;
        }

        public PrintWriter getLogWriter() throws SQLException {
            return null;
        }

        public void setLogWriter(PrintWriter out) throws SQLException {

        }

        public void setLoginTimeout(int seconds) throws SQLException {

        }

        public int getLoginTimeout() throws SQLException {
            return 0;
        }
    }

    public static class DSProvider implements DataSourceProvider {
        private int val;
        private boolean initialized;

        public DSProvider() {
        }

        public int getVal() {
            return val;
        }

        public void setVal(int val) {
            this.val = val;
        }

        public boolean isInitialized() {
            return initialized;
        }

        public void init() {
            if (val != 1) {
                fail();
            }
            initialized = true;
        }


        public void close() {

        }

        public DataSource getDataSource() throws ProviderException {
            if (!initialized) {
                fail();
            }
            return EasyMock.createNiceMock(DataSource.class);
        }
    }

    private class MockFactory implements ObjectFactory<Integer> {
        private Integer instance;

        public MockFactory(Integer instance) {
            this.instance = instance;
        }

        public Integer getInstance() throws ObjectCreationException {
            return instance;
        }
    }


}
