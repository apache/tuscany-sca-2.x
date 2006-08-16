package org.apache.tuscany.container.spring.model;

import org.apache.tuscany.spi.model.ServiceContract;

/**
 * @version $Rev$ $Date$
 */
public class SpringServiceContract<T> extends ServiceContract<T> {
    public SpringServiceContract() {
    }

    public SpringServiceContract(Class interfaceClass) {
        super(interfaceClass);
    }
}
