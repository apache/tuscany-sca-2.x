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

package org.apache.tuscany.sca.implementation.bpel;

import java.net.URI;
import java.net.URL;

import javax.xml.namespace.QName;

import org.apache.tuscany.sca.assembly.Base;


/**
 * The BPEL process definition.
 *
 * @version $Rev$ $Date$
 */
public interface BPELProcessDefinition extends Base {

    /**
     * Get the BPEL process Name
     */
    QName getName();

    /**
     * Set the BPEL process Name
     * @param processName process QName
     */
    void setName(QName name);

    /**
     * Get BPEL process URI
     * @return URI for the process
     */
    URI getURI();

    /**
     * Set the BPEL process URI
     * @param uri for the process
     */
    void setURI(URI uri);

    /**
     * Get the URL for the process location
     * @return
     */
    URL getLocation();

    /**
     * Set the URL for the process location
     * @param url
     */
    void setLocation(URL location);
}
