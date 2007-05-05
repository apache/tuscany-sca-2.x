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
package echo;

import java.lang.reflect.InvocationTargetException;

import org.apache.tuscany.invocation.Interceptor;
import org.apache.tuscany.invocation.InvocationRuntimeException;
import org.apache.tuscany.invocation.Message;

/**
 * Interceptor for the sample echo binding.
 * 
 * @version $Rev$ $Date$
 */
public class EchoBindingInterceptor implements Interceptor {
    private Interceptor next;

    private Object echo(Object[] args) throws InvocationTargetException {
        // echo back the result, a real binding would invoke some API for flowing the request
        return args[0];
    }

    public Message invoke(Message msg) throws InvocationRuntimeException {
        try {
            Object resp = echo((Object[])msg.getBody());
            msg.setBody(resp);
        } catch (InvocationTargetException e) {
            msg.setBodyWithFault(e.getCause());
        } catch (Throwable e) {
            msg.setBodyWithFault(e);
        }
        return msg;
    }  

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    public Interceptor getNext() {
        return next;
    }

    public void setNext(Interceptor next) {
        this.next = next;
    }

    public boolean isOptimizable() {
        return false;
    }

}
