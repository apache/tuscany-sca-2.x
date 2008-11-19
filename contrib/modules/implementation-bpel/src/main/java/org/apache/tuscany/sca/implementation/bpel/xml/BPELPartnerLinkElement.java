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

import javax.xml.namespace.QName;

import javax.wsdl.PortType;

/**
 * Represents a <partnerLink.../> element in a BPEL process
 * - this has attributes:
 *      name
 *      partnerLinkType
 *      myRole
 *      partnerRole
 * - plus zero or more property elements as children
 * 
 * The partnerlink may also be given an SCA Type - either of service or of reference - this must
 * generally be calculated and set on the partnerLink by inspecting the BPEL process
 *
 * @version $Rev$ $Date$
 */
public class BPELPartnerLinkElement {

    private String 	REFERENCE_TYPE = "reference";
    private String 	SERVICE_TYPE = "service";
    private String 	name;
    private QName	partnerLinkType;
    private BPELPartnerLinkTypeElement 	pLinkType = null;
    private String 	myRole;
    private String 	partnerRole;
    private String 	scaName;			// Holds the SCA reference or service name
    private String 	scaType = null; 	// Holds the SCA type = null | service | reference
    
    public BPELPartnerLinkElement(String name, 
    							  QName partnerLinkType,
    							  String myRole, 
    							  String partnerRole ) {
        this.name = name;
        this.partnerLinkType = partnerLinkType;
        this.myRole = myRole;
        this.partnerRole = partnerRole;
        
        scaName = null;
    }

    public QName getPartnerLinkType() {
        return partnerLinkType;
    }
    
    public void setPartnerLinkType( BPELPartnerLinkTypeElement pLinkType ) {
    	this.pLinkType = pLinkType;
    }
    
    
    public PortType getMyRolePortType() {
    	return getRolePortType( myRole );
    }
    
    public PortType getPartnerRolePortType() {
    	return getRolePortType( partnerRole );
    }
    
    private PortType getRolePortType( String theRole ) {
    	if (theRole == null || theRole.length() == 0) {
            return null;
        } // end if
        if (theRole.equals(pLinkType.getRole1Name())) {
            return pLinkType.getRole1pType();
        } else if (theRole.equals(pLinkType.getRole2Name())) {
            return pLinkType.getRole2pType();
        } // end if
        return null;
    } // end getRolePortType

    public String getName() {
        return name;
    }
    
    public String getMyRole() {
    	return myRole;
    }
    
    public String getPartnerRole() {
    	return partnerRole;
    }
    
    public void setSCAName( String name ) {
    	scaName = name;
    }
    
    public String getSCAName() {
    	return scaName;
    }
    
    public boolean isSCATyped() {
    	return ( !(scaType == null) );
    }
    
    public void setAsReference( String name ) {
    	scaType = REFERENCE_TYPE;
    	scaName = name;
    }
    
    public void setAsService( String name ) {
    	scaType = SERVICE_TYPE;
    	scaName = name;
    }
    
    public String querySCAType() {
    	return scaType;
    }

} // end class BPELPartnerLinkElement
