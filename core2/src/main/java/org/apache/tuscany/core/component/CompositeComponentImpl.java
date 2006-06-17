package org.apache.tuscany.core.component;

import org.osoa.sca.ServiceUnavailableException;

import org.apache.tuscany.spi.QualifiedName;
import org.apache.tuscany.spi.component.AtomicComponent;
import org.apache.tuscany.spi.component.CompositeComponent;
import org.apache.tuscany.spi.component.Reference;
import org.apache.tuscany.spi.component.SCAObject;
import org.apache.tuscany.spi.component.Service;
import org.apache.tuscany.spi.component.TargetException;
import org.apache.tuscany.spi.wire.WireService;

/**
 * The standard implementation of a composite component. Autowiring is performed by delegating to the parent composite.
 *
 * @version $Rev$ $Date$
 */
public class CompositeComponentImpl<T> extends AbstractCompositeComponent<T> {
    private String uri;

    public CompositeComponentImpl(String name,
                                  CompositeComponent parent,
                                  AutowireComponent autowireContext,
                                  WireService wireService) {
        super(name, parent, autowireContext, wireService);
    }

    public String getURI() {
        return uri;
    }

    public void setURI(String uri) {
        this.uri = uri;
    }

    //FIXME this should be removed and added to an impl of ModuleContext
    public Object locateService(String name) throws ServiceUnavailableException {
        checkInit();
        QualifiedName qName = new QualifiedName(name);
        SCAObject context = children.get(qName.getPartName());
        if (context == null) {
            throw new ServiceUnavailableException("ServiceDefinition not found [" + name + "]");
        }
        try {
            if (context instanceof AtomicComponent) {
                return ((AtomicComponent) context).getServiceInstance(qName.getPortName());
            } else if (context instanceof Service || context instanceof Reference) {
                return context.getServiceInstance();
            } else {
                throw new ServiceUnavailableException("Illegal target type [" + name + "]");
            }
        } catch (TargetException e) {
            e.addContextName(getName());
            throw new ServiceUnavailableException(e);
        }
    }

}
