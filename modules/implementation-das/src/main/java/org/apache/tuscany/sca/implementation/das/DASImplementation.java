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

import org.apache.tuscany.sca.assembly.Implementation;

/**
 * The model representing a sample DAS implementation in an SCA assembly model.
 * 
 * @version $Rev$ $Date$
 */
public interface DASImplementation extends Implementation {

    /**
     * Return the DAS configuration side file
     * 
     * @return the name of the das configuration side file
     */
    public String getConfig();

    /**
     * Sets the DAS configuration side file
     * 
     * @param config The name of the das configuration side file
     */
    public void setConfig(String config);
    
    /**
     * Return the data store type being used
     * @return The data store type
     */
    public String getDataAccessType();
    
    /**
     * Sets the data store type being used
     * @param dataAccessType The data store type in use
     */
    public void setDataAccessType(String dataAccessType);
}
