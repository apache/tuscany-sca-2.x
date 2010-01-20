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
import org.osgi.service.remoteserviceadmin.ImportReference;
import org.osgi.service.remoteserviceadmin.ImportRegistration;

/**
 * Implementation of ImportReference. It keeps a reference count of ImportRegistrations
 */
public class ImportReferenceImpl implements ImportReference {
    private Node node;
    private final ServiceReference importedService;
    private final EndpointDescription endpointDescription;
    private int count = 0;

    /**
     * @param exportedService
     * @param endpointDescription
     */
    public ImportReferenceImpl(Node node, ServiceReference importedService, EndpointDescription endpointDescription) {
        super();
        this.node = node;
        this.importedService = importedService;
        this.endpointDescription = endpointDescription;
    }

    public ServiceReference getImportedService() {
        return importedService;
    }

    public EndpointDescription getImportedEndpoint() {
        return endpointDescription;
    }
    
    public synchronized ImportRegistration register() {
        count++;
        return new ImportRegistrationImpl(this);
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
