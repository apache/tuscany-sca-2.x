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

import org.apache.tuscany.das.rdb.Command;
import org.apache.tuscany.das.rdb.DAS;
import org.osoa.sca.annotations.Scope;

import commonj.sdo.DataObject;

@Scope("COMPOSITE")
public class DASServiceImpl implements DASService {

    protected DAS das = null;


    /**
     * Initialize DAS
     * @return
     * @throws DASServiceException
     */
    private void initDAS(InputStream config) throws DASServiceException {
        if(config == null){
            throw new DASServiceException("Missing configuration information");
        }

        if(this.das != null){
            this.das.releaseResources();
            this.das = null;
        }

        this.das = DAS.FACTORY.createDAS(config);
    }

    /**
     * Get a DAS instance based on the configuration
     * @return
     * @throws DASServiceException
     */
    private DAS getDAS() throws DASServiceException {
        if(this.das == null){
             throw new DASServiceException("DAS not initialized. Please provide DAS configuration torugh das.SetConfig");
        }

        return this.das;
    }

    /**
     * Set DAS configuration file to be used
     * @param configStream
     * @throws DASServiceException
     */
    public void configureService(InputStream configStream) throws DASServiceException{
        this.initDAS(configStream);
    }



    /**
     * Execute an existing command. The commands are defined in the DAS Configuration file being used by the service
     * @param commandName Command name as it appears on the DAS Configuration file
     * @param commandArguments Vector with arguments to be used by the command
     * @throws DASServiceException
     * @return
     */
    public DataObject executeCommand(String commandName, Vector commandArguments) throws DASServiceException{
        Command command = this.getDAS().getCommand(commandName);

        if(command == null){
            throw new DASServiceException("Invalid command: " + commandName);
        }

        //check if arguments was passed
        if(commandArguments != null && commandArguments.size() > 0){
            //we need to set the arguments
            int pos=0;
            for(Object argument : commandArguments){
                pos++;
                command.setParameter(pos, argument);
            }
        }

        DataObject root = command.executeQuery();

        return root;
    }

    /**
     * Execute a new command, this can be any arbitrary valid query based on the backend implementation (e.g. SQL Query for DAS RDB)
     * @param newCommand A new command to be executed (e.g SQL Query)
     * @param commandArguments Vector with arguments to be used by the command
     * @throws DASServiceException
     * @return
     */
    public DataObject execute(String adHocQuery, Vector commandArguments) throws DASServiceException {
       Command command = this.getDAS().createCommand(adHocQuery);

        if(command == null){
            throw new DASServiceException("Invalid command: " + adHocQuery);
        }

        //check if arguments was passed
        if(commandArguments != null && commandArguments.size() > 0){
            //we need to set the arguments
            int pos=0;
            for(Object argument : commandArguments){
                pos++;
                command.setParameter(pos, argument);
            }
        }
        DataObject root = command.executeQuery();

        return root;
    }

    /**
     * Apply all changes on the graph back to the persistent repository.
     * This would save the changes on the SDO ChangeSummary back to the database
     * Note: Your SDO ojects should have been created with ChangeSummary support
     * @throws DASServiceException
     * @param graphRoot SDO Object with changes to be commited to persistent repository
     */
    public void applyChanges(DataObject graphRoot) throws DASServiceException{
        // TODO Auto-generated method stub

    }
}
