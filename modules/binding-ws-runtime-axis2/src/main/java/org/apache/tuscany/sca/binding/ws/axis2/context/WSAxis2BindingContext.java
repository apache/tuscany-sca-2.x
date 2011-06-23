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
package org.apache.tuscany.sca.binding.ws.axis2.context;

import org.apache.axis2.client.OperationClient;
import org.apache.axis2.context.MessageContext;

/**
 * Context that the WS Axis2 binding puts on the Tuscany wire
 *
 * @version $Rev: 813727 $ $Date: 2009-09-11 10:02:58 +0100 (Fri, 11 Sep 2009) $
 */
public class WSAxis2BindingContext {

    private OperationClient axisOperationClient;
    private MessageContext axisInMessageContext;
    private MessageContext axisOutMessageContext;

    public OperationClient getAxisOperationClient() {
        return axisOperationClient;
    }
    
    public void setAxisOperationClient(OperationClient axisOperationClient) {
        this.axisOperationClient = axisOperationClient;
    }
    
    public MessageContext getAxisInMessageContext() {
        return axisInMessageContext;
    }
    
    public void setAxisInMessageContext(MessageContext axisInMessageContext) {
        this.axisInMessageContext = axisInMessageContext;
    }
    
    public MessageContext getAxisOutMessageContext() {
        return axisOutMessageContext;
    }
    
    public void setAxisOutMessageContext(MessageContext axisOutMessageContext) {
        this.axisOutMessageContext = axisOutMessageContext;
    }
}
