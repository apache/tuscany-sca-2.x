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

package org.apache.tuscany.sca.policy.transaction.runtime;

import javax.transaction.TransactionManager;

import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.core.ModuleActivator;
import org.apache.tuscany.sca.core.UtilityExtensionPoint;

/**
 * @version $Rev$ $Date$
 */
public class TransactionModuleActivator implements ModuleActivator {
    private TransactionManagerWrapper wrapper;
    private ExtensionPointRegistry registry;
    
    public TransactionModuleActivator(ExtensionPointRegistry registry) {
        this.registry = registry;
    }

    /**
     * @see org.apache.tuscany.sca.core.ModuleActivator#start(org.apache.tuscany.sca.core.ExtensionPointRegistry)
     */
    public void start() {
        if (registry != null) {
            UtilityExtensionPoint utilities = registry.getExtensionPoint(UtilityExtensionPoint.class);
            TransactionManager transactionManager = utilities.getUtility(TransactionManager.class);
            if (transactionManager != null) {
                // The transaction manage is provided by the hosting environment
//                RuntimeWireProcessorExtensionPoint wireProcessorExtensionPoint =
//                    registry.getExtensionPoint(RuntimeWireProcessorExtensionPoint.class);
//                TransactionManagerHelper helper = new TransactionManagerHelper(transactionManager);
//                wireProcessorExtensionPoint.addWireProcessor(new TransactionRuntimeWireProcessor(helper));
                return;
            }
        }
        try {
            wrapper = new TransactionManagerWrapper();
            wrapper.start();
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
        if (registry != null) {
            UtilityExtensionPoint utilities = registry.getExtensionPoint(UtilityExtensionPoint.class);
            utilities.addUtility(wrapper.getTransactionManager());
//            RuntimeWireProcessorExtensionPoint wireProcessorExtensionPoint =
//                registry.getExtensionPoint(RuntimeWireProcessorExtensionPoint.class);
//            TransactionManagerHelper helper = new TransactionManagerHelper(wrapper.getTransactionManager());
//            wireProcessorExtensionPoint.addWireProcessor(new TransactionRuntimeWireProcessor(helper));
        }
    }

    /**
     * @see org.apache.tuscany.sca.core.ModuleActivator#stop(org.apache.tuscany.sca.core.ExtensionPointRegistry)
     */
    public void stop() {
        try {
            if (wrapper != null) {
                wrapper.stop();
                wrapper = null;
            }
            if (registry != null && wrapper != null) {
                UtilityExtensionPoint utilities = registry.getExtensionPoint(UtilityExtensionPoint.class);
                utilities.removeUtility(wrapper.getTransactionManager());
            }
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

}
