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
package org.apache.tuscany.sca.implementation.bpel.ode.provider;

import javax.transaction.TransactionManager;

import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.implementation.bpel.BPELImplementation;
import org.apache.tuscany.sca.implementation.bpel.ode.EmbeddedODEServer;
import org.apache.tuscany.sca.implementation.bpel.ode.GeronimoTxFactory;
import org.apache.tuscany.sca.provider.ImplementationProvider;
import org.apache.tuscany.sca.provider.ImplementationProviderFactory;
import org.apache.tuscany.sca.runtime.RuntimeComponent;
import org.osoa.sca.annotations.Destroy;

/**
 * BPEL Implementation provider factory
 * 
 * We use the provider factory to instantiate a ODE server that is going to be injected in all BPEL components
 * 
 * @version $Rev$ $Date$
 */
public class BPELImplementationProviderFactory implements ImplementationProviderFactory<BPELImplementation> {

    private EmbeddedODEServer odeServer;
    private TransactionManager txMgr;

    /**
     * Default constructor receiving an extension point
     * @param extensionPoints
     */
    public BPELImplementationProviderFactory(ExtensionPointRegistry extensionPoints) {
        GeronimoTxFactory txFactory = new GeronimoTxFactory();
        txMgr = txFactory.getTransactionManager();
        this.odeServer = new EmbeddedODEServer(txMgr);
    }

    /**
     * Creates a new BPEL Implementation and inject the EmbeddedODEServer
     */
    public ImplementationProvider createImplementationProvider(RuntimeComponent component, BPELImplementation implementation) {
        return new BPELImplementationProvider(component, implementation, odeServer, txMgr);
    }
    
    public Class<BPELImplementation> getModelType() {
        return BPELImplementation.class;
    }
    
    @Destroy
    public void destroy() {
        txMgr = null;
    }
}
