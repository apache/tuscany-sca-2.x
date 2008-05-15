package org.apache.tuscany.sca.interfacedef.wsdl.xml;

import javax.wsdl.extensions.ExtensibilityElement;
import javax.xml.namespace.QName;

/**
 * A class to serve as the extensibility element for BPEL partnerLinkType elements
 * @author Mike Edwards
 *
 * @version $Rev$ $Date$
 */
public class BPELPartnerLinkTypeExt implements ExtensibilityElement {
	
	private QName elementType = null;
	private String linkTypeName = null;
	private String[] roleNames = new String[2];
	private QName[] rolePortTypes = new QName[2];

	// -- methods required by the Extensibility Element interface
	public QName getElementType() {
		return elementType;
	}

	public Boolean getRequired() {
		return true;
	}

	public void setElementType(QName theName ) {
		elementType = theName;
	}

	public void setRequired(Boolean required) {
		// intentionally left blank
	}
	
	// -- other methods
	
	public void setName( String theName ) {
		linkTypeName = theName;
	}
	
	public String getName() {
		return linkTypeName;
	}
	
	public void setRole( int i, String name, QName portType ) {
		if( i > 1 ) return;
		roleNames[i] = name;
		rolePortTypes[i] = portType;
	}

	public String getRoleName( int i ) {
		if( i > 1 ) return null;
		return roleNames[i];
	}
	
	public QName getRolePortType( int i ) {
		if( i > 1 ) return null;
		return rolePortTypes[i];
	}
	
} // end BPELPartnerLinkTypeExt
