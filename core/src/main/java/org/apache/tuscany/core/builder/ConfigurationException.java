package org.apache.tuscany.core.builder;

/**
 * Represents an error processing a logical configuration model
 * 
 * @version $Rev$ $Date$
 */
public class ConfigurationException extends BuilderException {

    public ConfigurationException() {
        super();
    }

    public ConfigurationException(String message) {
        super(message);
    }

    public ConfigurationException(String message, Throwable cause) {
        super(message, cause);
    }

    public ConfigurationException(Throwable cause) {
        super(cause);
    }

}
