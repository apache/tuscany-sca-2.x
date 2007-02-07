package org.apache.tuscany.core.builder;

import java.net.URI;

import org.apache.tuscany.spi.builder.WiringException;

/**
 * Indicates the target component of a reference was not found
 *
 * @version $Rev$ $Date$
 */
public class TargetComponentNotFoundException extends WiringException {

    public TargetComponentNotFoundException(String message, URI sourceName, URI targetName) {
        super(message, sourceName, targetName);
    }

}
