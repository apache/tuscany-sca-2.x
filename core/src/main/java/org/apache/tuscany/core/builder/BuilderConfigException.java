package org.apache.tuscany.core.builder;

/**
 * Represents an error processing an assembly model
 * 
 * @version $Rev$ $Date$
 */
public class BuilderConfigException extends BuilderException {

    public BuilderConfigException() {
        super();
    }

    public BuilderConfigException(String message) {
        super(message);
    }

    public BuilderConfigException(String message, Throwable cause) {
        super(message, cause);
    }

    public BuilderConfigException(Throwable cause) {
        super(cause);
    }

}
