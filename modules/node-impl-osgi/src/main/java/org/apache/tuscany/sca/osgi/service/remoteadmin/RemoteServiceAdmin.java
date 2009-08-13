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

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.osgi.framework.ServiceReference;

/**
 * A Remote Service Admin manages the import and export of services. A
 * Distribution Provider can expose a control interface. This interface allows
 * the a remote controller to control the export and import of services. The API
 * allows a remote controller to export a service, to import a service, and find
 * out about the current imports and exports.
 * 
 * @ThreadSafe
 */

public interface RemoteServiceAdmin {
    /**
     * Export a service to an endpoint. The Remote Service Admin must create an
     * endpoint that can be used by other Distrbution Providers to connect to
     * this Remote Service Admin and use the exported service. This method can
     * return null if the service could not be exported.
     * 
     * @param ref The Service Reference to export
     * @return Export Registration that combines the Endpoint Description and
     *         the Service Reference or <code>null</code if the service could
     *         not be exported
     */
    public List<ExportRegistration> exportService(ServiceReference ref);

    /**
     * Export a service to a given endpoint. The Remote Service Admin must
     * create an endpoint from the given description that can be used by other
     * Distrbution Providers to connect to this Remote Service Admin and use the
     * exported service. This method can return null if the service could not be
     * exported because the endpoint could not be implemented by this Remote
     * Service Admin.
     * 
     * @param ref The Service Reference to export
     * @param properties The properties to create a local endpoint that can be
     *            implemented by this Remote Service Admin. If this is null, the
     *            endpoint will be determined by the properties on the service,
     * @see exportService(ServiceReference). The properties are the same as
     *      given for an exported service. They are overlaid over any properties
     *      the service defines
     * @return Export Registration that combines the Endpoint Description and
     *         the Service Reference or <code>null</code if the service could
     *         not be exported
     */
    public List<ExportRegistration> exportService(ServiceReference ref, Map<String, Object> properties);

    /**
     * Answer the currently active Export Registrations.
     * 
     * @return Returns A collection of Export Registrations that are currently
     *         active.
     */
    public Collection<ExportRegistration> getExportedServices();

    /**
     * Answer the currently active Import Registrations.
     * 
     * @return Returns A collection of Import Registrations that are currently
     *         active.
     */
    public Collection<ImportRegistration> getImportedEndpoints();

    /**
     * Import a service from an endpoint. The Remote Service Admin must use the
     * given endpoint to create a proxy. This method can return null if the
     * service could not be imported.
     * 
     * @param endpoint The Endpoint Description to be used for import
     * @return An Import Registration that combines the Endpoint Description and
     *         the Service Reference or <code>null</code if the endpoint could
     *         not be imported
     */
    public ImportRegistration importService(EndpointDescription endpoint);
}
