package org.apache.tuscany.core.wire;

import org.apache.tuscany.spi.wire.ProxyCreationException;
import org.apache.tuscany.spi.wire.WireFactory;
import org.apache.tuscany.spi.wire.WireFactoryInitException;
import org.apache.tuscany.spi.wire.WireConfiguration;
import org.apache.tuscany.spi.context.CompositeContext;

/**
 * Returns an actual implementation instance as opposed to a proxy. Used in cases where proxying may be optimized away.
 * 
 * @version $Rev: 379957 $ $Date: 2006-02-22 14:58:24 -0800 (Wed, 22 Feb 2006) $
 */
public class NullWireFactory implements WireFactory {

    private CompositeContext parentContext;

    private String targetName;

    private Class businessInterface;

    public NullWireFactory(String componentName, CompositeContext parentContext) {
        assert (parentContext != null) : "Parent context was null";
        this.targetName = componentName;
        this.parentContext = parentContext;
    }

    public void initialize(Class businessInterface, WireConfiguration config) throws WireFactoryInitException {
        this.businessInterface = businessInterface;
    }

    public Object createProxy() throws ProxyCreationException {
        return parentContext.getContext(targetName);
    }

    public void initialize() throws WireFactoryInitException {
    }

    public void setBusinessInterface(Class interfaze) {
        businessInterface = interfaze;
    }

    public Class getBusinessInterface() {
        return businessInterface;
    }

    public void addInterface(Class claz) {
        throw new UnsupportedOperationException();
    }

    public Class[] getImplementatedInterfaces() {
        throw new UnsupportedOperationException();
    }

}
