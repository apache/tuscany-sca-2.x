package org.apache.tuscany.container.java.model;

import org.apache.tuscany.core.model.PojoComponentType;
import org.apache.tuscany.model.AtomicImplementation;

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
