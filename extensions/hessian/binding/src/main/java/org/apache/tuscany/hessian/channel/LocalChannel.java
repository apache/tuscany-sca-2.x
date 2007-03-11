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
package org.apache.tuscany.hessian.channel;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import org.apache.tuscany.spi.wire.Message;
import org.apache.tuscany.spi.wire.MessageImpl;

import com.caucho.hessian.io.HessianInput;
import com.caucho.hessian.io.HessianOutput;
import org.apache.tuscany.hessian.Channel;
import org.apache.tuscany.hessian.InvocationException;
import org.apache.tuscany.hessian.destination.LocalDestination;

/**
 * A channel implementation that uses an in-VM transport
 *
 * @version $Rev$ $Date$
 */
public class LocalChannel implements Channel {
    private LocalDestination destination;

    public LocalChannel(LocalDestination destination) {
        this.destination = destination;
    }

    public Message send(String operation, Class<?> returnType, Message message) throws InvocationException {
        Object payload = message.getBody();
        Object[] args;
        if (payload != null && !payload.getClass().isArray()) {
            args = new Object[]{payload};
        } else {
            args = (Object[]) payload;
        }
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            HessianOutput output = new HessianOutput(baos);
            output.call(operation, args);
            output.close();
            ByteArrayInputStream bas = new ByteArrayInputStream(baos.toByteArray());
            HessianInput input = new HessianInput(bas);

            ByteArrayOutputStream returnOutputStream = new ByteArrayOutputStream();
            HessianOutput returnOutput = new HessianOutput(returnOutputStream);
            destination.invoke(input, returnOutput);
            ByteArrayInputStream returnStream = new ByteArrayInputStream(returnOutputStream.toByteArray());
            HessianInput in = new HessianInput(returnStream);
            // FIXME handle faults
            Object reply = in.readReply(returnType);
            Message msg = new MessageImpl();
            msg.setBody(reply);
            return msg;
        } catch (Throwable e) {
            throw new InvocationException(e);
        }


    }

}
