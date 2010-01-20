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

import org.osgi.service.remoteserviceadmin.ImportReference;
import org.osgi.service.remoteserviceadmin.ImportRegistration;

/**
 * 
 */
public class ImportRegistrationImpl implements ImportRegistration {
    private ImportReferenceImpl importReference;
    private Throwable exception;

    /**
     * @param importReference
     */
    public ImportRegistrationImpl(ImportReferenceImpl importReference) {
        super();
        this.importReference = importReference;
    }

    /**
     * @param exportedService
     * @param endpointDescription
     * @param exception
     */
    public ImportRegistrationImpl(ImportReferenceImpl importReference, Throwable exception) {
        super();
        this.importReference = importReference;
        this.exception = exception;
    }

    /**
     * @see org.osgi.remoteserviceadmin.ImportRegistration#close()
     */
    public void close() {
        if (importReference != null) {
            importReference.unregister();
        }
        exception = null;
        importReference = null;
    }

    public Throwable getException() {
        return exception;
    }

    public ImportReference getImportReference() {
        return importReference;
    }

}
