/*
 *
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

package org.apache.tuscany.sca.osgi.service.remoteadmin;

import java.util.EventObject;

import org.osgi.framework.Bundle;

/**
 * Provides the event information for a Remote Admin event.
 * 
 * @Immutable
 */
public class RemoteAdminEvent extends EventObject {
    /**
     * A fatal exporting error occurred. The Export Registration has been
     * closed.
     */
    public final static int EXPORT_ERROR = 0x1;

    /**
     * Add an export registration. The Remote Services Admin will call this
     * method when it exports a service. When this service is registered,
     * the Remote Service Admin must notify the listener of all existing
     * Export Registrations.
     */
    public final static int EXPORT_REGISTRATION = 0x2;

    /**
     * Remove an export registration. The Remote Services Admin will call
     * this method when it removes the export of a service.
     */
    public final static int EXPORT_UNREGISTRATION = 0x4;

    /**
     * A problematic situation occurred, the export is still active.
     */
    public final static int EXPORT_WARNING = 0x8;

    /**
     * A fatal importing error occurred. The Import Registration has been
     * closed.
     */
    public final static int IMPORT_ERROR = 0x10;

    /**
     * Add an import registration. The Remote Services Admin will call this
     * method when it imports a service. When this service is registered,
     * the Remote Service Admin must notify the listener of all existing
     * Import Registrations.
     */
    public final static int IMPORT_REGISTRATION = 0x20;

    /**
     * Remove an import registration. The Remote Services Admin will call
     * this method when it removes the import of a service.
     */
    public final static int IMPORT_UNREGISTRATION = 0x40;

    /**
     * A problematic situation occurred, the import is still active.
     */
    public final static int IMPORT_WARNING = 0x80;

    private static final long serialVersionUID = -6562225073284539118L;
    private Throwable exception;
    private ExportRegistration exportRegistration;
    private ImportRegistration importRegistration;
    private int type;

    public RemoteAdminEvent(Bundle source, int type, ExportRegistration registration, Throwable exception) {
        super(source);
        this.type = type;
        this.exportRegistration = registration;
        this.exception = exception;
    }

    public RemoteAdminEvent(Bundle source, int type, ImportRegistration registration, Throwable exception) {
        super(source);
        this.type = type;
        this.importRegistration = registration;
        this.exception = exception;
    }

    /**
     * Returns the exception
     * 
     * @return
     */
    public Throwable getException() {
        return exception;
    }

    /**
     * Returns the exportRegistration
     * 
     * @return
     */
    public ExportRegistration getExportRegistration() {
        return exportRegistration;
    }

    /**
     * Returns the importRegistration
     * 
     * @return
     */
    public ImportRegistration getImportRegistration() {
        return importRegistration;
    }

    /**
     * Returns the source
     * 
     * @return
     */
    public Bundle getSource() {
        return (Bundle)source;
    }

    /**
     * Returns the type
     * 
     * @return
     */
    public int getType() {
        return type;
    }

}
