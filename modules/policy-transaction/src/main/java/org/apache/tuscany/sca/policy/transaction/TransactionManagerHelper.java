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

import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.InvalidTransactionException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;
import javax.transaction.Transaction;
import javax.transaction.TransactionManager;

/**
 * @version $Rev$ $Date$
 */
public class TransactionManagerHelper {
    // private static final Logger logger = Logger.getLogger(TransactionManagerHelper.class.getName());

    private TransactionManager tm;
    public TransactionManagerHelper(TransactionManager tm) {
        super();
        this.tm = tm;
    }

    public Transaction managedGlobalTransactionPreInvoke() throws SystemException, NotSupportedException {
        if (tm.getTransaction() == null) {
            tm.begin();
            return tm.getTransaction();
        }
        return null;
    }

    public void managedGlobalTransactionPostInvoke(Transaction created) throws InvalidTransactionException,
        IllegalStateException, SystemException, SecurityException, HeuristicMixedException, HeuristicRollbackException,
        RollbackException {
        if (created != null) {
            created.commit();
        }
    }

    public void managedLocalTransactionPreInvoke() {
        // 
    }

    public Transaction noManagedTransactionPreInvoke() throws SystemException {
        if (tm.getTransaction() != null) {
            return tm.suspend();
        }
        return null;
    }

    public void noManagedTransactionPostInvoke(Transaction suspended) throws InvalidTransactionException,
        IllegalStateException, SystemException {
        if (suspended != null) {
            tm.resume(suspended);
        }
    }

    public void propgatesTransactionPreInvoke() {
    }

    public Transaction suspendsTransactionPreInvoke() throws SystemException {
        if (tm.getTransaction() != null) {
            return tm.suspend();
        } else {
            return null;
        }
    }

    public void suspendsTransactionPostInvoke(Transaction suspended) throws InvalidTransactionException,
        IllegalStateException, SystemException {
        if (suspended != null) {
            tm.resume(suspended);
        }
    }

    public void transactedOnewayPreInvoke() {
        //
    }

    public void immediateOnewayPreInvoke() {
        // 
    }

    public TransactionManager getTransactionManager() {
        return tm;
    }

    public void setTransactionManager(TransactionManager tm) {
        this.tm = tm;
    }


}
