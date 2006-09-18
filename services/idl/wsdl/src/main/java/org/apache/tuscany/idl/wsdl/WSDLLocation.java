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
package org.apache.tuscany.idl.wsdl;

import java.net.URL;

/**
 * A location where the WSDL for a namespace can be found.
 *
 * @version $Rev$ $Date$
 */
public class WSDLLocation {
    private final String namespace;
    private final URL location;

    /**
     * Constructor specifying a namespace and where its WSDL can be found.
     *
     * @param namespace the target namespace
     * @param location  the location of the WSDL
     */
    public WSDLLocation(String namespace, URL location) {
        this.namespace = namespace;
        this.location = location;
    }

    /**
     * Returns the target namespace.
     *
     * @return the target namespace
     */
    public String getNamespace() {
        return namespace;
    }

    /**
     * Returns the location where the WSDL definition can be found.
     *
     * @return the location where the WSDL definition can be found
     */
    public URL getLocation() {
        return location;
    }
}
