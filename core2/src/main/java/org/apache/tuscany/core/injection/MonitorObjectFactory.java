package org.apache.tuscany.core.injection;

import org.apache.tuscany.spi.ObjectCreationException;
import org.apache.tuscany.spi.ObjectFactory;
import org.apache.tuscany.spi.monitor.MonitorFactory;

import org.apache.tuscany.core.component.AutowireComponent;

/**
 * An <code>ObjectFactory</code> implementation that creates monitor instances using autowire resolution to a parent
 * component to obtain the <code>MonitorFactory</code>
 *
 * @version $Rev: 411440 $ $Date: 2006-06-03 07:40:55 -0700 (Sat, 03 Jun 2006) $
 */
public class MonitorObjectFactory<T> implements ObjectFactory<T> {
    private final AutowireComponent<?> parent;
    private final Class<T> type;

    public MonitorObjectFactory(AutowireComponent<?> parent, Class<T> type) {
        this.parent = parent;
        this.type = type;
    }

    @SuppressWarnings("unchecked")
    public T getInstance() throws ObjectCreationException {
        MonitorFactory factory = parent.resolveInstance(MonitorFactory.class);
        if (factory == null) {
            throw new ObjectCreationException("Monitor factory not configured");
        }
        return factory.getMonitor(type);
    }
}
