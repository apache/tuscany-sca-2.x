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

package org.apache.tuscany.sca.osgi.service.remoteadmin.impl;

import org.apache.tuscany.sca.node.Node;
import org.apache.tuscany.sca.osgi.service.remoteadmin.EndpointDescription;
import org.apache.tuscany.sca.osgi.service.remoteadmin.ExportRegistration;
import org.osgi.framework.ServiceReference;

/**
 * Implementation of {@link ExportRegistration}
 */
public class ExportRegistrationImpl implements ExportRegistration {
    private Node node;
    private ServiceReference exportedService;
    private EndpointDescription endpointDescription;
    private Throwable exception;

    /**
     * @param exportedService
     * @param endpointDescription
     * @param exception
     */
    public ExportRegistrationImpl(Node node,
                                  ServiceReference exportedService,
                                  EndpointDescription endpointDescription,
                                  Throwable exception) {
        super();
        this.node = node;
        this.exportedService = exportedService;
        this.endpointDescription = endpointDescription;
        this.exception = exception;
    }

    /**
     * @param exportedService
     * @param endpointDescription
     */
    public ExportRegistrationImpl(Node node, ServiceReference exportedService, EndpointDescription endpointDescription) {
        this(node, exportedService, endpointDescription, null);
    }

    /**
     * @see org.apache.tuscany.sca.osgi.service.remoteadmin.ExportRegistration#close()
     */
    public void close() {
        if (node != null) {
            node.stop();
            node = null;
        }
        exception = null;
        endpointDescription = null;
        exportedService = null;
    }

    public ServiceReference getExportedService() {
        return exportedService;
    }

    public EndpointDescription getEndpointDescription() {
        return endpointDescription;
    }

    public Throwable getException() {
        return exception;
    }

    public Node getNode() {
        return node;
    }

}