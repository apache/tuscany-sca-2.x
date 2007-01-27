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
package org.apache.tuscany.servicemix;

import java.lang.reflect.InvocationTargetException;

import javax.jbi.messaging.DeliveryChannel;
import javax.jbi.messaging.ExchangeStatus;
import javax.jbi.messaging.InOut;
import javax.jbi.messaging.MessagingException;
import javax.jbi.messaging.NormalizedMessage;
import javax.xml.namespace.QName;

import org.apache.servicemix.jbi.jaxp.StringSource;
import org.apache.servicemix.sca.ScaServiceUnit;
import org.apache.tuscany.spi.extension.TargetInvokerExtension;

/**
 * Invoke a JBI reference.
 */
public class ServiceMixInvoker extends TargetInvokerExtension {

    private QName serviceName;

    private ScaServiceUnit serviceUnit;

    public ServiceMixInvoker(QName serviceName) {
        this.serviceName = serviceName;
        this.serviceUnit = ScaServiceUnit.getCurrentScaServiceUnit();
    }

    public Object invokeTarget(Object payload) throws InvocationTargetException {
        try {
            DeliveryChannel channel = serviceUnit.getComponent().getComponentContext().getDeliveryChannel();

            // TODO: in-only case ?
            // TODO: interface based routing ?
            // TODO: explicit endpoint selection ?

            InOut inout = channel.createExchangeFactory().createInOutExchange();
            inout.setService(serviceName);
            NormalizedMessage in = inout.createMessage();
            inout.setInMessage(in);
            in.setContent(new StringSource(payload.toString()));

            boolean sent = channel.sendSync(inout);
            // TODO: check for error ?

            NormalizedMessage out = inout.getOutMessage();
            Object response = out.getContent();
            inout.setStatus(ExchangeStatus.DONE);
            channel.send(inout);

            return response;

        } catch (MessagingException e) {
            throw new InvocationTargetException(e);
        }
    }

}
