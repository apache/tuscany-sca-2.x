package org.apache.tuscany.spi.loader;

/**
 * @version $Rev$ $Date$
 */
public class UndefinedReferenceException extends LoaderException {
    public UndefinedReferenceException(String name) {
        super("Reference not found on implementation", name);
    }
}
