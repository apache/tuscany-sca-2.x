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
import java.util.List;

import javax.wsdl.PortType;
import javax.xml.namespace.QName;

import org.apache.tuscany.sca.assembly.Base;
import org.apache.tuscany.sca.implementation.bpel.xml.BPELImportElement;
import org.apache.tuscany.sca.implementation.bpel.xml.BPELPartnerLinkElement;
import org.apache.tuscany.sca.interfacedef.wsdl.WSDLInterface;

/**
 * The model representing a BPEL process definition.
 *
 * @version $Rev$ $Date$
 */
public interface BPELProcessDefinition extends Base {

    /**
     * Get the BPEL process Name
     * 
     * @return
     */
    QName getName();

    /**
     * Set the BPEL process Name
     * 
     * @param processName process QName
     */
    void setName(QName name);

    /**
     * Get BPEL process URI
     * 
     * @return URI for the process
     */
    String getURI();

    /**
     * Set the BPEL process URI
     * 
     * @param uri for the process
     */
    void setURI(String uri);

    /**
     * Get the URL for the process location
     * 
     * @return
     */
    String getLocation();

    /**
     * Set the URL for the process location
     * 
     * @param url
     */
    void setLocation(String location);
    
    /**
     * Return the list of PartnerLinks for this process
     * 
     * @return
     */
    List<BPELPartnerLinkElement> getPartnerLinks();
    
    /**
     * Return the list of imports for this process
     * 
     * @return
     */
    List<BPELImportElement> getImports();
    
    /**
     * Return the collection of associated port types
     * 
     * @return
     */
    public List<PortType> getPortTypes() ;
    
    /**
     * Return the collection of associated WSDL interfaces
     * @return
     */
    public List<WSDLInterface> getInterfaces() ;
}
