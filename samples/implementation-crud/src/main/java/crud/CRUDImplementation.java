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
package crud;

import org.apache.tuscany.assembly.Implementation;

/**
 * The model representing a sample CRUD implementation in an SCA assembly model.
 * The sample CRUD implementation is not a full blown implementation, it only
 * supports a subset of what a component implementation can support: - a single
 * fixed service (as opposed to a list of services typed by different
 * interfaces) - a directory attribute used to specify where a CRUD component is
 * going to persist resources - no references or properties - no policy intents
 * or policy sets
 * 
 * @version $$Rev$$ $$Date: 2007-04-23 19:18:54 -0700 (Mon, 23 Apr
 *          2007) $$
 */
public interface CRUDImplementation extends Implementation {

    /**
     * Returns the directory used by CRUD implementations to persist resources.
     * 
     * @return the directory used to persist resources
     */
    public String getDirectory();

    /**
     * Sets the directory used by CRUD implementations to persist resources.
     * 
     * @param directory the directory used to persist resources
     */
    public void setDirectory(String directory);

}
