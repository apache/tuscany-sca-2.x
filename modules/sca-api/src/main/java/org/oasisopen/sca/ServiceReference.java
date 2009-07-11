/*
 * (c) Copyright BEA Systems, Inc., Cape Clear Software, International Business Machines Corp, Interface21, IONA Technologies,
 * Oracle, Primeton Technologies, Progress Software, Red Hat, Rogue Wave Software, SAP AG., Siemens AG., Software AG., Sybase
 * Inc., TIBCO Software Inc., 2005, 2007. All rights reserved.
 * 
 * see http://www.osoa.org/display/Main/Service+Component+Architecture+Specifications
 */
package org.oasisopen.sca;

import java.io.Serializable;


/**
 * A ServiceReference represents a client's perspective of a reference to another service.
 *
 * @version $Rev$ $Date$
 * @param <B> the Java interface associated with this reference
 */
public interface ServiceReference<B> extends Serializable {
    B getService();
    Class<B> getBusinessInterface();    
}
