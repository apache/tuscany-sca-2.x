package org.apache.tuscany.core.context;

import org.apache.tuscany.spi.QualifiedName;
import org.apache.tuscany.spi.wire.WireService;
import org.apache.tuscany.spi.context.AtomicComponent;
import org.apache.tuscany.spi.context.CompositeComponent;
import org.apache.tuscany.spi.context.SCAObject;
import org.apache.tuscany.spi.context.Reference;
import org.apache.tuscany.spi.context.Service;
import org.apache.tuscany.spi.context.TargetException;
import org.osoa.sca.ServiceUnavailableException;

/**
 * The standard implementation of an composite context. Autowiring is performed by delegating to the parent
 * context.
 *
 * @version $Rev: 399348 $ $Date: 2006-05-03 09:33:22 -0700 (Wed, 03 May 2006) $
 */
public class CompositeComponentImpl<T> extends AbstractCompositeComponent<T> {

    public CompositeComponentImpl(String name, CompositeComponent parent, AutowireComponent autowireContext, WireService wireService) {
        super(name, parent, autowireContext,wireService);
    }

    private String uri;

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
                return context.getService();
            } else {
                throw new ServiceUnavailableException("Illegal target type [" + name + "]");
            }
        } catch (TargetException e) {
            e.addContextName(getName());
            throw new ServiceUnavailableException(e);
        }
    }

}
