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

import org.osgi.framework.ServiceReference;

/**
 * An Import Registration associates an active proxy service to a remote
 * endpoint. The Import Registration can be used to delete the proxy associated
 * with an endpoint. It is created with the RemoteAdmin.importService method.
 * 
 * @ThreadSafe
 */
public interface ImportRegistration {

    /**
     * Unregister this Import Registration. This must close the connection to
     * the end endpoint unregister the proxy. After this method returns, all
     * other methods must return null. This method has no effect when the
     * service is already unregistered or in the process off.
     */
    public void close();

    /**
     * Exception for any error during the import process. If the Remote Admin
     * for some reasons is unable to create a registration, then it must return
     * a Throwable from this method. In this case, all other methods must return
     * on this interface must thrown an Illegal State Exception. If no error
     * occurred, this method must return null. The error must be set before this
     * Import Registration is returned. Asynchronously occurring errors must be
     * reported to the log.
     * 
     * @return The exception that occurred during the creation of the
     *         registration or null if no exception occurred.
     */
    public Throwable getException();

    /**
     * Answer the associated remote Endpoint Description.
     * 
     * @return A Endpoint Description for the remote endpoint.
     */
    public EndpointDescription getImportedEndpointDescription();

    /**
     * Answer the associated Service Reference for the proxy to the endpoint.
     * 
     * @return A Service Reference to the proxy for the endpoint.
     */
    public ServiceReference getImportedService();

}
