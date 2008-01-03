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

import javax.transaction.TransactionManager;

import org.apache.openjpa.jdbc.conf.JDBCConfigurationImpl;
import org.apache.openjpa.jdbc.sql.DBDictionary;
import org.apache.openjpa.jdbc.sql.DerbyDictionary;
import org.apache.openjpa.kernel.BrokerImpl;
import org.apache.openjpa.lib.conf.Configurations;
import javax.sql.*;
import org.apache.openjpa.ee.*;
import org.apache.openjpa.kernel.*;

public class TuscanyJDBCConfigurationImpl extends JDBCConfigurationImpl {
    private TransactionManager tm;

    public TuscanyJDBCConfigurationImpl(TransactionManager tm,DataSource _ds) {
        this.tm = tm;
		ds2 = _ds;
    }
    public ManagedRuntime getManagedRuntimeInstance() {
		return new TuscanyManagerRuntime(tm);
	}

    public Object getConnectionFactory() {
        return null;
    }
 
    public DBDictionary getDBDictionaryInstance() {
        DerbyDictionary dd = new DerbyDictionary();
        Configurations.configureInstance(dd, this, "", "");
        return dd;
    }
	private DataSource ds2;
	@Override
	public DataSource getDataSource2(StoreContext ctx) {
	
		return ds2;
	}
}
