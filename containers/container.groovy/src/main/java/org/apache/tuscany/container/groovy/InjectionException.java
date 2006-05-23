package org.apache.tuscany.container.groovy;

import org.apache.tuscany.spi.TuscanyRuntimeException;

/**
 * @version $$Rev$$ $$Date$$
 */
public class InjectionException extends TuscanyRuntimeException {
    public InjectionException() {
    }

    public InjectionException(String string) {
        super(string);
    }

    public InjectionException(String string, Throwable throwable) {
        super(string, throwable);
    }

    public InjectionException(Throwable throwable) {
        super(throwable);
    }
}
