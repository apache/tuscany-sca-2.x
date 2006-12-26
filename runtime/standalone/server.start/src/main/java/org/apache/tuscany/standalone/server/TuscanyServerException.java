package org.apache.tuscany.standalone.server;

import org.apache.tuscany.api.TuscanyRuntimeException;

/**
 * Exception thrown by the tuscany server during startup and shutdown.
 * 
 * @version $Revisiion$ $Date$
 *
 */

@SuppressWarnings("serial")
public class TuscanyServerException extends TuscanyRuntimeException {

    /**
     * Initializes the cause.
     * @param cause Root cause of the exception.
     */
    public TuscanyServerException(Throwable cause) {
        super(cause);
    }

}
