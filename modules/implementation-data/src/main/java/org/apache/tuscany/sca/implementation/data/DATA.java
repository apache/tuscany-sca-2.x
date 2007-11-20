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
package org.apache.tuscany.sca.implementation.data;

import javax.xml.stream.XMLStreamReader;

/**
 * The service interface of a DAS service provided by DAS components.
 *
 * @version $Rev$ $Date$
 */
public interface DATA {
    
    /**
     * Retrieve the Database table contents
     * If a id is given, the results will be filtered to a matching row
     * @param id The PK that identifies the row on the table
     * @return The row content in XML format
     */
    XMLStreamReader get(String id);
    
    /**
     * Delete the Database table contents
     * If a id is given, only a specific row will be deleted
     * @param id The PK that identifies the row on the table
     * @return The number of rows affected
     */
    int delete(String id);
    
}
