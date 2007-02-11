package org.apache.tuscany.spi.component;

/**
 * Denotes an attempt to add a child to a composite component with an illegal name
 *
 * @version $Rev$ $Date$
 */
public class MalformedNameException extends RegistrationException {

    public MalformedNameException(Throwable e) {
        super("Malformed name", e);
    }

}
