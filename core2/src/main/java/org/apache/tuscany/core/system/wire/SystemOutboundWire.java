package org.apache.tuscany.core.system.wire;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Map;

import org.apache.tuscany.spi.QualifiedName;
import org.apache.tuscany.spi.context.TargetException;
import org.apache.tuscany.spi.wire.InboundWire;
import org.apache.tuscany.spi.wire.OutboundInvocationChain;
import org.apache.tuscany.spi.wire.OutboundWire;

/**
 * An outbound wire configured to use the {@link org.apache.tuscany.core.system.model.SystemBinding}. System
 * wires bind directly to their targets without prxying or interposing invocation chains.
 *
 * @version $$Rev$$ $$Date$$
 */
public class SystemOutboundWire<T> implements OutboundWire<T> {
    private String referenceName;
    private QualifiedName targetName;
    private Class<T> businessInterface;
    private InboundWire<T> targetWire;

    public SystemOutboundWire(String referenceName, QualifiedName targetName, Class<T> businessInterface) {
        this.referenceName = referenceName;
        this.targetName = targetName;
        this.businessInterface = businessInterface;
    }

    public String getReferenceName() {
        return referenceName;
    }

    public void setReferenceName(String referenceName) {
        this.referenceName = referenceName;
    }

    public QualifiedName getTargetName() {
        return targetName;
    }

    public void setTargetName(QualifiedName targetName) {
        this.targetName = targetName;
    }

    public T getTargetService() throws TargetException {
        if (targetWire == null) {
            throw new TargetException("No target wire connected to source wire");
        }
        return targetWire.getTargetService();
    }

    public Class<T> getBusinessInterface() {
        return businessInterface;
    }

    public void setBusinessInterface(Class<T> businessInterface) {
        this.businessInterface = businessInterface;
    }

    public Class[] getImplementedInterfaces() {
        return new Class[0];
    }


    public Map<Method, OutboundInvocationChain> getInvocationChains() {
        return Collections.emptyMap();
    }

    public void addInvocationChain(Method method, OutboundInvocationChain chains) {
        throw new UnsupportedOperationException();
    }

    public void addInvocationChains(Map chains) {
        throw new UnsupportedOperationException();
    }

    public void addInterface(Class claz) {
        throw new UnsupportedOperationException();
    }

    public void setTargetWire(InboundWire<T> wire) {
        targetWire = wire;
    }

    public boolean isOptimizable() {
        return true;
    }

}
