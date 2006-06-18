package org.apache.tuscany.container.java;

import org.apache.tuscany.spi.model.AtomicImplementation;
import org.apache.tuscany.spi.model.PojoComponentType;

/**
 * @version $$Rev$$ $$Date$$
 */
public class JavaImplementation extends AtomicImplementation<PojoComponentType> {
    private Class<?> implementationClass;

    public Class<?> getImplementationClass() {
        return implementationClass;
    }

    public void setImplementationClass(Class<?> implementationClass) {
        this.implementationClass = implementationClass;
    }
}
