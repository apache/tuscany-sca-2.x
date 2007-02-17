package org.apache.tuscany.core.builder;

import java.net.URI;

import org.apache.tuscany.spi.builder.WiringException;

/**
 * Indicates a component was not found during wiring
 *
 * @version $Rev$ $Date$
 */
public class ComponentNotFoundException extends WiringException {

    public ComponentNotFoundException(String message, URI name) {
        super(message, name, name);
    }

}
