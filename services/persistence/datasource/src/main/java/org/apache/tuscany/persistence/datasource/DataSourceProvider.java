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
package org.apache.tuscany.persistence.datasource;

import javax.sql.DataSource;

/**
 * DataSource providers may optionally implement this interface to receive callback notifications
 *
 * @version $Rev$ $Date$
 */
public interface DataSourceProvider {

    /**
     * Signals to the provider to initialize after all parameters have been set
     *
     * @throws ProviderException if an exception occurs during initialization
     */
    void init() throws ProviderException;

    /**
     * Signals to the provider to close any open resources and prepare for shutdown
     *
     * @throws ProviderException if an exception occurs during shutdown
     */
    void close() throws ProviderException;

    /**
     * Returns a new <code>DataSource</code> instance that will be bound into a composite system service tree. Called
     * after initialize.
     *
     * @throws ProviderException if an error occurs creating a new instance
     */
    DataSource getDataSource() throws ProviderException;

}
