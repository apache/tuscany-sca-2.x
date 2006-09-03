package org.apache.tuscany.runtime.webapp;

import org.apache.tuscany.api.TuscanyException;

/**
 * Denotes an invalid path representing a composite in application hierarchy
 *
 * @version $Rev$ $Date$
 */
public class InvalidCompositePath extends TuscanyException {
    public InvalidCompositePath() {
    }

    public InvalidCompositePath(String message) {
        super(message);
    }

    public InvalidCompositePath(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidCompositePath(Throwable cause) {
        super(cause);
    }
}
