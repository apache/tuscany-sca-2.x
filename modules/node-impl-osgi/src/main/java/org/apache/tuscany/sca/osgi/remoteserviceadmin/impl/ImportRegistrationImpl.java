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
 * 
 */
public class ImportRegistrationImpl implements ImportRegistration {
    private Node node;
    private ImportReference importReference;
    private Throwable exception;

    /**
     * @param exportedService
     * @param endpointDescription
     * @param exception
     */
    public ImportRegistrationImpl(Node node,
                                  ServiceReference importedService,
                                  EndpointDescription endpointDescription,
                                  Throwable exception) {
        super();
        this.node = node;
        this.importReference = new ImportReferenceImpl(importedService, endpointDescription);
        this.exception = exception;
    }

    /**
     * @param exportedService
     * @param endpointDescription
     */
    public ImportRegistrationImpl(Node node, ServiceReference importedService, EndpointDescription endpointDescription) {
        super();
        this.node = node;
        this.importReference = new ImportReferenceImpl(importedService, endpointDescription);
    }

    /**
     * @see org.osgi.remoteserviceadmin.ImportRegistration#close()
     */
    public void close() {
        if (node != null) {
            node.stop();
            node = null;
        }
        exception = null;
        importReference = null;
    }

    public Throwable getException() {
        return exception;
    }

    public Node getNode() {
        return node;
    }

    public ImportReference getImportReference() {
        return importReference;
    }

}
