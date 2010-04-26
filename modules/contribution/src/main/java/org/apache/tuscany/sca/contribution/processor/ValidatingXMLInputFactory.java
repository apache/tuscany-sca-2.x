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

package org.apache.tuscany.sca.contribution.processor;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;

import org.apache.tuscany.sca.monitor.Monitor;

/**
 * Base marker class for validating XML input factories.
 *
 * @version $Rev$ $Date$
 */
public abstract class ValidatingXMLInputFactory extends XMLInputFactory {
    
    /**
     * Allows the monitor to be set in lieu of the context being passed
     * into the create methods. The base definitions of the create methods 
     * don't allow for this. 
     * 
     * @param reader the XMLStreamReader instance
     * @param monitor the current monitor object
     * 
     * @tuscany.spi.extension.asclient
     */
    public static void setMonitor(XMLStreamReader reader, Monitor monitor) {
        if (reader instanceof ValidatingXMLStreamReader) {
            ((ValidatingXMLStreamReader)reader).setMonitor(monitor);
        }
    }
}
