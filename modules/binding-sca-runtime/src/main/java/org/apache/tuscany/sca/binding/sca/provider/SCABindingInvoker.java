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

package org.apache.tuscany.sca.binding.sca.provider;

import org.apache.tuscany.sca.databinding.Mediator;
import org.apache.tuscany.sca.interfacedef.Operation;
import org.apache.tuscany.sca.invocation.DataExchangeSemantics;
import org.apache.tuscany.sca.invocation.Interceptor;
import org.apache.tuscany.sca.invocation.InvocationChain;
import org.apache.tuscany.sca.invocation.Invoker;
import org.apache.tuscany.sca.invocation.Message;

/**
 * @version $Rev$ $Date$
 */
public class SCABindingInvoker implements Interceptor, DataExchangeSemantics {
    private InvocationChain chain;
    private Mediator mediator;
    private Operation sourceOperation;
    private Operation targetOperation;
    private boolean copyArgs;
    
    /**
     * Construct a SCABindingInvoker that delegates to the service invocaiton chain
     */
    public SCABindingInvoker(InvocationChain chain, Operation sourceOperation, Mediator mediator) {
        super();
        this.chain = chain;
        this.mediator = mediator;
        this.sourceOperation = sourceOperation;
        this.targetOperation = chain.getTargetOperation();
        initCopyArgs();
    }

    /**
     * @see org.apache.tuscany.sca.invocation.Interceptor#getNext()
     */
    public Invoker getNext() {
        return chain.getHeadInvoker();
    }

    /**
     * @see org.apache.tuscany.sca.invocation.Interceptor#setNext(org.apache.tuscany.sca.invocation.Invoker)
     */
    public void setNext(Invoker next) {
        // NOOP
    }

    /**
     * @see org.apache.tuscany.sca.invocation.Invoker#invoke(org.apache.tuscany.sca.invocation.Message)
     */
    public Message invoke(Message msg) {

        if (copyArgs) {
            msg.setBody(mediator.copyInput(msg.getBody(), sourceOperation, targetOperation));
        }
        
        Message resultMsg = getNext().invoke(msg);
        
        if (copyArgs) {
            // Note source and target operation swapped so result is in source class loader
            if (resultMsg.isFault()) {
                resultMsg.setFaultBody(mediator.copyFault(resultMsg.getBody(), targetOperation, sourceOperation));
            } else {
                if (sourceOperation.getOutputType() != null) {
                    resultMsg.setBody(mediator.copyOutput(resultMsg.getBody(), targetOperation, sourceOperation));
                }
            }
        }

        return resultMsg;
    }

    /**
     * Work out if pass-by-value copies or cross classloader copies need to be done
     * - if source and target are in different classloaders
     * - if the interfaces are remotable unless @AllowsPassByReference or 
     *   a data transformation has been done in the chain
     * - what else?
     *    - have a flag to optionally disable copies for individual composite/service/operation
     *      to improve the performance of specific local invocations?
     */
    private void initCopyArgs() {
        this.copyArgs = crossClassLoaders() || isRemotable();
    }

    private boolean crossClassLoaders() {
        // TODO: for now if the operation is remotable the cross classloader copying will 
        // happen automatically but this needs also to check the non-remotable operation classloaders 
        return false;
    }

    /**
     * Pass-by-value copies are required if the interfaces are remotable unless the
     * implementation uses the @AllowsPassByReference annotation.
     */
    protected boolean isRemotable() {
        if (!sourceOperation.getInterface().isRemotable()) {
            return false;
        }
        if (!chain.getTargetOperation().getInterface().isRemotable()) {
            return false;
        }
        return true;
    }
    
    /**
     * @see org.apache.tuscany.sca.invocation.DataExchangeSemantics#allowsPassByReference()
     */
    public boolean allowsPassByReference() {
        return false;
    }

}
