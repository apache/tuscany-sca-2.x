/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.tuscany.sca.implementation.bpel.ode;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.ode.bpel.iapi.BpelEngineException;
import org.apache.ode.bpel.iapi.ContextException;
import org.apache.ode.bpel.iapi.MessageExchangeContext;
import org.apache.ode.bpel.iapi.MyRoleMessageExchange;
import org.apache.ode.bpel.iapi.PartnerRoleMessageExchange;

/**
 * Message Exchange Context information
 * 
 * @version $Rev$ $Date$
 */
public class ODEMessageExchangeContext implements MessageExchangeContext {
    private static final Log __log = LogFactory.getLog(ODEMessageExchangeContext.class);
    
    private EmbeddedODEServer _server;

    public ODEMessageExchangeContext(EmbeddedODEServer _server) {
        this._server = _server;
    }

    public void invokePartner(PartnerRoleMessageExchange partnerRoleMessageExchange) throws ContextException {
        if (__log.isDebugEnabled()) {
            __log.debug("Invoking a partner operation: " + partnerRoleMessageExchange.getOperationName());
        }
        
        ODEExternalService scaService = new ODEExternalService(_server);
        scaService.invoke(partnerRoleMessageExchange);
    }

    public void onAsyncReply(MyRoleMessageExchange myRoleMessageExchange) throws BpelEngineException {
        if (__log.isDebugEnabled()) {
            __log.debug("Processing an async reply from service " + myRoleMessageExchange.getServiceName());
        }
    }
 }
