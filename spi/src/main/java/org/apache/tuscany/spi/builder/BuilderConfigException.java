package org.apache.tuscany.spi.builder;

/**
 * Represents an error processing an assembly model
 *
 * @version $Rev: 398248 $ $Date: 2006-04-29 23:02:47 +0100 (Sat, 29 Apr 2006) $
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
