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

package org.apache.tuscany.sca.implementation.data.das;

import java.util.HashMap;
import java.util.Map;

import org.apache.tuscany.das.rdb.ConfigHelper;
import org.apache.tuscany.das.rdb.DAS;
import org.apache.tuscany.sca.implementation.data.config.ConnectionInfo;

/**
 * The DataAccessEngineManager acts like a registry and factory for DAS instances
 * It holds DAS by it's config file name, reusing the same DAS for all components 
 * using the same config file.
 * 
 * @version $Rev$ $Date$
 */
public class DataAccessEngineManager {
    //private final Map<String, DAS> registry = new HashMap<String, DAS>();
    
    public DataAccessEngineManager() {
        super();
    }
    
    
    protected DAS initializeDAS(ConnectionInfo connectionInfo) {
        //load the config file
        System.out.println("Initializing DAS");
        
        ConfigHelper configHelper = new ConfigHelper();
        
        String dataSource = connectionInfo.getDataSource();
        if(dataSource != null && dataSource.length() > 0) {
            configHelper.addConnectionInfo(dataSource);
        } else {
            String driverClass = connectionInfo.getConnectionProperties().getDriverClass();
            String connectionURL = connectionInfo.getConnectionProperties().getDatabaseURL();
            String userName = connectionInfo.getConnectionProperties().getUsername();
            String password = connectionInfo.getConnectionProperties().getPassword();
            int loginTimeout = connectionInfo.getConnectionProperties().getLoginTimeout();

            configHelper.addConnectionInfo(driverClass, connectionURL, userName, password, loginTimeout);
        }
        
        DAS das = DAS.FACTORY.createDAS(configHelper.getConfig());
        
        return das;
    }
    
    public DAS getDAS(ConnectionInfo connectionInfo) {
        assert connectionInfo != null;
        
        //FIXME: cache the das, we need to define the keys to use (datasource and databaseurl + hashed(username + password))
        
        return initializeDAS(connectionInfo);
    }
    

}
