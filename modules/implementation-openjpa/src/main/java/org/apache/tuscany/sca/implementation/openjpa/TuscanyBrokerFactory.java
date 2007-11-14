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

package org.apache.tuscany.sca.implementation.openjpa;

import javax.sql.XAConnection;
import javax.transaction.TransactionManager;

import org.apache.derby.jdbc.EmbeddedXADataSource;
import org.apache.openjpa.conf.OpenJPAConfiguration;
import org.apache.openjpa.kernel.AbstractBrokerFactory;
import org.apache.openjpa.kernel.StoreManager;
import org.apache.openjpa.lib.conf.ConfigurationProvider;

public class TuscanyBrokerFactory extends AbstractBrokerFactory {
    protected TuscanyBrokerFactory(OpenJPAConfiguration conf) {
        super(conf);
    }

    private XAConnection xaconn;

    @Override
    protected StoreManager newStoreManager() {
        try {
            if (xaconn == null) {
                if (cp.getProperties().get("dbtype").equals("Derby")) {
                    EmbeddedXADataSource xads = new EmbeddedXADataSource();
                    xads.setDatabaseName((String)cp.getProperties().get("dbname"));
                    xads.setCreateDatabase((String)cp.getProperties().get("dbcreate"));
                    xaconn = xads.getXAConnection();
                }

                System.out.println("------------TuscanyBrokerFactory.newStoreManager....");

            }
            return new TuscanyStoreManager(xaconn);

        } catch (Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex);
        }
    }

    private static TransactionManager tm;
    private static ConfigurationProvider cp;

    public static TuscanyBrokerFactory newInstance(ConfigurationProvider _cp) {
        tm = (TransactionManager)_cp.getProperties().get("TransactionManager");
        TuscanyJDBCConfigurationImpl conf = new TuscanyJDBCConfigurationImpl(tm);
        _cp.setInto(conf);
        cp = _cp;
        return new TuscanyBrokerFactory(conf);
    }
}
