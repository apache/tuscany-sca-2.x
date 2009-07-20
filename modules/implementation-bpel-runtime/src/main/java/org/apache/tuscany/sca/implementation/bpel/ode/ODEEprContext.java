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

import java.util.Map;

import javax.xml.namespace.QName;

import org.apache.ode.bpel.iapi.EndpointReference;
import org.apache.ode.bpel.iapi.EndpointReferenceContext;
import org.w3c.dom.Element;

/**
 * Implementation of the ODE EndpointReferenceContext interface, used by the ODE BPEL Engine
 * to handle conversions of EndpointReferences at runtime.
 * 
 * An ODE Endpoint reference relates to SCA Reference EndpointReferences (pointers to target
 * services) and to BPEL PartnerLink elements and any associated BPEL process variables with
 * type set to element="sref:service-ref"
 *
 */
public class ODEEprContext implements EndpointReferenceContext {

    /**
     * Converts an endpoint reference from its XML representation to another
     * type of endpoint reference.
     *
     * @param targetType
     * @param sourceEndpoint
     * @return converted EndpointReference, being of targetType
     */
	public EndpointReference convertEndpoint( QName targetType,
			                                  Element sourceEndpoint) {
		// For the present, Tuscany only has one type of EndpointReference, so that the
		// targetType parameter is of no significance.
		return new ODEEndpointReference( sourceEndpoint );
	} // end method convertEndpoint

	public Map getConfigLookup(EndpointReference epr) {
		// TODO Auto-generated method stub
		return null;
	}
	
    /**
     * Resolve an end-point reference from its XML representation. The
     * nature of the representation is determined by the integration
     * layer. The BPEL engine uses this method to reconstruct
     * {@link EndpointReference}  objects that have been persisted in the
     * database via {@link EndpointReference#toXML(javax.xml.transform.Result)}
     * method.
     *
     * @param XML representation of the EPR
     * @return reconstituted EPR object {@link EndpointReference}
     */
	public EndpointReference resolveEndpointReference(Element epr) {
		return new ODEEndpointReference( epr );
	} // end method resolveEndpointReference

} // end class ODEEprContext
