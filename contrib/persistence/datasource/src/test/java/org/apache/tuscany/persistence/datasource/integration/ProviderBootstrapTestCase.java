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
package org.apache.tuscany.persistence.datasource.integration;

import java.net.URL;
import javax.sql.DataSource;

import org.apache.tuscany.spi.component.AtomicComponent;

import org.apache.tuscany.persistence.datasource.integration.mock.Provider;
import junit.framework.TestCase;

/**
 * Verifies bootstrapping of a datasource implementation as a system service in an application composite using a mock
 * Provider
 *
 * @version $Rev$ $Date$
 */
public class ProviderBootstrapTestCase extends TestCase {

    public void testBoot() throws Exception {
//        DataSource ds = (DataSource) ((AtomicComponent)component.getSystemChild("TestDS")).getTargetInstance();
//        assertNotNull(ds);
//        assertEquals("value", ((Provider) ds).getTest());
    }

    protected void setUp() throws Exception {
//        URL url = getClass().getResource("/META-INF/sca/dataSource.scdl");
//        addExtension("DataSourceExtension", url);
//        setApplicationSCDL(getClass().getResource("/META-INF/sca/provider.scdl"));
        super.setUp();
    }

}
