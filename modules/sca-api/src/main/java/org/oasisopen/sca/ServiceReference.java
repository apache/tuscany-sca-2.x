/*
 * Copyright(C) OASIS(R) 2005,2009. All Rights Reserved.
 * OASIS trademark, IPR and other policies apply.
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
