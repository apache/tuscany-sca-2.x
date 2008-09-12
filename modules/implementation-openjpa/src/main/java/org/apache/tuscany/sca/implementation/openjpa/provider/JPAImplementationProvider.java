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

package org.apache.tuscany.sca.implementation.openjpa.provider;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManagerFactory;
import javax.transaction.TransactionManager;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.openjpa.kernel.Bootstrap;
import org.apache.openjpa.kernel.BrokerFactory;
import org.apache.openjpa.persistence.JPAFacadeHelper;
import org.apache.openjpa.persistence.PersistenceUnitInfoImpl;
import org.apache.openjpa.persistence.PersistenceProductDerivation.ConfigurationProviderImpl;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.implementation.openjpa.JPAImplementation;
import org.apache.tuscany.sca.interfacedef.Operation;
import org.apache.tuscany.sca.invocation.Invoker;
import org.apache.tuscany.sca.provider.ImplementationProvider;
import org.apache.tuscany.sca.runtime.RuntimeComponent;
import org.apache.tuscany.sca.runtime.RuntimeComponentService;

public class JPAImplementationProvider implements ImplementationProvider {
    private RuntimeComponent component;
    private JPAImplementation implementation;
    private EntityManagerFactory emf;
    private TransactionManager tm;

    public JPAImplementationProvider(RuntimeComponent component,
                                         JPAImplementation implementation,
                                         ExtensionPointRegistry extensionPoints) {
        this.component = component;
        this.implementation = implementation;
        tm =
            (TransactionManager)extensionPoints.getExtensionPoint(org.apache.geronimo.transaction.manager.XAWork.class);
        try {
            // tm.begin();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        initEntityManager();
    }

    private void initEntityManager() {
        Map map = new HashMap();
        map.put("TransactionManager", tm);
        PersistenceUnitInfoImpl _info = implementation.getPersistenceUnitInfoImpl();
        _info.fromUserProperties(map);
        ConfigurationProviderImpl cp = new ConfigurationProviderImpl();
        cp.addProperties(_info.toOpenJPAProperties());
        cp.addProperties(implementation.getDataSourceMeta());

        BrokerFactory factory = Bootstrap.newBrokerFactory(cp, null);

        emf = JPAFacadeHelper.toEntityManagerFactory(factory);

    }

    private Log log = LogFactory.getLog(this.getClass());

    public Invoker createInvoker(RuntimeComponentService service, Operation operation) {

        return new JPAInvoker(operation, emf, tm);
    }

    public void start() {
        // TODO Auto-generated method stub

    }

    public void stop() {
        // TODO Auto-generated method stub

    }

    public boolean supportsOneWayInvocation() {
        // TODO Auto-generated method stub
        return false;
    }

}
