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
package das;

import java.io.InputStream;
import java.util.Vector;

import commonj.sdo.DataObject;

public interface DASService {

    /**
     * Set DAS configuration file to be used
     * @param configStream
     * @throws DASServiceException
     */
    public void configureService(InputStream configStream) throws DASServiceException;

    /**
     * Execute an existing command. The commands are defined in the DAS Configuration file being used by the service
     * @param commandName Command name as it appears on the DAS Configuration file
     * @param commandArguments Vector with arguments to be used by the command
     * @throws DASServiceException
     * @return
     */
    public DataObject executeCommand(String commandName, Vector commandArguments) throws DASServiceException;

    /**
     * Execute a new command, this can be any arbitrary valid query based on the backend implementation (e.g. SQL Query for DAS RDB)
     * @param adHocQuery A new command to be executed (e.g SQL Query)
     * @param commandArguments Vector with arguments to be used by the command
     * @throws DASServiceException
     * @return
     */
    public DataObject execute(String adHocQuery, Vector commandArguments) throws DASServiceException;

    /**
     * Apply all changes on the graph back to the persistent repository.
     * This would save the changes on the SDO ChangeSummary back to the database
     * Note: Your SDO ojects should have been created with ChangeSummary support
     * @param graphRoot SDO Object with changes to be commited to persistent repository
     * @throws DASServiceException
     */
    public void applyChanges(DataObject graphRoot) throws DASServiceException;
}
