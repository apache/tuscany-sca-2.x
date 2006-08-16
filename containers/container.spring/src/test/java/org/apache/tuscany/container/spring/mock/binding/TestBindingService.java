package org.apache.tuscany.container.spring.mock.binding;

import org.apache.tuscany.spi.CoreRuntimeException;
import org.apache.tuscany.spi.component.CompositeComponent;
import org.apache.tuscany.spi.extension.ServiceExtension;
import org.apache.tuscany.spi.wire.WireService;

/**
 * @version $Rev: 431036 $ $Date: 2006-08-12 06:58:50 -0700 (Sat, 12 Aug 2006) $
 */
public class TestBindingService<T> extends ServiceExtension<T> {
    public TestBindingService(String name,
                              Class<T> interfaze,
                              CompositeComponent parent,
                              WireService wireService) throws CoreRuntimeException {
        super(name, interfaze, parent, wireService);
        // do nothing, but this could register with the host environment
    }
}
