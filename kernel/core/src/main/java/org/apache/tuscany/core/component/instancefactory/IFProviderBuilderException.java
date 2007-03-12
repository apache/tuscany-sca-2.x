package org.apache.tuscany.core.component.instancefactory;

import org.apache.tuscany.api.TuscanyException;

/**
 * The builder exception for IF provider.
 * @author Dell
 *
 */
@SuppressWarnings("serial")
public class IFProviderBuilderException extends TuscanyException {
    
    /**
     * Initializes the message.
     * @param message Initializes the message.
     */
    public IFProviderBuilderException(String message) {
        super(message);
    }
    
    /**
     * Initializes the causer.
     * @param cause Initializes the cause.
     */
    public IFProviderBuilderException(Throwable cause) {
        super(cause);
    }

}
