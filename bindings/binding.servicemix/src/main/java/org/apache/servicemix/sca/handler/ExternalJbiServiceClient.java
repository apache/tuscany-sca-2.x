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
package org.apache.servicemix.sca.handler;

import java.io.ByteArrayOutputStream;
import java.lang.reflect.Method;

import javax.jbi.messaging.DeliveryChannel;
import javax.jbi.messaging.ExchangeStatus;
import javax.jbi.messaging.InOut;
import javax.jbi.messaging.NormalizedMessage;
import javax.xml.bind.JAXBContext;

import org.apache.servicemix.jbi.jaxp.StringSource;
import org.apache.servicemix.sca.ScaServiceUnit;
import org.apache.servicemix.sca.assembly.JbiBinding;
import org.apache.tuscany.model.assembly.ExternalService;

public class ExternalJbiServiceClient {

    private ExternalService externalService;
    
    private JbiBinding jbiBinding;

    private ScaServiceUnit serviceUnit;

    /**
     * Constructs a new ExternalWebServiceClient.
     * 
     * @param externalService
     * @param wsBinding
     */
    public ExternalJbiServiceClient(ExternalService externalService) {
        this.serviceUnit = ScaServiceUnit.getCurrentScaServiceUnit();
        this.externalService = externalService;
        this.jbiBinding = (JbiBinding) this.externalService.getBindings().get(0);
    }

    /**
     * Invoke an operation on the external Web service.
     * 
     * @param method
     * @param args
     * @return
     */
    public Object invoke(Method method, Object[] args) {
        if (args == null || args.length != 1) {
            throw new IllegalStateException("args should have exactly one object");
        }
        try {
            Object payload = args[0];
            Class inputClass = method.getParameterTypes()[0];
            Class outputClass = method.getReturnType();
            JAXBContext context = JAXBContext.newInstance(inputClass, outputClass);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            context.createMarshaller().marshal(payload, baos);
            
            DeliveryChannel channel = serviceUnit.getComponent().getComponentContext().getDeliveryChannel();
            // TODO: in-only case ?
            // TODO: interface based routing ?
            // TODO: explicit endpoint selection ?
            InOut inout = channel.createExchangeFactory().createInOutExchange();
            inout.setService(jbiBinding.getServiceName());
            NormalizedMessage in = inout.createMessage();
            inout.setInMessage(in);
            in.setContent(new StringSource(baos.toString()));
            boolean sent = channel.sendSync(inout);
            // TODO: check for error ?
            NormalizedMessage out = inout.getOutMessage();
            Object response = context.createUnmarshaller().unmarshal(out.getContent());
            inout.setStatus(ExchangeStatus.DONE);
            channel.send(inout);
            return response;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
