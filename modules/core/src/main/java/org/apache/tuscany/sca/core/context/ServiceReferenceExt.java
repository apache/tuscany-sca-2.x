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

package org.apache.tuscany.sca.core.context;

import java.io.Externalizable;
import java.io.IOException;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.apache.tuscany.sca.assembly.EndpointReference;
import org.apache.tuscany.sca.contribution.processor.ContributionReadException;
import org.apache.tuscany.sca.contribution.processor.ContributionWriteException;
import org.apache.tuscany.sca.runtime.RuntimeWire;
import org.oasisopen.sca.ServiceReference;

/**
 * Extended ServiceReference
 */
public interface ServiceReferenceExt<B> extends ServiceReference<B>, Externalizable {
    /**
     * Return the wire that sits behind this service reference
     * @return wire
     */
    RuntimeWire getRuntimeWire(); 
    
    /**
     * Return the EndpointReference that sits behind this service reference
     * @return endpoint reference
     */
    EndpointReference getEndpointReference();

    // TODO - EPR - the the following still required?
    /**
     * TBD
     * 
     * @param callbackID
     */
    void attachCallbackID(Object callbackID);

    /**
     * TBD
     * 
     * @return
     */
    XMLStreamReader getXMLReader();
}
