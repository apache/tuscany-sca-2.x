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
package org.apache.tuscany.sca.implementation.das;

import commonj.sdo.DataObject;


/**
 * The service interface of a DAS service provided by DAS components.
 * 
 * @version $Rev$ $Date$
 */
public interface DAS {

    /**
     * Execute a DAS Command specified on the DAS config file.
     * @param commandName The name of the command
     * @return
     */
    DataObject executeCommand(String commandName);
    
    /**Execute a DAS Command specified on the DAS config file, 
     * and narrow the results based on the provided XPath
     * 
     * @param commandName The name of the command
     * @param xPath The xPath filter
     * @return
     */
    DataObject executeCommand(String commandName, String xPath);
    
    

}
