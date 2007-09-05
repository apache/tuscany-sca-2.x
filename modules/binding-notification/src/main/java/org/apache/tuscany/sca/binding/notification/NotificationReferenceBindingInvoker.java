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
package org.apache.tuscany.sca.binding.notification;

import java.io.OutputStream;
import java.util.HashMap;

import org.apache.axiom.om.OMElement;
import org.apache.tuscany.sca.binding.notification.NotificationReferenceBindingProvider.SubscriberInfo;
import org.apache.tuscany.sca.binding.notification.encoding.Constants;
import org.apache.tuscany.sca.binding.notification.util.IOUtils;
import org.apache.tuscany.sca.binding.notification.util.IOUtils.IOUtilsException;
import org.apache.tuscany.sca.binding.notification.util.IOUtils.Writeable;
import org.apache.tuscany.sca.implementation.notification.ImmutableMessage;
import org.apache.tuscany.sca.interfacedef.Operation;
import org.apache.tuscany.sca.invocation.Invoker;
import org.apache.tuscany.sca.invocation.Message;

/**
 * Turns invoke into remote message fan-out
 *
 * @version $Rev$ $Date$
 */
public class NotificationReferenceBindingInvoker implements Invoker {

    private static final Message RESPONSE = new ImmutableMessage();
    private Operation operation;
    
    private NotificationReferenceBindingProvider notificationReferenceBindingProvider;

    public NotificationReferenceBindingInvoker(Operation operation,
                                               NotificationReferenceBindingProvider notificationReferenceBindingProvider) {
        this.operation = operation;
        this.notificationReferenceBindingProvider = notificationReferenceBindingProvider;
    }
    
    public Message invoke(Message msg) {
        Object payload = msg.getBody();
        if (payload == null) {
            throw new RuntimeException("Message body is null");
        }
        Writeable writeable = null;
        String incomingBrokerID = null;
        if (payload.getClass().isArray()) {
            Object[] bodyArray = (Object[])payload;
            if (bodyArray.length == 3) {
                writeable = getWriteableFromByteArray((byte[])bodyArray[1]);
                incomingBrokerID = (String)bodyArray[2];
            }
            else if (bodyArray.length == 1) {
                writeable = getWriteableFromPayload(bodyArray[0]);
            }
            else {
                throw new RuntimeException("Invalid body array size");
            }
        }
        else {
            writeable = getWriteableFromPayload(payload);
        }

        try {
            synchronized(this) {
                for (SubscriberInfo subscriber : notificationReferenceBindingProvider.getSubscribers()) {
                    // check for each subscriber's broker id and skip if equal
                    if (incomingBrokerID != null && subscriber.brokerID != null && incomingBrokerID.equals(subscriber.brokerID)) {
                        continue;
                    }
                    HashMap<String, String> headers = new HashMap<String, String>();
                    headers.put(IOUtils.Notification_Operation, operation.getName());
                    String brokerID = notificationReferenceBindingProvider.getBrokerID();
                    if (brokerID != null) {
                        headers.put(Constants.Broker_ID, brokerID);
                    }
                    IOUtils.sendHttpRequest(subscriber.address, headers, writeable, null);
                }
            }
        } catch(Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Sender caught exception", e);
        }
        return RESPONSE;
    }
    
    private Writeable getWriteableFromPayload(Object payload) throws RuntimeException {
        if (!(payload instanceof OMElement)) {
            throw new RuntimeException("payload not OMElement");
        }
        final OMElement element = (OMElement)payload;
        Writeable writeable = new Writeable() {
            public void write(OutputStream os) throws IOUtilsException {
                try {
                    element.serialize(os);
                    os.flush();
                }
                catch(Exception e) {
                    throw new IOUtilsException(e);
                }
            }
        };
        return writeable;
    }

    private Writeable getWriteableFromByteArray(final byte[] payload) {
        Writeable writeable = new Writeable() {
            public void write(OutputStream os) throws IOUtilsException {
                try {
                    os.write(payload);
                    os.flush();
                }
                catch(Exception e) {
                    throw new IOUtilsException(e);
                }
            }
        };
        return writeable;
    }
}
