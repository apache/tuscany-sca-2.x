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
package org.apache.tuscany.sca.implementation.bpel.xml;

import org.apache.tuscany.sca.interfacedef.wsdl.WSDLDefinition;

/**
 * Represents an <import.../> element in a BPEL process
 * - this has attributes:
 *      location
 *      importType
 *      namespace
 *
 * @version $Rev$ $Date$
 */
public class BPELImportElement {
	
    private String location;
    private String importType;
    private String namespace;
    private WSDLDefinition theWSDL = null;

    public BPELImportElement(String location, String importType, String namespace) {
        this.location = location;
        this.importType = importType;
        this.namespace = namespace;
    }

    public String getImportType() {
        return importType;
    }

    public String getLocation() {
        return location;
    }

    public String getNamespace() {
        return namespace;
    }

    public void setWSDLDefinition(WSDLDefinition theDefinition) {
        theWSDL = theDefinition;
    }

    public WSDLDefinition getWSDLDefinition() {
        return theWSDL;
    }

} // end class BPELImportElement
