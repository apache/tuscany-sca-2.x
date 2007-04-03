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
package org.apache.tuscany.container.spring.impl;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.apache.tuscany.spi.component.TargetNotFoundException;
import org.apache.tuscany.spi.wire.InvocationRuntimeException;
import org.apache.tuscany.spi.wire.Message;
import org.apache.tuscany.spi.wire.TargetInvoker;

import org.springframework.beans.BeansException;

/**
 * Dispatches to an operation on a Spring bean. Since Spring manages bean lifecycle and scope through resolution in the
 * target proxy, the invoker can safely cache the target proxy.
 *
 * @version $$Rev$$ $$Date$$
 */
public class SpringInvoker implements TargetInvoker {
    private SpringCompositeComponent component;
    // default to true since Spring handles resolution
    private boolean cacheable = true;
    private String beanName;
    private Method method;
    // caching is thread-safe since Spring handles resolution
    private Object bean;

    public SpringInvoker(String beanName, Method method, SpringCompositeComponent component) {
        this.beanName = beanName;
        this.method = method;
        this.component = component;
    }

    public Object invokeTarget(final Object object, final short sequence) throws InvocationTargetException {
        if (bean == null) {
            try {
                bean = component.getBean(Object.class, beanName);
                if (bean == null) {
                    throw new InvocationTargetException(new TargetNotFoundException(beanName));
                }
            } catch (BeansException e) {
                throw new InvocationTargetException(e);
            }
        }
        try {
            if (object != null && !object.getClass().isArray()) {
                return method.invoke(bean, object);
            } else {
                return method.invoke(bean, (Object[]) object);
            }
        } catch (IllegalAccessException e) {
            throw new InvocationTargetException(e);
        }
    }

    public Message invoke(Message msg) throws InvocationRuntimeException {
        try {
            Object resp = invokeTarget(msg.getBody(), TargetInvoker.NONE);
            msg.setBody(resp);
        } catch (InvocationTargetException e) {
            msg.setBodyWithFault(e.getCause());
        } catch (Throwable e) {
            msg.setBodyWithFault(e);
        }
        return msg;
    }

    public boolean isCacheable() {
        return cacheable;
    }

    public void setCacheable(boolean cacheable) {
        this.cacheable = cacheable;
    }

    public boolean isOptimizable() {
        return false;
    }


    public SpringInvoker clone() throws CloneNotSupportedException {
        SpringInvoker invoker = (SpringInvoker) super.clone();
        invoker.bean = null;
        return invoker;
    }


}
