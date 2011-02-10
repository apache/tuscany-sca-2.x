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

package org.apache.tuscany.sca.shell.jline;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import jline.SimpleCompletor;

import org.apache.tuscany.sca.assembly.Endpoint;
import org.apache.tuscany.sca.impl.NodeImpl;
import org.apache.tuscany.sca.runtime.EndpointRegistry;
import org.apache.tuscany.sca.runtime.RuntimeComponent;
import org.apache.tuscany.sca.shell.Shell;

/**
 * A Completor for available service operations
 */
public class ServiceOperationCompletor extends SimpleCompletor {

    private Shell shell;
    private static final List<String> EXCLUDED_OPS = Arrays.asList(new String[] {"equals", "getClass",
                                                                                 "getInvocationHandler",
                                                                                 "getProxyClass", "hashCode",
                                                                                 "isProxyClass", "newProxyInstance",
                                                                                 "notify", "notifyAll", "toString",
                                                                                 "wait", "CGLIB$SET_STATIC_CALLBACKS",
                                                                                 "CGLIB$SET_THREAD_CALLBACKS",
                                                                                 "CGLIB$findMethodProxy",
                                                                                 "getCallback", "getCallbacks",
                                                                                 "newInstance", "setCallback",
                                                                                 "setCallbacks"});    
    
    public ServiceOperationCompletor(Shell shell) {
        super("");
        this.shell = shell;
    }
    
    @Override
    public int complete(final String buffer, final int cursor, final List clist) {
        String service = TShellCompletor.lastArg;
        EndpointRegistry reg = ((NodeImpl)shell.getNode()).getEndpointRegistry();
        List<Endpoint> endpoints = reg.findEndpoint(service);
        if (endpoints.size() < 1) {
            return -1;
        }
        String serviceName = null;
        if (service.contains("/")) {
            int i = service.indexOf("/");
            if (i < service.length()-1) {
                serviceName = service.substring(i+1);
            }
        }
        Object proxy = ((RuntimeComponent)endpoints.get(0).getComponent()).getServiceReference(null, serviceName).getService();        
        Method[] ms = proxy.getClass().getMethods();
        List<String> ops = new ArrayList<String>();
        for (Method m : ms) {
            if (!EXCLUDED_OPS.contains(m.getName())) {
                ops.add(m.getName());
            }
        }
        setCandidateStrings(ops.toArray(new String[ops.size()]));
        return super.complete(buffer, cursor, clist);
    }
}
