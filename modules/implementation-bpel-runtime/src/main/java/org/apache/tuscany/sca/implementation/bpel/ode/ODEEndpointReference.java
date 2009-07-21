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

import org.apache.ode.bpel.iapi.Endpoint;
import org.apache.ode.bpel.iapi.EndpointReference;
import org.apache.ode.utils.DOMUtils;
import org.apache.tuscany.sca.assembly.Base;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Tuscany implementation of the ODE EndpointReference interface
 *
 */
public class ODEEndpointReference implements EndpointReference {

	
	private     Document doc = DOMUtils.newDocument();
    private 	Element serviceref;
    
    /**
     * Private constructor for the EndpointReference
     */
    private ODEEndpointReference() {
    	super();
    } // end ODEEndpointReference()
    
    /**
     * Add a new <service-ref/> element to the EndpointReference
     */
    private void addServiceRef() {
    	serviceref = doc.createElementNS(EndpointReference.SERVICE_REF_QNAME.getNamespaceURI(),
				 EndpointReference.SERVICE_REF_QNAME.getLocalPart());
    	doc.appendChild(serviceref);
    } // end method addServiceRef()
    
    /**
     * Create an EndpointReference from an Endpoint object
     * @param anEndpoint - the endpoint object
     */
    public ODEEndpointReference( Endpoint anEndpoint ) {
    	this();
    	addServiceRef();
        // If there is an endpoint for this reference (ie the reference is wired) 
    	// then add an element to indicate this
        String eprCount = anEndpoint.portName;
        if( !"0".equals(eprCount) ) {
        	Element eprElement = doc.createElementNS( Base.SCA11_TUSCANY_NS, "EPR");
        	serviceref.appendChild(eprElement);
        } // end if
    	return;
    } // end 
    
    /**
     * Create a new EndpointReference from an existing endpointElement, which is assumed
     * to be of the form:
     * <sref:service-ref>
     * 		<EPR/>
     * </sref:service-ref>
     * 
     * @param endpointElement the endpointElement
     */
    public ODEEndpointReference( Element endpointElement ) {
    	this();
    	if( endpointElement != null ) {
    		// import the service-ref element into this EndpointReference, if the
    		// root element is a <sref:service-ref/>
    		if( endpointElement.getLocalName().equals("service-ref") ) {
    			doc.appendChild( doc.importNode(endpointElement, true) );
    		} // end if
        } // end if
    	return;
    } // end 
	
	public Document toXML() {
		return doc;
	} // end toXML()

}
