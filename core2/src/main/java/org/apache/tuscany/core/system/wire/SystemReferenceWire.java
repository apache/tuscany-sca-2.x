package org.apache.tuscany.core.system.wire;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Map;

import org.apache.tuscany.spi.QualifiedName;
import org.apache.tuscany.spi.context.TargetException;
import org.apache.tuscany.spi.wire.ReferenceInvocationChain;
import org.apache.tuscany.spi.wire.ReferenceWire;
import org.apache.tuscany.spi.wire.RuntimeWire;

/**
 * The source side of a wire configured to use the {@link org.apache.tuscany.core.system.model.SystemBinding}
 *
 * @version $$Rev$$ $$Date$$
 */
public class SystemReferenceWire<T> implements ReferenceWire<T> {
    private String referenceName;
    private QualifiedName targetName;
    private Class<T> businessInterface;
    private RuntimeWire<T> targetWire;

    public SystemReferenceWire(String referenceName, QualifiedName targetName, Class<T> businessInterface) {
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

    public Map<Method, ReferenceInvocationChain> getInvocationChains() {
        return Collections.emptyMap();
    }

    public void addInvocationChain(Method method, ReferenceInvocationChain chains) {
        throw new UnsupportedOperationException();
    }

    public void addInvocationChains(Map chains) {
        throw new UnsupportedOperationException();
    }

    public void addInterface(Class claz) {
        throw new UnsupportedOperationException();
    }

    public void setTargetWire(RuntimeWire<T> wire) {
        targetWire = wire;
    }

    public boolean isOptimizable() {
        return true;
    }

}
