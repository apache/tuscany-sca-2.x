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

import java.util.ArrayList;
import java.util.List;

import jline.SimpleCompletor;

import org.apache.tuscany.sca.assembly.Endpoint;
import org.apache.tuscany.sca.node2.impl.NodeImpl;
import org.apache.tuscany.sca.runtime.EndpointRegistry;
import org.apache.tuscany.sca.shell.Shell;

/**
 * A Completor for available services
 */
public class ServiceCompletor extends SimpleCompletor {

    private Shell shell;

    public ServiceCompletor(Shell shell) {
        super("");
        this.shell = shell;
    }
    
    @Override
    public int complete(final String buffer, final int cursor, final List clist) {
        if (shell.getNode() != null) {
            List<String> services = new ArrayList<String>();
            EndpointRegistry reg = ((NodeImpl)shell.getNode()).getEndpointRegistry();
            for (Endpoint endpoint : reg.getEndpoints()) {
                services.add(endpoint.getComponent().getURI() + "/" + endpoint.getService().getName());
            }
            setCandidateStrings(services.toArray(new String[services.size()]));
        }
       return super.complete(buffer, cursor, clist);
    }
    
}
