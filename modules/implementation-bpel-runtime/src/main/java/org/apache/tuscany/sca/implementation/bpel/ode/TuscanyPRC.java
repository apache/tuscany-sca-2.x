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

package org.apache.tuscany.sca.implementation.bpel.ode;

import javax.wsdl.PortType;
import javax.xml.namespace.QName;

import org.apache.ode.bpel.iapi.Endpoint;
import org.apache.ode.bpel.iapi.EndpointReference;
import org.apache.ode.bpel.iapi.PartnerRoleChannel;

/**
 * Tuscany Partner Role Channel for ODE Integration
 * 
 * @version $Rev$ $Date$
 */
public class TuscanyPRC implements PartnerRoleChannel {
    private final QName 	processName;
    private final QName		pid;
    private final Endpoint	endpoint;
    
    public TuscanyPRC(QName processName, QName pid, PortType portType, Endpoint endpoint){
        this.processName 	= processName;
        this.pid 			= pid;
        this.endpoint		= endpoint;
    }
    
    public QName getProcessName() {
        return this.processName;
    }
    
    public Endpoint getEndpoint() {
    	return this.endpoint;
    }

    public void close() {

    }
    
    public EndpointReference getInitialEndpointReference() {
    	
    	return new ODEEndpointReference( this.endpoint );
    } // end method getInitialEndpointReference

}
