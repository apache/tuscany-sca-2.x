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
package org.apache.tuscany.hessian;

import java.lang.reflect.InvocationTargetException;

import org.apache.tuscany.spi.component.WorkContext;
import org.apache.tuscany.spi.wire.InvocationRuntimeException;
import org.apache.tuscany.spi.wire.Message;
import org.apache.tuscany.spi.wire.TargetInvoker;

/**
 * @version $Rev$ $Date$
 */
public class HessianTargetInvoker implements TargetInvoker {
    private String operation;
    private Channel channel;
    protected WorkContext workContext;

    public HessianTargetInvoker(String operation, Channel channel, WorkContext workContext) {
        this.operation = operation;
        this.workContext = workContext;
        this.channel = channel;
    }

    public boolean isCacheable() {
        return false;
    }

    public void setCacheable(boolean cacheable) {
    }

    public boolean isOptimizable() {
        return false;
    }

    public Message invoke(Message msg) throws InvocationRuntimeException {
        try {
            return channel.send(operation, null, msg);
        } catch (InvocationException e) {
            msg.setBodyWithFault(e);
            return msg;
        }
    }

    public Object clone() throws CloneNotSupportedException {
        try {
            return super.clone();
        } catch (CloneNotSupportedException e) {
            // TargetInvoker extends Cloneable so this should not have been thrown
            throw new AssertionError(e);
        }
    }

    public Object invokeTarget(final Object payload, final short sequence) throws InvocationTargetException {
        return new UnsupportedOperationException();
    }


}
