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

package org.apache.tuscany.sca.implementation.das.provider;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.tuscany.das.rdb.DAS;

/**
 * The DataAccessEngineManager acts like a registry and factory for DAS instances
 * It holds DAS by it's config file name, reusing the same DAS for all components 
 * using the same config file.
 * 
 * @version $Rev$ $Date$
 */
public class DataAccessEngineManager {
    private final Map<String, DAS> registry = new HashMap<String, DAS>();
    
    public DataAccessEngineManager() {
        super();
    }
    
    protected DAS initializeDAS(String config) throws MissingConfigFileException {
        //load the config file
        System.out.println("Initializing DAS");
        DAS das = DAS.FACTORY.createDAS(this.getConfigStream(config));
        
        return das;
    }
    
    protected InputStream getConfigStream(String config) throws MissingConfigFileException{
        InputStream configStream = null;
        
        try {
            configStream = this.getClass().getResourceAsStream(config); 
        } catch (Exception e) {
            throw new MissingConfigFileException(config); 
        }
        
        return configStream;
    }
    
    public DAS getDAS(String config) throws MissingConfigFileException {
        DAS das = registry.get(config);
        if ( das == null) {
            das = this.initializeDAS(config);
            this.registry.put(config, das);
        }
        return das;
    }
    

}
