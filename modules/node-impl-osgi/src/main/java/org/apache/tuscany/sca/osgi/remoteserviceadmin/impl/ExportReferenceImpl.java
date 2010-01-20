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

package org.apache.tuscany.sca.osgi.remoteserviceadmin.impl;

import org.apache.tuscany.sca.node.Node;
import org.osgi.framework.ServiceReference;
import org.osgi.service.remoteserviceadmin.EndpointDescription;
import org.osgi.service.remoteserviceadmin.ExportReference;
import org.osgi.service.remoteserviceadmin.ExportRegistration;

/**
 * 
 */
public class ExportReferenceImpl implements ExportReference {
    private Node node;
    private final ServiceReference exportedService;
    private final EndpointDescription endpointDescription;
    private int count;

    /**
     * @param exportedService
     * @param endpointDescription
     */
    public ExportReferenceImpl(Node node, ServiceReference exportedService, EndpointDescription endpointDescription) {
        super();
        this.node = node;
        this.exportedService = exportedService;
        this.endpointDescription = endpointDescription;
    }

    public ServiceReference getExportedService() {
        return exportedService;
    }

    public EndpointDescription getExportedEndpoint() {
        return endpointDescription;
    }
    
    public synchronized ExportRegistration register() {
        count++;
        return new ExportRegistrationImpl(this);
    }

    public synchronized void unregister() {
        if (count > 0) {
            count--;
        }
        if (count == 0) {
            if (node != null) {
                node.stop();
                node = null;
            }
        }
    }

}
