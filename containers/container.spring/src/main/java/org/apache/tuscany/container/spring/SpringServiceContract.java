package org.apache.tuscany.container.spring;

import org.apache.tuscany.spi.model.ServiceContract;

/**
 * @version $Rev$ $Date$
 */
public class SpringServiceContract extends ServiceContract {
    public SpringServiceContract() {
    }

    public SpringServiceContract(Class interfaceClass) {
        super(interfaceClass);
    }
}
