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

import java.lang.reflect.Method;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.transaction.TransactionManager;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.tuscany.sca.interfacedef.Operation;
import org.apache.tuscany.sca.interfacedef.java.impl.JavaInterfaceUtil;
import org.apache.tuscany.sca.invocation.Invoker;
import org.apache.tuscany.sca.invocation.Message;

public class JPAInvoker implements Invoker {
    private Operation operation;
    private TransactionManager tm;

    public JPAInvoker(Operation operation, EntityManagerFactory emf, TransactionManager tm) {
        this.operation = operation;
        this.tm = tm;
        this.emf = emf;
    }

    private Log log = LogFactory.getLog(this.getClass());
    private EntityManagerFactory emf;

    public Message invoke(Message msg) {
        try {
            tm.begin();
            Method method = JavaInterfaceUtil.findMethod(EntityManager.class, operation);
            Object r = method.invoke(emf.createEntityManager(), (Object[])msg.getBody());
            tm.commit();
            log.info(method);
            msg.setBody(r);
            return msg;
        } catch (Exception ex) {
            throw new RuntimeException(ex);

        }
    }

}
