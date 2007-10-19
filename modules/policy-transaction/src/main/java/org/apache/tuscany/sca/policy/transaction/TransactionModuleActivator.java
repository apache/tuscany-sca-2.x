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

import javax.transaction.TransactionManager;
import javax.transaction.xa.XAException;

import org.apache.geronimo.transaction.manager.GeronimoTransactionManager;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.core.ModuleActivator;

/**
 * @version $Rev$ $Date$
 */
public class TransactionModuleActivator implements ModuleActivator {
    private TransactionManager transactionManager;
    
    /**
     * @see org.apache.tuscany.sca.core.ModuleActivator#start(org.apache.tuscany.sca.core.ExtensionPointRegistry)
     */
    public void start(ExtensionPointRegistry registry) {
        try {
            this.transactionManager = new GeronimoTransactionManager();
        } catch (XAException e) {
            throw new IllegalStateException(e);
        }
        registry.addExtensionPoint(this.transactionManager);
    }

    /**
     * @see org.apache.tuscany.sca.core.ModuleActivator#stop(org.apache.tuscany.sca.core.ExtensionPointRegistry)
     */
    public void stop(ExtensionPointRegistry registry) {
    }

}
