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

import java.lang.reflect.InvocationTargetException;
import java.security.PrivilegedExceptionAction;

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

    public TransactionManager getTransactionManager() {
        return tm;
    }

    public void setTransactionManager(TransactionManager tm) {
        this.tm = tm;
    }

    public void validateOneway(TransactionIntent onewayIntent, TransactionIntent implIntent)
        throws IncompatibleIntentException {
        if (onewayIntent == TransactionIntent.transactedOneWay) {
            if (implIntent != TransactionIntent.managedTransactionGlobal) {
                throw new IncompatibleIntentException(onewayIntent + "<-X->" + implIntent);
            }
        }
    }

    public void validateInbound(TransactionIntent serviceIntent, TransactionIntent implIntent)
        throws IncompatibleIntentException {
        if (serviceIntent == TransactionIntent.propagatesTransacton) {
            if (implIntent != TransactionIntent.managedTransactionGlobal) {
                throw new IncompatibleIntentException(serviceIntent + "<-X->" + implIntent);
            }
        }
    }

    public void validateOutbound(TransactionIntent referenceIntent, TransactionIntent implIntent)
        throws IncompatibleIntentException {
        if (referenceIntent == TransactionIntent.propagatesTransacton) {
            if (implIntent != TransactionIntent.managedTransactionGlobal) {
                throw new IncompatibleIntentException(referenceIntent + "<-X->" + implIntent);
            }
        }
    }

    public <T> T handlesOutbound(TransactionIntent referenceIntent,
                                 TransactionIntent implIntent,
                                 PrivilegedExceptionAction<T> action) throws Exception {

        if (implIntent == null) {
            implIntent = TransactionIntent.noManagedTransaction;
        }

        if (referenceIntent == TransactionIntent.propagatesTransacton) {
            if (implIntent != TransactionIntent.managedTransactionGlobal) {
                throw new IncompatibleIntentException(referenceIntent + "<-X->" + implIntent);
            } else {
                // propagates the current TX
                return run(action);
            }
        } else if (referenceIntent == TransactionIntent.suspendsTransaction) {
            Transaction tx = suspendsTransactionPreInvoke();
            try {
                return run(action);
            } finally {
                suspendsTransactionPostInvoke(tx);
            }
        } else {
            return run(action);
        }
    }

    private <T> T run(PrivilegedExceptionAction<T> action) throws Exception {
        try {
            return action.run();
        } catch (Exception e) {
            throw new InvocationTargetException(e);
        }
    }

    public <T> T handlesInbound(TransactionIntent serviceIntent,
                                TransactionIntent implIntent,
                                PrivilegedExceptionAction<T> action) throws Exception {
        if (serviceIntent == null && implIntent == null) {
            return run(action);
        }

        if (implIntent == null) {
            implIntent = TransactionIntent.noManagedTransaction;
        }

        if (serviceIntent == TransactionIntent.propagatesTransacton) {
            if (implIntent != TransactionIntent.managedTransactionGlobal) {
                throw new IncompatibleIntentException(serviceIntent + "<-X->" + implIntent);
            } else {
                // Make sure a global TX is in place
                Transaction tx = managedGlobalTransactionPreInvoke();
                try {
                    return run(action);
                } finally {
                    managedGlobalTransactionPostInvoke(tx);
                }
            }
        } else if (serviceIntent == TransactionIntent.suspendsTransaction) {
            Transaction tx1 = suspendsTransactionPreInvoke();
            try {
                if (implIntent == TransactionIntent.managedTransactionGlobal) {
                    // Start a new TX
                    Transaction tx2 = managedGlobalTransactionPreInvoke();
                    try {
                        return run(action);
                    } finally {
                        // Commit tx2
                        managedGlobalTransactionPostInvoke(tx2);
                    }
                } else {
                    return run(action);
                }
            } finally {
                suspendsTransactionPostInvoke(tx1);
            }
        } else {
            if (implIntent == TransactionIntent.managedTransactionGlobal) {
                // Start a new TX
                Transaction tx2 = managedGlobalTransactionPreInvoke();
                try {
                    return run(action);
                } finally {
                    // Commit tx2
                    managedGlobalTransactionPostInvoke(tx2);
                }
            } else {
                return run(action);
            }
        }
    }

    public <T> void handlesOneWay(TransactionIntent onewayIntent,
                                  TransactionIntent implIntent,
                                  PrivilegedExceptionAction<T> action) throws Exception {
        if (implIntent == null) {
            implIntent = TransactionIntent.noManagedTransaction;
        }

        if (onewayIntent == null) {
            // Assume transactedOneWay
            run(action);
            return;
        }

        if (onewayIntent == TransactionIntent.transactedOneWay) {
            if (implIntent != TransactionIntent.managedTransactionGlobal) {
                throw new IncompatibleIntentException(onewayIntent + "<-X->" + implIntent);
            } else {
                run(action);
                return;
            }
        } else {
            // TransactionIntent.immediateOneWay
            Transaction tx = suspendsTransactionPreInvoke();
            try {
                run(action);
                return;
            } finally {
                suspendsTransactionPostInvoke(tx);
            }
        }
    }

}
